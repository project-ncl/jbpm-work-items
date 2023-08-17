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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jbpm.process.longrest.demoservices.Service;
import org.jbpm.process.longrest.demoservices.ServiceListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
@RunWith(Parameterized.class)
public class RemoteInvokerTest {

    private static final Logger logger = LoggerFactory.getLogger(RemoteInvokerTest.class);

    private static int PORT = 8080;
    private static String HOST = "localhost";
    private static UndertowJaxrsServer server;
    private static RemoteInvoker remoteInvoker;

    private final String requestPath;
    private final int expectedStatus;
    private Type expectedError;

//    private final ServiceListener serviceListener = new ServiceListener();

    @BeforeClass
    public static void preTestSetup() throws Exception {
        server = new UndertowJaxrsServer();

        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setApplicationClass(JaxRsActivator.class.getName());

        DeploymentInfo deploymentInfo = server.undertowDeployment(deployment, "/");
        deploymentInfo.setClassLoader(RemoteInvokerTest.class.getClassLoader());
        deploymentInfo.setDeploymentName("TestServices");
        deploymentInfo.setContextPath("/");

//        deploymentInfo.addServletContextAttribute(Service.SERVICE_LISTENER_KEY, serviceListener);

        server.deploy(deploymentInfo);
        Undertow.Builder builder = Undertow.builder().addHttpListener(PORT, HOST);
        server.start(builder);

        remoteInvoker = new RemoteInvoker(
                "container-id",
                42,
                2000,
                2000,
                2000,
                false
        );
    }

    @AfterClass
    public static void postTestTeardown() throws Exception {
        logger.info("Stopping http server ...");
        server.stop();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "/", 200, null },
                { "/200-empty", 200, null },
                { "/204", 204, null },
                { "/404", 404, FailedResponseException.class },
                { "/404-empty", 404, FailedResponseException.class }
        });
    }

    public RemoteInvokerTest(String requestPath, int expectedStatus, Type expectedError) {
        this.requestPath = requestPath;
        this.expectedStatus = expectedStatus;
        this.expectedError = expectedError;
    }

    @Test
    public void shouldReturnCorrectStatus() throws ResponseProcessingException, RemoteInvocationException {
        //when
        RemoteInvocationResult result = remoteInvoker.invoke(
                "GET",
                baseUrl() + requestPath,
                "",
                "",
                "",
                "",
                Collections.emptyMap()
        );

        //then
        Assert.assertEquals(expectedStatus, result.getResponseCode());
        Assert.assertTrue(expectedError == null ? result.getErrorCause() == null : result.getErrorCause().getClass().getTypeName().equals(expectedError.getTypeName()));
    }

    private String baseUrl() {
        return "http://" + HOST + ":" + PORT + "/demo-service";
    }
}
