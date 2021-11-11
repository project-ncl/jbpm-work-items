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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class MethodInvoker implements Serializable {

    public static final Logger logger = Logger.getLogger(MethodInvoker.class.getName());

    public static String prepareTemplate(String templateMethod, Map<String, Object> restResponse) {
        if (templateMethod == null) {
            return null;
        }
        try {
            int methodStartPos = templateMethod.lastIndexOf('.');
            String templateClassName = templateMethod.substring(0, methodStartPos);
            String templateMethodName = templateMethod.substring(methodStartPos + 1);

            Class<?> templClass = Class.forName(templateClassName);
            Method method = templClass.getMethod(templateMethodName, Map.class);
            Object template = method.invoke(null, restResponse);
            logger.log(Level.INFO, "Cancel template: " + template); //TODO lower log level
            if (template != null) {
                return (String) template;
            } else {
                return null;
            }
        } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            logger.severe("Unable to invoke method. " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
