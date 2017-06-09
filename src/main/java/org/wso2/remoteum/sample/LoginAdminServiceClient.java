package org.wso2.remoteum.sample;


import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by prakhash on 5/15/17.
 */

@WebServlet(name = "Test", urlPatterns = {"/authenticate"})
public class LoginAdminServiceClient extends HttpServlet {

    private static String serverUrl="https://localhost:9444/services/";
    private static String username="admin";
    private static String password="admin";

    private AuthenticationAdminStub authstub = null;
    private String authCookie = null;
    private ConfigurationContext ctx;

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            this.LoginAdminServiceClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialization of environment
     *
     * @throws Exception
     */
    public void LoginAdminServiceClient() throws Exception {
        ctx = ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);
        String authEPR = serverUrl + "AuthenticationAdmin";
        authstub = new AuthenticationAdminStub(ctx, authEPR);
        ServiceClient client = authstub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, authCookie);

        //set trust store properties required in SSL communication.
        System.setProperty("javax.net.ssl.trustStore", RemoteUMSampleConstants.TRUST_STORE_PATH);
        System.setProperty("javax.net.ssl.trustStorePassword", RemoteUMSampleConstants.TRUST_STORE_PASSWORD);

        //log in as admin user and obtain the cookie
        this.login(username, password);
        //create web service client
    }

    /**
     * Authenticate to carbon as admin user and obtain the authentication cookie
     *
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public String login(String username, String password) throws Exception {

        //String cookie = null;
        boolean loggedIn = authstub.login(username, password, "localhost");
        if (loggedIn) {
            System.out.println("The user " + username + " logged in successfully.");
            System.out.println();
            authCookie = (String) authstub._getServiceClient().getServiceContext().getProperty(
                    HTTPConstants.COOKIE_STRING);
        } else {
            System.out.println("Error logging in " + username);
        }
        return authCookie;
    }



}
