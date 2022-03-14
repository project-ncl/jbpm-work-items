package org.jbpm.process.longrest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jbpm.process.longrest.util.Mapper;
import org.jbpm.process.longrest.util.Strings;
import org.kie.api.runtime.process.ProcessContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class ExecuteRestUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteRestUtils.class);

//    public static ExecuteRestConfiguration overrideWithContextParameters(ExecuteRestConfiguration config, ProcessContext processContext) {
    public static ExecuteRestConfiguration getExecuteRestConfiguration(ProcessContext processContext, ExecuteRestConfiguration overrides) {
        logger.debug("Overrides: {}", overrides );
        try {
            ExecuteRestConfiguration result = new ExecuteRestConfiguration();
            Method[] methods = ExecuteRestConfiguration.class.getDeclaredMethods();
            for (Method setterMethod : methods) {
                if (setterMethod.getName().startsWith("set")) {
                    Object newValue = null;
                    if (overrides != null) {
                        Method getterMethod = ExecuteRestConfiguration.class.getMethod(setterMethod.getName().replaceFirst("set", "get"));
                        newValue = getterMethod.invoke(overrides);
                    }
                    if (newValue == null) {
                        String fieldName = Strings.decapitalize(setterMethod.getName().substring(3));
                        newValue = processContext.getVariable(fieldName);
                    }
                    if (newValue != null) {
                        setterMethod.invoke(result, newValue);
                    }
                }
            }
            logger.debug("Updated configuration: {}", result );
            return result;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Cannot override parameters.", e);
        }
    }
}
