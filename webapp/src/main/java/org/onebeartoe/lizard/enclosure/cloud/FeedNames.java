
package org.onebeartoe.lizard.enclosure.cloud;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roberto Marquez
 */
public class FeedNames 
{
    public static final String EXTERNAL_TEMPERATURE = "lizard-enclosure-external-temperature";
    
    public static final String TOP_TEMPERATURE = "lizard-enclosure-top-temperature";
    
    public static final String TOP_HUMIDITY = "lizard-enclosure-top-humidity";
    
    public static final String BOTTOM_TEMPERATURE = "lizard-enclosure-bottom-temperature";
    
    public static final String BOTTOM_HUMIDITY = "lizard-enclosure-bottom-humidity";
    
    public static List<String> list()
    {
        List<String> list = new ArrayList();
        
        list.add(EXTERNAL_TEMPERATURE);
        list.add(TOP_TEMPERATURE);
        list.add(TOP_HUMIDITY);
        
        return list;
    }
}
