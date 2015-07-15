
package io.adafruit.service;

import io.adafruit.AioResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author Roberto Marquez
 */
public class ApacheAdafruitIoService implements AdafruitIoService
{
    private String aioKey;
    
    private HttpClient client;
    
    public ApacheAdafruitIoService(String aioKey)
    {
        this.aioKey = aioKey;
        
        client = new DefaultHttpClient();
    }
    
    @Override
    public boolean createFeed(String feedName) throws UnsupportedEncodingException, IOException
    {
        String url = baseUrl + "feeds";
        
        List<NameValuePair> urlParameters = new ArrayList();
        urlParameters.add(new BasicNameValuePair("name", feedName));
        
        AioResponse ar = sendPostRequest(url, urlParameters);
        
        // we expect 201 response if the data was added to the feed
        boolean succeeded = ar.code == 201;
       
        return succeeded;
    }

    @Override
    public boolean addFeedData(String feedName, String value) throws UnsupportedEncodingException, IOException
    {
        String url = baseUrl + "feeds/" + feedName + "/data";
        
        List<NameValuePair> urlParameters = new ArrayList();
        urlParameters.add(new BasicNameValuePair("value", value));
        
        AioResponse ar = sendPostRequest(url, urlParameters);
        
        // we expect 201 response if the data was added to the feed
        boolean succeeded = ar.code == 201;
       
        return succeeded;
    }
    
    private AioResponse processResponse(HttpResponse response) throws IOException
    {        
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        System.out.println("Response Code: " + statusCode);

        System.out.println("Response:");
        
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        InputStreamReader reader = new InputStreamReader(content);
        BufferedReader br = new BufferedReader(reader);

        StringBuilder result = new StringBuilder();
        String line;
        
        while ((line = br.readLine()) != null) 
        {
            result.append(line);
            result.append("\n");
        }
        
        br.close();

        String aioContent = result.toString();
        
        AioResponse ar = new AioResponse();
        ar.code = statusCode;
        ar.content = aioContent;
                
        System.out.println(aioContent);
        
        return ar;
    }

    @Override
    public List<String> feedNames()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private AioResponse sendPostRequest(String url, List<NameValuePair> urlParameters) throws UnsupportedEncodingException, IOException
    {
        HttpPost post = new HttpPost(url);
        
        // set the Adafruit IO Key
        post.setHeader("x-aio-key", aioKey);

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
 
        System.out.println("\nSending 'POST' request to URL: " + url);        
        HttpResponse response = client.execute(post);
        
        System.out.println("Post parameters: " + post.getEntity());
        
        AioResponse ar = processResponse(response);
        
        return ar;
    }
}
