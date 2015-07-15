
package io.adafruit;

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
 * This example posts a new value to an existing feed.
 * 
 * The HTTP Post example is from: http://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
 * 
 * @author Roberto Marquez
 */
public class PostData 
{
    public static void main(String [] args) throws UnsupportedEncodingException, IOException
    {
        String url = "https://io.adafruit.com/api/feeds/lizard-enclosure-top-temperature/data";
 
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // set the Adafruit IO Key
        String aioKey = AioKeyLoader.load();
        post.setHeader("x-aio-key", aioKey);
        
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("value", "88"));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
 
        HttpResponse response = client.execute(post);
        
        System.out.println("\nSending 'POST' request to URL: " + url);
        
        System.out.println("Post parameters: " + post.getEntity());
        
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

        System.out.println(result.toString());
    }
}
