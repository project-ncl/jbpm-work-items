package main.java.org.jbpm.process.longrest.authentication;

import org.apache.http.impl.client.HttpClients;
import org.jbpm.process.longrest.Constant;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;

import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to obtain an OIDC Access token. The environment variables: SSO_URL, SSO_REALM,
 * SSO_SERVICE_ACCOUNT_CLIENT, SSO_SERVICE_ACCOUNT_SECRET must be set to be able to use it
 */
public class OidcClient {

    private static final Logger logger = LoggerFactory.getLogger(OidcClient.class);

    public static String getAccessToken() {

        if (System.getProperty(Constant.SSO_OIDC_BEING_TESTED, "false").equals("true")) {
            logger.warn("SSO_OIDC_BEING_TESTED set to true. Returning bogus value for getAccessToken");
            return "1234";
        }

        checkIfEnvironmentVariableExists(Constant.SSO_URL_VARIABLE);
        checkIfEnvironmentVariableExists(Constant.SSO_REALM_VARIABLE);
        checkIfEnvironmentVariableExists(Constant.SSO_SERVICE_ACCOUNT_CLIENT_VARIABLE);
        checkIfEnvironmentVariableExists(Constant.SSO_SERVICE_ACCOUNT_SECRET_VARIABLE);

        final Configuration configuration = new Configuration(
                System.getenv(Constant.SSO_URL_VARIABLE),
                System.getenv(Constant.SSO_REALM_VARIABLE),
                System.getenv(Constant.SSO_SERVICE_ACCOUNT_CLIENT_VARIABLE),
                Collections.singletonMap("secret", System.getenv(Constant.SSO_SERVICE_ACCOUNT_SECRET_VARIABLE)),
                HttpClients.createDefault());

        return AuthzClient.create(configuration).obtainAccessToken().getToken();
    }

    private static void checkIfEnvironmentVariableExists(String environmentVariable) {
        if (System.getenv(environmentVariable) == null) {
            throw new RuntimeException("Environment Variable: " + environmentVariable + " is not set!");
        }
    }
}
