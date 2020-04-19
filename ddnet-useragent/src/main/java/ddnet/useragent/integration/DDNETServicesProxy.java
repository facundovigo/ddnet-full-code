package ddnet.useragent.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DDNETServicesProxy {
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private URL endpoint;
    private String sessionID;

    public DDNETServicesProxy(URL endpoint, String sessionID) {
        setEndpoint(endpoint);
        setSessionID(sessionID);
    }

    public URL getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(URL endpoint) {
        if (endpoint == null)
            throw new IllegalArgumentException("endpoint");

        this.endpoint = endpoint;
    }

    private void setSessionID(String sessionID) {
        if (sessionID == null || sessionID.trim().length() == 0)
            throw new IllegalArgumentException("sessionID");

        this.sessionID = sessionID;
    }

	public void uploadStudyFile(String studyID, File file) throws FileNotFoundException {
		uploadStudyFile(studyID, new FileInputStream(file), FilenameUtils.getExtension(file.getAbsolutePath()));
	}
    
    public void uploadStudyFile(String studyID, InputStream fileInputStream, String fileExtension) {        
		try {
			HttpPost request = new HttpPost(new URL(endpoint, "restricted/rest/reporting/report/audio/" + studyID).toExternalForm());

	        String filename = String.format("%s.%s", UUID.randomUUID().toString(), fileExtension);
	        MultipartEntity mpEntity = new MultipartEntity();
	        ContentBody contentBody = new InputStreamBody(fileInputStream, filename);
	        mpEntity.addPart("study-file", contentBody);
	        request.setEntity(mpEntity);
	
	        executeRequestAsString(request);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
    }

    public String[] getStudyFiles(String studyID) {
        try {
            HttpGet request = new HttpGet(new URL(endpoint, "restricted/rest/studies/" + studyID + "/files").toExternalForm());

            JSONArray files = executeRequestAsJSONArray(request);
            Collection<String> returnValue = new ArrayList<>();
            for(int i=0;i<files.length();i++)
                returnValue.add(files.get(i).toString());

            return returnValue.toArray(new String[] {});
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private JSONArray executeRequestAsJSONArray(HttpRequestBase request) throws JSONException {
        return new JSONArray(executeRequestAsString(request));
    }

    private JSONObject executeRequestAsJSONObject(HttpRequestBase request) throws JSONException {
        return new JSONObject(executeRequestAsString(request));
    }

    private String executeRequestAsString(HttpRequestBase request) {
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

    private HttpClientContext getHttpClientContext() {
        HttpClientContext localContext = new HttpClientContext();
        localContext.setCookieStore(new BasicCookieStore());
        BasicClientCookie cookie = new BasicClientCookie(SESSION_COOKIE_NAME, sessionID);
        cookie.setDomain(endpoint.getHost());
        cookie.setPath(endpoint.getPath());
        localContext.getCookieStore().addCookie(cookie);
        return localContext;
    }
}
