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
package org.jbpm.process.longrest.authentication;

import main.java.org.jbpm.process.longrest.authentication.OidcClient;
import org.jbpm.process.longrest.Constant;
import org.junit.Assert;
import org.junit.Test;

public class OidcClientTest {

    @Test
    public void testGetTokenFailsIfEnvVarMissing() {
        // make sure one of the env vars that OidcClient needs is not set
        Assert.assertNull(System.getenv(Constant.SSO_SERVICE_ACCOUNT_SECRET_VARIABLE));

        Assert.assertThrows(RuntimeException.class, OidcClient::getAccessToken);
    }
}
