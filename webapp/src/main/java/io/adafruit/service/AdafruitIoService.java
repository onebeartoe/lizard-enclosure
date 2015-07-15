
package io.adafruit.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author Roberto Marquez
 */
public interface AdafruitIoService 
{
    public static final String baseUrl = "https://io.adafruit.com/api/";
    
    boolean createFeed(String feedName) throws UnsupportedEncodingException, IOException;
    
    boolean addFeedData(String feedName, String value) throws UnsupportedEncodingException, IOException;
    
    List<String> feedNames();
}
