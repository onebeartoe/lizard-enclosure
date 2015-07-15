
package io.adafruit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads an Adafruit IO Key from a text file on the filesystem.
 * 
 * The file should contain only the characters in the Adafruit IO Key.
 * 
 * @author Roberto Marquez
 */
public class AioKeyLoader 
{
    private static final String LOGGER_NAME = AioKeyLoader.class.getSimpleName();
    
    private static Logger logger = Logger.getLogger(LOGGER_NAME);
    
    public static String load() throws IOException
    {
        // change this path to where ever the Adafruit IO Key is stored        
        String windowsLocation = "C:\\home\\owner\\aio.key";
        File f = new File(windowsLocation);
        
        if( f.exists() )
        {
            String message = "Using Adafruit IO Key for a Windows path";
            logger.log(Level.INFO, message);
        }
        else
        {
            String raspberryPiLocation = "/home/pi/aio.key";
            
            f = new File(raspberryPiLocation);
        }
        
        Path path = f.toPath();
        
        List<String> lines = Files.readAllLines(path);
        
        String key = lines.get(0);
        
        return key;
    }
}
