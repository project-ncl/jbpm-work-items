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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class MethodInvoker implements Serializable {

    public static final Logger logger = Logger.getLogger(MethodInvoker.class.getName());

    public static String withRestResponse(Serializable objectInstance, String methodName, Map<String, Object> restResponse) {
        if (objectInstance == null || methodName == null) {
            return null;
        }
        try {
            Class<?> objectClass = objectInstance.getClass();
            Method method = objectClass.getMethod(methodName, Map.class);
            Object result = method.invoke(objectInstance, restResponse);
            logger.log(Level.FINE, "Result from withRestResponse: " + result);
            if (result != null) {
                return (String) result;
            } else {
                return null;
            }
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Unable to invoke method. ", e);
            throw new RuntimeException(e);
        }
    }

}
