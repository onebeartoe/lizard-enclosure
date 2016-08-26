
package org.onebeartoe.lizard.enclosure;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.io.File;
import java.time.Duration;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.onebeartoe.electronics.photorama.Camera;

/**
 *
 * @author lando
 */
public class RaspberryPiLizardEnclosureSevice implements LizardEnclosureSevice
{
    private Logger logger;
    
    private Camera camera;
    
    private final long blackoutDuration = Duration.ofMinutes(20).toMillis();
    
    private long lastSnapshot = 0;

    public RaspberryPiLizardEnclosureSevice(Camera camera)
    {
        logger = Logger.getLogger(getClass().getName());
        
        String userHome = System.getProperty("user.home");
        
        final String photosOutputDir = "/home/pi/lizard-enclosure/selfies";
        
        try
        {
            File outdir = new File(photosOutputDir);
            outdir.mkdirs();
            
            camera.setOutputPath(photosOutputDir);            
        }
        catch (Exception ex)
        {
            String message = "An error occurred while setting the selfie output path";
            logger.log(Level.SEVERE, message, ex);
        }
        
        this.camera = camera;
    }
    
    @Override
    public GpioPinListenerDigital newSelfieListener()
    {
        GpioPinListenerDigital selfieListener = new GpioPinListenerDigital()
            {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpdsce)
                {
                    if(gpdsce.getState() == PinState.HIGH)
                    {
                        System.out.println("Low state reached");
                    }
                    else
                    {
                        System.out.println("High state reached");
                     
                        long now = (new Date()).getTime();
                        
                        if(now < lastSnapshot + blackoutDuration)
                        {
                            String message = "not taking taking a snapshot, it is too soon since the last one";

                            System.out.println(message);
                        }
                        else
                        {
                            String message = "taking a snapshot";

                            System.out.println(message);
                            
                            lastSnapshot = now;

                            camera.takeSnapshot();
                        }
                    }
                }
            };
        
        return selfieListener; 
    }
    
    @Override
    public GpioPinDigitalInput provisionSelfiePin(GpioController gpio) 
    {
        GpioPinDigitalInput selfiePin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06,
                                                        SELFIE_SENSOR_PIN,
                                                        PinPullResistance.PULL_DOWN);
        
        return selfiePin;
    }
    
}
