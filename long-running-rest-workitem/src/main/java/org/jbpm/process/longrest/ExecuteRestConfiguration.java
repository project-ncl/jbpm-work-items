package org.jbpm.process.longrest;

import java.io.Serializable;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class ExecuteRestConfiguration implements Serializable {

    Boolean noCallback;
    Boolean ignoreCancelSignals;

    String requestUrl;
    String requestMethod;
    String requestHeaders;
    String requestTemplate;
    String cancelUrlJsonPointer;
    String cancelUrlTemplate;
    String cancelBodyFunctionName;
    String cancelMethod;
    String cancelHeaders;
    String successEvalTemplate;

    Integer taskTimeout;
    Integer cancelTimeout;

    String heartbeatTimeout;

    Integer retryDelay;
    Integer maxRetries;

    Integer requestSocketTimeout;

    public Boolean getNoCallback() {
        return noCallback;
    }

    public Boolean getIgnoreCancelSignals() {
        return ignoreCancelSignals;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getCancelUrlJsonPointer() {
        return cancelUrlJsonPointer;
    }

    public String getCancelUrlTemplate() {
        return cancelUrlTemplate;
    }

    public String getCancelBodyFunctionName() {
        return cancelBodyFunctionName;
    }

    public String getRequestTemplate() {
        return requestTemplate;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public String getCancelMethod() {
        return cancelMethod;
    }

    public String getCancelHeaders() {
        return cancelHeaders;
    }

    public String getSuccessEvalTemplate() {
        return successEvalTemplate;
    }

    public Integer getTaskTimeout() {
        return taskTimeout;
    }

    public Integer getCancelTimeout() {
        return cancelTimeout;
    }

    public String getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public Integer getRetryDelay() {
        return retryDelay;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public Integer getRequestSocketTimeout() {
        return requestSocketTimeout;
    }

    public void setNoCallback(Boolean noCallback) {
        this.noCallback = noCallback;
    }

    public void setIgnoreCancelSignals(Boolean ignoreCancelSignals) {
        this.ignoreCancelSignals = ignoreCancelSignals;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setCancelUrlJsonPointer(String cancelUrlJsonPointer) {
        this.cancelUrlJsonPointer = cancelUrlJsonPointer;
    }

    public void setCancelUrlTemplate(String cancelUrlTemplate) {
        this.cancelUrlTemplate = cancelUrlTemplate;
    }

    public void setCancelBodyFunctionName(String cancelBodyFunctionName) {
        this.cancelBodyFunctionName = cancelBodyFunctionName;
    }

    public void setRequestTemplate(String requestTemplate) {
        this.requestTemplate = requestTemplate;
    }

    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public void setCancelMethod(String cancelMethod) {
        this.cancelMethod = cancelMethod;
    }

    public void setCancelHeaders(String cancelHeaders) {
        this.cancelHeaders = cancelHeaders;
    }

    public void setSuccessEvalTemplate(String successEvalTemplate) {
        this.successEvalTemplate = successEvalTemplate;
    }

    public void setTaskTimeout(Integer taskTimeout) {
        this.taskTimeout = taskTimeout;
    }

    public void setCancelTimeout(Integer cancelTimeout) {
        this.cancelTimeout = cancelTimeout;
    }

    public void setHeartbeatTimeout(String heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    public void setRetryDelay(Integer retryDelay) {
        this.retryDelay = retryDelay;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public void setRequestSocketTimeout(Integer requestSocketTimeout) {
        this.requestSocketTimeout = requestSocketTimeout;
    }

    public static ExecuteRestConfigurationBuilder builder() {
        return new ExecuteRestConfigurationBuilder();
    }

    public static final class ExecuteRestConfigurationBuilder {

        Boolean noCallback;
        Boolean ignoreCancelSignals;
        String requestUrl;
        String requestMethod;
        String cancelUrlJsonPointer;
        String cancelUrlTemplate;
        String cancelBodyFunctionName;
        String requestTemplate;
        String requestHeaders;
        String cancelMethod;
        String cancelHeaders;
        String successEvalTemplate;
        Integer taskTimeout;
        Integer cancelTimeout;
        String heartbeatTimeout;
        Integer retryDelay;
        Integer maxRetries;
        Integer requestSocketTimeout;

        ExecuteRestConfigurationBuilder() {
        }

        public ExecuteRestConfigurationBuilder noCallback(Boolean noCallback) {
            this.noCallback = noCallback;
            return this;
        }

        public ExecuteRestConfigurationBuilder ignoreCancelSignals(Boolean ignoreCancelSignals) {
            this.ignoreCancelSignals = ignoreCancelSignals;
            return this;
        }

        public ExecuteRestConfigurationBuilder requestUrl(String requestUrl) {
            this.requestUrl = requestUrl;
            return this;
        }

        public ExecuteRestConfigurationBuilder requestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public ExecuteRestConfigurationBuilder cancelUrlJsonPointer(String cancelUrlJsonPointer) {
            this.cancelUrlJsonPointer = cancelUrlJsonPointer;
            return this;
        }

        public ExecuteRestConfigurationBuilder cancelUrlTemplate(String cancelUrlTemplate) {
            this.cancelUrlTemplate = cancelUrlTemplate;
            return this;
        }

        public ExecuteRestConfigurationBuilder cancelBodyFunctionName(String cancelBodyFunctionName) {
            this.cancelBodyFunctionName = cancelBodyFunctionName;
            return this;
        }

        public ExecuteRestConfigurationBuilder requestTemplate(String requestTemplate) {
            this.requestTemplate = requestTemplate;
            return this;
        }

        public ExecuteRestConfigurationBuilder requestHeaders(String requestHeaders) {
            this.requestHeaders = requestHeaders;
            return this;
        }

        public ExecuteRestConfigurationBuilder cancelMethod(String cancelMethod) {
            this.cancelMethod = cancelMethod;
            return this;
        }

        public ExecuteRestConfigurationBuilder cancelHeaders(String cancelHeaders) {
            this.cancelHeaders = cancelHeaders;
            return this;
        }

        public ExecuteRestConfigurationBuilder successEvalTemplate(String successEvalTemplate) {
            this.successEvalTemplate = successEvalTemplate;
            return this;
        }

        public ExecuteRestConfigurationBuilder taskTimeout(Integer taskTimeout) {
            this.taskTimeout = taskTimeout;
            return this;
        }

        public ExecuteRestConfigurationBuilder cancelTimeout(Integer cancelTimeout) {
            this.cancelTimeout = cancelTimeout;
            return this;
        }

        public ExecuteRestConfigurationBuilder heartbeatTimeout(String heartbeatTimeout) {
            this.heartbeatTimeout = heartbeatTimeout;
            return this;
        }

        public ExecuteRestConfigurationBuilder retryDelay(Integer retryDelay) {
            this.retryDelay = retryDelay;
            return this;
        }

        public ExecuteRestConfigurationBuilder maxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public ExecuteRestConfigurationBuilder requestSocketTimeout(Integer requestSocketTimeout) {
            this.requestSocketTimeout = requestSocketTimeout;
            return this;
        }

        public ExecuteRestConfiguration build() {
            ExecuteRestConfiguration executeRestConfiguration = new ExecuteRestConfiguration();
            executeRestConfiguration.cancelUrlJsonPointer = this.cancelUrlJsonPointer;
            executeRestConfiguration.requestHeaders = this.requestHeaders;
            executeRestConfiguration.requestMethod = this.requestMethod;
            executeRestConfiguration.successEvalTemplate = this.successEvalTemplate;
            executeRestConfiguration.cancelHeaders = this.cancelHeaders;
            executeRestConfiguration.heartbeatTimeout = this.heartbeatTimeout;
            executeRestConfiguration.cancelMethod = this.cancelMethod;
            executeRestConfiguration.maxRetries = this.maxRetries;
            executeRestConfiguration.requestTemplate = this.requestTemplate;
            executeRestConfiguration.taskTimeout = this.taskTimeout;
            executeRestConfiguration.requestUrl = this.requestUrl;
            executeRestConfiguration.retryDelay = this.retryDelay;
            executeRestConfiguration.cancelUrlTemplate = this.cancelUrlTemplate;
            executeRestConfiguration.requestSocketTimeout = this.requestSocketTimeout;
            executeRestConfiguration.cancelBodyFunctionName = this.cancelBodyFunctionName;
            executeRestConfiguration.noCallback = this.noCallback;
            executeRestConfiguration.ignoreCancelSignals = this.ignoreCancelSignals;
            executeRestConfiguration.cancelTimeout = this.cancelTimeout;
            return executeRestConfiguration;
        }
    }

    @Override
    public String toString() {
        return "ExecuteRestConfiguration{" +
                "noCallback=" + noCallback +
                ", ignoreCancelSignals=" + ignoreCancelSignals +
                ", requestUrl='" + requestUrl + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", requestHeaders='" + requestHeaders + '\'' +
                ", requestTemplate='" + requestTemplate + '\'' +
                ", cancelUrlJsonPointer='" + cancelUrlJsonPointer + '\'' +
                ", cancelUrlTemplate='" + cancelUrlTemplate + '\'' +
                ", cancelBodyFunctionName='" + cancelBodyFunctionName + '\'' +
                ", cancelMethod='" + cancelMethod + '\'' +
                ", cancelHeaders='" + cancelHeaders + '\'' +
                ", successEvalTemplate='" + successEvalTemplate + '\'' +
                ", taskTimeout=" + taskTimeout +
                ", cancelTimeout=" + cancelTimeout +
                ", heartbeatTimeout='" + heartbeatTimeout + '\'' +
                ", retryDelay=" + retryDelay +
                ", maxRetries=" + maxRetries +
                ", requestSocketTimeout=" + requestSocketTimeout +
                '}';
    }
}
