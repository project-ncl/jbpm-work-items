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

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.longrest.util.ProcessUtils;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidMavenDepends;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.jbpm.process.workitem.core.util.service.WidAction;
import org.jbpm.process.workitem.core.util.service.WidAuth;
import org.jbpm.process.workitem.core.util.service.WidService;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Wid(widfile = "LongRunningRestService.wid",
        name = "LongRunningRestService-7.59.0-SNAPSHOT",
        displayName = "LongRunningRestService 7.59.0-SNAPSHOT",
        defaultHandler = "mvel: new org.jbpm.process.longrest.LongRunningRestServiceWorkItemHandler(runtimeManager)",
        category = "long-running-rest-workitem",
        documentation = "",
        parameters = {
                @WidParameter(name = "url", required = true),
                @WidParameter(name = "method", required = true),
                @WidParameter(name = "headers", required = false),
                @WidParameter(name = "template", required = false),
                @WidParameter(name = "cancelUrlJsonPointer", required = false),
                @WidParameter(name = "cancelUrlTemplate", required = false),
                @WidParameter(name = "socketTimeout", required = false),
                @WidParameter(name = "connectTimeout", required = false),
                @WidParameter(name = "connectionRequestTimeout", required = false)
        },
        results = {
                @WidResult(name = "responseCode"),
                @WidResult(name = "result"),
                @WidResult(name = "cancelUrl"),
                @WidResult(name = "error")
        },
        mavenDepends = {
                @WidMavenDepends(group = "${groupId}", artifact = "${artifactId}", version = "${version}")
        },
        serviceInfo = @WidService(category = "REST service", description = "",
                keywords = "rest,long-running",
                action = @WidAction(title = "Long running REST service handler ver. ${version}"),
                authinfo = @WidAuth(required = true, params = {"url"})
        )
)
public class LongRunningRestServiceWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(LongRunningRestServiceWorkItemHandler.class);

    /**
     * Cookies are required for sticky session in cases where cancel request must hit the same node behind the load balancer.
     */
    private static final String COOKIES_KEY = "cookies";

    private final RuntimeManager runtimeManager;

    public LongRunningRestServiceWorkItemHandler(RuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
        logger.debug("Constructing with runtimeManager ...");
        setLogThrownException(false);
    }

    public LongRunningRestServiceWorkItemHandler() {
        logger.debug("Constructing without runtimeManager ...");
        runtimeManager = null;
        setLogThrownException(false);
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {
            RequiredParameterValidator.validate(this.getClass(), workItem);

            long processInstanceId = workItem.getProcessInstanceId();

            RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
            KieSession kieSession = runtimeEngine.getKieSession();
            WorkflowProcessInstance processInstance = (WorkflowProcessInstance) kieSession.getProcessInstance(processInstanceId);
            String containerId = (String) kieSession.getEnvironment().get("deploymentId");
            runtimeManager.disposeRuntimeEngine(runtimeEngine);

            String cancelUrlJsonPointer = ProcessUtils.getParameter(workItem, Constant.CANCEL_URL_JSON_POINTER_VARIABLE, "");
            String cancelUrlTemplate = ProcessUtils.getParameter(workItem, Constant.CANCEL_URL_TEMPLATE_VARIABLE, "");
            String requestUrl = ProcessUtils.getParameter(workItem, "url", "");
            String requestMethod = ProcessUtils.getParameter(workItem, "method", "");
            String requestTemplate = ProcessUtils.getParameter(workItem, "template", "");
            String requestHeaders = ProcessUtils.getParameter(workItem, "headers", "");
            int socketTimeout = ProcessUtils.getParameter(workItem, "socketTimeout", 30000);
            int connectTimeout = ProcessUtils.getParameter(workItem, "connectTimeout", 5000);
            int connectionRequestTimeout = ProcessUtils.getParameter(workItem, "connectionRequestTimeout", 0);

            RemoteInvoker remoteInvoker = new RemoteInvoker(
                    containerId,
                    processInstanceId,
                    socketTimeout,
                    connectTimeout,
                    connectionRequestTimeout);

            try {
                RemoteInvocationResult result = remoteInvoker.invoke(
                        requestMethod,
                        requestUrl,
                        requestTemplate,
                        cancelUrlJsonPointer,
                        cancelUrlTemplate,
                        requestHeaders,
                        (Map<String, String>) processInstance.getVariable(COOKIES_KEY)
                );
                processInstance.setVariable(COOKIES_KEY, result.getResponseCookies());
                if (result.getErrorCause() == null) {
                    completeWorkItem(manager, workItem.getId(), result.getResponseCode(), result.getServiceInvocationResult(), result.getCancelUrl());
                } else {
                    completeWorkItemWithFailedResponse(manager, workItem.getId(), result.getResponseCode(), result.getServiceInvocationResult(), result.getErrorCause());
                }
            } catch (RemoteInvocationException e) {
                String message = MessageFormat.format("Failed to invoke remote service. ProcessInstanceId {0}.", processInstanceId);
                logger.warn(message, e);
                completeWorkItem(manager, workItem.getId(), e);
            } catch (ResponseProcessingException e) {
                String message = MessageFormat.format("Failed to process response. ProcessInstanceId {0}.", processInstanceId);
                logger.warn(message, e);
                completeWorkItem(manager, workItem.getId(), e);
            }
        } catch (Throwable cause) {
            logger.error("Failed to execute workitem handler due to the following error.", cause);
            completeWorkItem(manager, workItem.getId(), cause);
        }
    }

    /**
     * Complete WorkItem and store the http service response.
     * Long running operations are not completed using this handler but via REST api call.
     */
    private void completeWorkItem(
            WorkItemManager manager,
            long workItemId,
            int responseCode,
            Map<String, Object> serviceInvocationResult,
            String cancelUrl) {
        completeWorkItem(
                manager,
                workItemId,
                responseCode,
                serviceInvocationResult,
                cancelUrl,
                null);
    }

    private void completeWorkItem(WorkItemManager manager, long workItemId, Throwable cause) {
        completeWorkItem(
                manager,
                workItemId,
                -1,
                Collections.emptyMap(),
                "",
                cause);
    }

    private void completeWorkItemWithFailedResponse(WorkItemManager manager, long workItemId, int statusCode, Map<String, Object> response, FailedResponseException e) {
        completeWorkItem(
                manager,
                workItemId,
                statusCode,
                response,
                "",
                e);
   }

    private void completeWorkItem(
            WorkItemManager manager,
            long workItemId,
            int responseCode,
            Map<String, Object> serviceInvocationResult,
            String cancelUrl,
            Throwable cause) {
        Map<String, Object> results = new HashMap<>();
        results.put("responseCode", responseCode);
        results.put("result", serviceInvocationResult);
        results.put("cancelUrl", cancelUrl);
        if (cause != null) {
            results.put("error", cause);
        }
        logger.info("Rest service workitem completion result {}.", results);
        manager.completeWorkItem(workItemId, results);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        completeWorkItem(manager, workItem.getId(), new WorkitemAbortedException());
    }
}
