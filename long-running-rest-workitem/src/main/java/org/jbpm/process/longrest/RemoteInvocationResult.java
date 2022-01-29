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

import java.util.Map;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class RemoteInvocationResult {

    private final int responseCode;
    private final Map<String, String> responseCookies;
    private final Map<String, Object> serviceInvocationResult;
    private final String cancelUrl;
    private final FailedResponseException errorCause;

    public RemoteInvocationResult(int responseCode, Map<String, String> responseCookies, Map<String, Object> serviceInvocationResult, String cancelUrl, FailedResponseException errorCause) {
        this.responseCode = responseCode;
        this.responseCookies = responseCookies;
        this.serviceInvocationResult = serviceInvocationResult;
        this.cancelUrl = cancelUrl;
        this.errorCause = errorCause;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Map<String, String> getResponseCookies() {
        return responseCookies;
    }

    public Map<String, Object> getServiceInvocationResult() {
        return serviceInvocationResult;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public FailedResponseException getErrorCause() {
        return errorCause;
    }
}
