package main.java.org.jbpm.process.longrest.authentication;

import kong.unirest.HttpResponse;
import kong.unirest.MultipartBody;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.jackson.JacksonObjectMapper;
import org.jbpm.process.longrest.Constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLHandshakeException;

/**
 * Class to obtain an OIDC Access token. The environment variables: SSO_URL, SSO_REALM,
 * SSO_SERVICE_ACCOUNT_CLIENT, SSO_SERVICE_ACCOUNT_SECRET must be set to be able to use it
 */
public class OidcClient {
    private static final int MAX_RETRIES = 10;

    static {
        // Configure Unirest ObjectMapper
        Unirest.config().setObjectMapper(new JacksonObjectMapper());
    }
    private static final Logger logger = LoggerFactory.getLogger(OidcClient.class);

    public static String getAccessToken() {

        try {
            checkIfEnvironmentVariableExists(Constant.SSO_URL_VARIABLE);
            checkIfEnvironmentVariableExists(Constant.SSO_REALM_VARIABLE);
            checkIfEnvironmentVariableExists(Constant.SSO_SERVICE_ACCOUNT_CLIENT_VARIABLE);
            checkIfEnvironmentVariableExists(Constant.SSO_SERVICE_ACCOUNT_SECRET_VARIABLE);
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            logger.error("Returning empty access token");
            return "";
        }

        String keycloakEndpoint = keycloakEndpoint(System.getenv((Constant.SSO_URL_VARIABLE)), System.getenv(Constant.SSO_REALM_VARIABLE));

        MultipartBody body = Unirest.post(keycloakEndpoint)
                .field("grant_type", "client_credentials")
                .field("client_id", System.getenv(Constant.SSO_SERVICE_ACCOUNT_CLIENT_VARIABLE))
                .field("client_secret", System.getenv(Constant.SSO_SERVICE_ACCOUNT_SECRET_VARIABLE));

        KeycloakResponse response = getKeycloakResponseWithRetries(body);
        return response.getAccessToken();
    }

    private static void checkIfEnvironmentVariableExists(String environmentVariable) {
        if (System.getenv(environmentVariable) == null) {
            throw new RuntimeException("Environment Variable: " + environmentVariable + " is not set!");
        }
    }

    static String keycloakEndpoint(String keycloakBaseUrl, String realm) {
        String keycloakUrl = keycloakBaseUrl;

        if (!keycloakBaseUrl.endsWith("/")) {
            keycloakUrl = keycloakBaseUrl + "/";
        }

        return keycloakUrl + "realms/" + realm + "/protocol/openid-connect/token";
    }

    static KeycloakResponse getKeycloakResponseWithRetries(MultipartBody body) throws UnirestException {
       int retries = 0;

        while (true) {
            try {
                HttpResponse<KeycloakResponse> postResponse = body.asObject(KeycloakResponse.class);
                return postResponse.getBody();
            } catch (UnirestException e) {
                if (e.getCause().getClass().equals(SSLHandshakeException.class)) {
                    throw new RuntimeException(
                            "Cannot reach the Keycloak server because of missing TLS certificates",
                            e.getCause());
                }
                retries++;

                if (retries > MAX_RETRIES) {
                    // all hope is lost. Stop retrying
                    throw e;
                } else if (retries == MAX_RETRIES / 2) {
                    // let the user know as she waits
                    logger.info("Having difficulty reaching {}. Retrying again...", body.getUrl());
                }
                sleepExponentially(retries);
                logger.debug("Retrying to reach: {}", body.getUrl());
            }
        }
    }

    /**
     * Sleep starting from 100 milliseconds when retries = 0, and sleeping exponentially as the retries increase
     *
     * The math is millisecondsSleep = 100 * 2^(retries)
     *
     * @param retries number of retries attempted
     */
    private static void sleepExponentially(int retries) {

        long amountOfSleep = (long) (100 * Math.pow(2, retries));
        logger.debug("Sleeping for {} seconds", String.format("%.1f", amountOfSleep / 1000.0));

        try {
            Thread.sleep(amountOfSleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
