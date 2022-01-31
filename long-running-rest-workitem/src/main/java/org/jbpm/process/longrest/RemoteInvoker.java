/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.longrest;

import java.io.IOException;
import java.net.HttpCookie;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.apache.commons.text.StringSubstitutor;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jbpm.process.longrest.util.Mapper;
import org.jbpm.process.longrest.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbpm.process.longrest.Constant.CONTAINER_ID_VARIABLE;
import static org.jbpm.process.longrest.Constant.PROCESS_INSTANCE_ID_VARIABLE;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class RemoteInvoker {

    private static final Logger logger = LoggerFactory.getLogger(RemoteInvoker.class);

    private final long processInstanceId;
    private final String containerId;
    private final HttpClient httpClient;

    public RemoteInvoker(
            String containerId,
            long processInstanceId,
            int socketTimeout,
            int connectTimeout,
            int connectionRequestTimeout) {
        this.containerId = containerId;
        this.processInstanceId = processInstanceId;

        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();

        HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                .setDefaultRequestConfig(config);

        httpClient = clientBuilder.build();
    }

    public RemoteInvocationResult invoke(
            String httpMethod,
            String requestUrl,
            String requestTemplate,
            String cancelUrlJsonPointer,
            String cancelUrlTemplate,
            String requestHeaders,
            Map<String, String> cookies) throws RemoteInvocationException, ResponseProcessingException {

        logger.debug("requestTemplate: {}", requestTemplate);

        Map<String, Object> variables = new HashMap<>();
        variables.put(PROCESS_INSTANCE_ID_VARIABLE, processInstanceId);
        variables.put(CONTAINER_ID_VARIABLE, containerId);
        StringSubstitutor sub = new StringSubstitutor(variables, "$(", ")");

        String requestBodyEvaluated;
        if (requestTemplate != null && !requestTemplate.equals("")) {
            requestBodyEvaluated = sub.replace(requestTemplate);
        } else {
            requestBodyEvaluated = "";
        }

        Map<String, String> requestHeadersMap = new HashMap<>();
        // Add cookies to the request
        if (cookies != null) {
            String cookieHeader = cookies.entrySet().stream()
                    .map(c -> c.getKey() + "=" + c.getValue())
                    .collect(Collectors.joining("; "));
            requestHeadersMap.put("Cookie", cookieHeader);
        }
        requestHeadersMap.putAll(Strings.toMap(requestHeaders));
        HttpResponse httpResponse = httpRequest(
                httpMethod,
                requestUrl,
                requestBodyEvaluated,
                requestHeadersMap);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        logger.info("Remote endpoint returned status: {}.", statusCode);

        Map<String, String> responseCookies = getCookies(httpResponse);

        HttpEntity responseEntity = httpResponse.getEntity();

        if (responseEntity == null || responseEntity.getContentLength() == 0L) {
            if (statusCode >= 200 && statusCode < 300) {
                //there is no content and the status is success
                return new RemoteInvocationResult(statusCode, responseCookies, Collections.emptyMap(), "", null);
            } else {
                logger.warn("Remote service responded with error status code {} and reason: {}. ProcessInstanceId {}.", statusCode, httpResponse.getStatusLine().getReasonPhrase(), processInstanceId);
                return new RemoteInvocationResult(statusCode, responseCookies, Collections.emptyMap(), "", new FailedResponseException(httpResponse.getStatusLine().getReasonPhrase(), statusCode));
            }
        } else {
            //process entity body
            String responseString;
            try {
                responseString = EntityUtils.toString(responseEntity, "UTF-8");
                logger.debug("Invocation response: {}", responseString);
            } catch (IOException e) {
                throw new ResponseProcessingException("Cannot read remote entity.", e);
            }
            JsonNode root;
            Map<String, Object> serviceResponse;
            try {
                root = Mapper.getInstance().readTree(responseString);
                if (JsonNodeType.ARRAY.equals(root.getNodeType())) {
                    //convert array to indexed map
                    serviceResponse = new LinkedHashMap<>();
                    Object[] array = Mapper.getInstance().convertValue(root, new TypeReference<Object[]>() {
                    });
                    for (int i = 0; i < array.length; i++) {
                        serviceResponse.put(Integer.toString(i), array[i]);
                    }
                } else {
                    serviceResponse = Mapper.getInstance().convertValue(root, new TypeReference<Map<String, Object>>() {
                    });
                }
            } catch (Exception e) {
                String message = MessageFormat.format("Cannot parse service invocation response. ProcessInstanceId {0}.", processInstanceId);
                throw new ResponseProcessingException(message, e);
            }

            if (statusCode >= 200 && statusCode < 300) {
                String cancelUrl = parseCancelUrl(root, cancelUrlTemplate, cancelUrlJsonPointer);
                return new RemoteInvocationResult(statusCode, responseCookies, serviceResponse, cancelUrl, null);
            } else {
                logger.warn("Remote service responded with error status code {} and reason: {}. ProcessInstanceId {}.", statusCode, httpResponse.getStatusLine().getReasonPhrase(), processInstanceId);
                return new RemoteInvocationResult(statusCode, responseCookies, serviceResponse, "", new FailedResponseException(httpResponse.getStatusLine().getReasonPhrase(), statusCode));
            }
        }
    }

    private HttpResponse httpRequest(
            String httpMethod,
            String requestUrl,
            String jsonContent,
            Map<String, String> requestHeaders) throws RemoteInvocationException {

        RequestBuilder requestBuilder = RequestBuilder.create(httpMethod).setUri(requestUrl);

        if (requestHeaders != null) {
            requestHeaders.forEach((k, v) -> requestBuilder.addHeader(k, v));
        }

        if (jsonContent != null && !jsonContent.equals("")) {
            requestBuilder.setHeader("Content-Type", "application/json");
            StringEntity entity = new StringEntity(jsonContent, ContentType.APPLICATION_JSON);
            requestBuilder.setEntity(entity);
        }

        logger.info("Invoking remote endpoint {} {} Headers: {} Body: {}.", httpMethod, requestUrl, requestHeaders, jsonContent);

        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(requestBuilder.build());
        } catch (IOException e) {
            throw new RemoteInvocationException("Unable to invoke remote endpoint.", e);
        }
        return httpResponse;
    }

    private String parseCancelUrl(
            JsonNode root,
            String cancelUrlTemplate,
            String cancelUrlJsonPointer) throws ResponseProcessingException {
        String cancelUrl = "";
        try {
            if (!Strings.isEmpty(cancelUrlTemplate)) {
                logger.debug("Setting cancel url from template: {}.", cancelUrlTemplate);
                cancelUrl = cancelUrlTemplate;
            } else if (!Strings.isEmpty(cancelUrlJsonPointer)) {
                logger.debug("Setting cancel url from json pointer: {}.", cancelUrlJsonPointer);
                JsonNode cancelUrlNode = root.at(cancelUrlJsonPointer);
                if (!cancelUrlNode.isMissingNode()) {
                    cancelUrl = cancelUrlNode.asText();
                }
            }
            logger.debug("Cancel url: {}.", cancelUrl);
        } catch (Exception e) {
            String message = MessageFormat.format("Cannot read cancel url from service invocation response. ProcessInstanceId {0}.", processInstanceId);
            throw new ResponseProcessingException(message, e);
        }
        return cancelUrl;
    }

    private Map<String, String> getCookies(HttpResponse response) {
        Map<String, String> cookies = new HashMap<>();
        Header[] cookieHeaders = response.getHeaders("Set-Cookie");
        for (Header cookieHeader : cookieHeaders) {
            List<HttpCookie> cookiesInTheHeader = HttpCookie.parse(cookieHeader.getValue());
            Map<String, String> cookiesMap = cookiesInTheHeader.stream()
                    .collect(Collectors.toMap(HttpCookie::getName, HttpCookie::getValue));
            cookies.putAll(cookiesMap);
        }
        return cookies;

    }
}
