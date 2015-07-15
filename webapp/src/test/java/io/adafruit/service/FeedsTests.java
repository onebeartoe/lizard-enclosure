package io.adafruit.service;

import io.adafruit.AioKeyLoader;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Roberto Marquez
 */
public class FeedsTests
{
    private AdafruitIoService adafruitIoService;
    
    public FeedsTests()
    {
        String aioKey;
        try
        {
            aioKey = AioKeyLoader.load();
            adafruitIoService = new ApacheAdafruitIoService(aioKey);
        }
        catch (IOException ex)
        {
            Logger.getLogger(FeedsTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    @Test
    public void addData() throws IOException 
    {
        String name = "lizard-enclosure-top-temperature";
        String value = String.valueOf(7);
        
        adafruitIoService.addFeedData(name, value);
    }
    
    @Test
    public void createFeed() throws IOException
    {
        long milis = (new Date()).getTime();
        String feedName = "okay-to-delete-" + milis;
        
        adafruitIoService.createFeed(feedName);
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }



}
