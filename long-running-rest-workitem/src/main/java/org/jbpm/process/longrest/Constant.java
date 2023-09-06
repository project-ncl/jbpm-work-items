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

public class Constant {

    public static final String CANCEL_SIGNAL_TYPE = "CancelAll";

    public static final String HOSTNAME_HTTP = "HOSTNAME_HTTP";

    public static final String HOSTNAME_HTTPS = "HOSTNAME_HTTPS";

    public static final String CANCEL_URL_JSON_POINTER_VARIABLE = "cancelUrlJsonPointer";

    public static final String CANCEL_URL_TEMPLATE_VARIABLE = "cancelUrlTemplate";

    public static final String HEARTBEAT_TIMEOUT_VARIABLE = "heartbeatTimeout";

    public static final String LAST_HEARTBEAT_VARIABLE = "lastHeartbeat";

    public static final String HEARTBEAT_VALIDATION_VARIABLE = "heartbeatValidation";

    public static final String PROCESS_INSTANCE_ID_VARIABLE = "processInstanceId";

    public static final String CONTAINER_ID_VARIABLE = "containerId";

    public static final String SSO_URL_VARIABLE = "SSO_URL";
    public static final String SSO_REALM_VARIABLE = "SSO_REALM";
    public static final String SSO_SERVICE_ACCOUNT_CLIENT_VARIABLE = "SSO_SERVICE_ACCOUNT_CLIENT";
    public static final String SSO_SERVICE_ACCOUNT_SECRET_VARIABLE = "SSO_SERVICE_ACCOUNT_SECRET";

    // Is the OidcClient being tested in a unit test?
    public static final String SSO_OIDC_BEING_TESTED = "SSO_OIDC_BEING_TESTED";
}
