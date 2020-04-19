package ddnet.mobile.integration;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.client.methods.HttpRequestBaseHC4;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.BasicCookieStoreHC4;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookieHC4;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class DDNETServicesProxy {
    private static final String LOG_TAG = "DDMOBILE.DDNETSvcProxy";
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private Uri endpoint;
    private String username;
    private String password;
    private String sessionID;

    public DDNETServicesProxy(Uri endpoint, String username, String password) {
        setEndpoint(endpoint);
        setUsername(username);
        setPassword(password);
    }

    public Uri getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Uri endpoint) {
        if (endpoint == null)
            throw new IllegalArgumentException("endpoint");

        this.endpoint = endpoint;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().length() == 0)
            throw new IllegalArgumentException("username");

        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().length() == 0)
            throw new IllegalArgumentException("password");

        this.password = password;
    }

    public void authenticate() throws DDNETAuthenticationException {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            sessionID = null;
            HttpClientContext localContext = new HttpClientContext();
            localContext.setCookieStore(new BasicCookieStoreHC4());

            HttpGetHC4 request = new HttpGetHC4(endpoint.buildUpon()
                    .appendPath("basic-authentication-servlet")
                    .appendQueryParameter("operation", "logon")
                    .build().toString());

            request.addHeader("Authorization", getAuthorizationHeaderValue());
            httpClient = HttpClients.createDefault();
            response = httpClient.execute(request, localContext);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED)
                throw new DDNETAuthenticationException("Usuario o clave inválidos");
            if (statusCode != HttpURLConnection.HTTP_OK)
                throw new DDNETAuthenticationException("Error realizando login en DDNET. StatusCode="+ statusCode);

            for(Cookie cookie : localContext.getCookieStore().getCookies()) {
                if (cookie.getName().equals(SESSION_COOKIE_NAME)) {
                    sessionID = cookie.getValue();
                    break;
                }
            }

            if (sessionID == null)
                throw new DDNETAuthenticationException("Cookie de sesión no encontrada");

            Log.d(LOG_TAG, "Authenticate - SessionID=" + sessionID);
        } catch (Throwable t) {
            throw new DDNETAuthenticationException(t);
        } finally {
            if (response != null) try { response.close(); } catch(Throwable ignore) { }
            if (httpClient != null) try { httpClient.close(); } catch(Throwable ignore) { }
        }
    }

    public boolean authenticateQuietly() {
        try {
            authenticate();
            return true;
        } catch (Throwable t) {
            Log.e(LOG_TAG, "Error CONTROLADO (authenticateQuietly)", t);
            return false;
        }
    }

    public void uploadStudyFile(String studyID, InputStream fileInputStream, String fileExtension) throws DDNETAuthenticationException {
        checkAuthentication();

        HttpPostHC4 request = new HttpPostHC4(endpoint.buildUpon()
                .appendPath("restricted").appendPath("rest").appendPath("studies")
                .appendPath(studyID).appendPath("files").build().toString());

        String filename = String.format("%s.%s", UUID.randomUUID().toString(), fileExtension);
        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody contentBody = new InputStreamBody(fileInputStream, filename);
        mpEntity.addPart("study-file", contentBody);
        request.setEntity(mpEntity);

        executeRequestAsString(request);
    }

    public String[] getStudyFiles(String studyID) throws DDNETAuthenticationException {
        checkAuthentication();

        try {
            HttpGetHC4 request = new HttpGetHC4(endpoint.buildUpon()
                    .appendPath("restricted").appendPath("rest").appendPath("studies")
                    .appendPath(studyID).appendPath("files").build().toString());

            JSONArray files = executeRequestAsJSONArray(request);
            Collection<String> returnValue = new ArrayList<>();
            for(int i=0;i<files.length();i++)
                returnValue.add(files.get(i).toString());

            return returnValue.toArray(new String[] {});
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private void checkAuthentication() throws DDNETAuthenticationException {
        if (sessionID == null)
            throw new DDNETAuthenticationException("No se ha realizado autenticación contra DDNET");

        // TODO: Validar que no haya expirado la autenticación realizada.
    }

    private JSONArray executeRequestAsJSONArray(HttpRequestBaseHC4 request) throws JSONException {
        return new JSONArray(executeRequestAsString(request));
    }

    private JSONObject executeRequestAsJSONObject(HttpRequestBaseHC4 request) throws JSONException {
        return new JSONObject(executeRequestAsString(request));
    }

    private String executeRequestAsString(HttpRequestBaseHC4 request) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            response = httpClient.execute(request, getHttpClientContext());
            HttpEntity entity = response.getEntity();
            entity.consumeContent();

            String line;
            StringBuilder responseStringBuilder = new StringBuilder();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(entity.getContent()));
            while ((line = streamReader.readLine()) != null)
                responseStringBuilder.append(line);

            return responseStringBuilder.toString();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            if (response != null) try { response.close(); } catch(Throwable ignore) { }
            if (httpClient != null) try { httpClient.close(); } catch(Throwable ignore) { }
        }
    }

    private String getAuthorizationHeaderValue() {
        final String usernamePassword = String.format("%s:%s", username, password);
        return String.format("Basic %s", Base64.encodeToString(usernamePassword.getBytes(), Base64.NO_WRAP));
    }

    private HttpClientContext getHttpClientContext() {
        HttpClientContext localContext = new HttpClientContext();
        localContext.setCookieStore(new BasicCookieStoreHC4());
        BasicClientCookieHC4 cookie = new BasicClientCookieHC4(SESSION_COOKIE_NAME, sessionID);
        cookie.setDomain(endpoint.getHost());
        cookie.setPath(endpoint.getPath());
        localContext.getCookieStore().addCookie(cookie);
        return localContext;
    }
}
