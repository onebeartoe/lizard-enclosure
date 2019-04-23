
package org.onebeartoe.lizard.enclosure.cloud;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.onebeartoe.web.adafruit.io.AdafruitIoService;
import org.onebeartoe.web.adafruit.io.AioKeyLoader;
import org.onebeartoe.web.adafruit.io.ApacheAdafruitIoService;

/**
 * @author Roberto Marquez
 */
public class LizardEnclosureCloudService 
{
    private AdafruitIoService adafruitIoService;
    
    public LizardEnclosureCloudService() throws IOException
    {
        String aioKey = AioKeyLoader.load();
        
        adafruitIoService = new ApacheAdafruitIoService(aioKey);
        
        initializeFeeds();
    }
    
    public boolean addFeedData(String feedName, Double sensorValue)
    {
        boolean succeeded = true;
        
        try
        {
            String value = String.valueOf(sensorValue);
            
            adafruitIoService.addFeedData(feedName, value);
        }
        catch (IOException ex)
        {
            succeeded = false;
            
            String message = "error adding value to cloud feed";
            
            Logger.getLogger(LizardEnclosureCloudService.class.getName()).log(Level.SEVERE, message, ex);
        }
        
        return succeeded;
    }

    public boolean addExternalTemperature(Double sensorValue)
    {
        String feedName = FeedNames.EXTERNAL_TEMPERATURE;
                
        boolean succeeded = addFeedData(feedName, sensorValue);
        
        return succeeded;
    }
    
    /**
     * This is for the humidity sensor in the ReptiBreeze portion of the controller.  It is 
     * on top of the aquarium.
     * 
     * @param sensorValue
     * @return 
     */
    public boolean addTopHumidity(Double sensorValue)
    {
        String feedName = FeedNames.TOP_HUMIDITY;
                
        boolean succeeded = addFeedData(feedName, sensorValue);
        
        return succeeded;
    }
    
    /**
     * This is for the temperature sensor in the ReptiBreeze portion of the controller.  It is 
     * on top of the aquarium.
     * 
     * @param sensorValue
     * @return 
     */
    public boolean addTopTemperature(Double sensorValue)
    {
        String feedName = FeedNames.TOP_TEMPERATURE;
                
        boolean succeeded = addFeedData(feedName, sensorValue);
        
        return succeeded;
    }
    
    private void initializeFeeds() //throws IOException
    {
// we need to find a better way to initialize the feeds if they dont exists
// maybe make a 'list feeds' request and only create if it doesn't alredy exist
// I didnt' see an error, but it my be droping any existing data if the feed already exists       
        
//        List<String> feedNames = FeedNames.list();
//        for(String name : feedNames)
//        {
//            try
//            {
//                adafruitIoService.createFeed(name);
//            }
//            catch (IOException ex)
//            {
//                Logger.getLogger(LizardEnclosureCloudService.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }
}
