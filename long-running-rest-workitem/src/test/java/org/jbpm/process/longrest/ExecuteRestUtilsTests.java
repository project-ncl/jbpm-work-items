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

import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessContext;
import org.mockito.Mockito;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class ExecuteRestUtilsTests {

    @Test
    public void shouldOverrideWithProcessParameters() {
        ExecuteRestConfiguration config = new ExecuteRestConfiguration();
        config.setTaskTimeout(20);
        config.setRequestMethod("GET");
        config.setRequestUrl("URL");
        config.setCancelMethod("PUT");

        ProcessContext context = Mockito.mock(ProcessContext.class);
        Mockito.when(context.getVariable(Mockito.matches("taskTimeout"))).thenReturn(10);
        Mockito.when(context.getVariable(Mockito.matches("retryDelay"))).thenReturn(10);
        Mockito.when(context.getVariable(Mockito.matches("requestMethod"))).thenReturn("POST");
        Mockito.when(context.getVariable(Mockito.matches("requestUrl"))).thenReturn("OLD-URL");

        ExecuteRestConfiguration overridden = ExecuteRestUtils.getExecuteRestConfiguration(context, config);

        Assert.assertEquals((Integer) 20, overridden.getTaskTimeout());
        Assert.assertEquals((Integer) 10, overridden.getRetryDelay());
        Assert.assertEquals("GET", overridden.getRequestMethod());
        Assert.assertEquals("URL", overridden.getRequestUrl());
        Assert.assertEquals("PUT", overridden.getCancelMethod());
    }
}
