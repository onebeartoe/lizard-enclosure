
package org.onebeartoe.lizard.enclosure.selfies;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.onebeartoe.electronics.photorama.Camera;
import org.onebeartoe.electronics.photorama.RaspberryPiCamera;
import org.onebeartoe.lizard.enclosure.LizardEnclosureSevice;
import org.onebeartoe.lizard.enclosure.RaspberryPiLizardEnclosureSevice;
import org.onebeartoe.system.Sleeper;

/**
 * @author Roberto Marquez
 * 
 * This class tests the connections to the selfie sensor, an Adafruit capacitive 
 * touch breakout board.
 * 
 * If the connections are good and the Raspberry Pi camera is functional, then 
 * a snapshot is taken when the capacitive touch sensor is activated.
 */
public class SelfieSensorTest 
{
    public static void main(String [] args) throws Exception
    {        
        GpioController gpio = GpioFactory.getInstance();
        
        String outpath = System.getProperty("user.home") + "/lizard-enclosure/photos";
        Camera camera = new RaspberryPiCamera();
        camera.setOutputPath(outpath);
                
        LizardEnclosureSevice lizardEnclosureSevice = new RaspberryPiLizardEnclosureSevice(camera);
        
        GpioPinListenerDigital selfieListener = lizardEnclosureSevice.newSelfieListener();
        
        GpioPinDigitalInput selfieSensorPin = lizardEnclosureSevice.provisionSelfiePin(gpio);
        selfieSensorPin.addListener(selfieListener);
        
        System.out.println("Ctrl+C to quit");
        while(true)
        {
            Sleeper.sleepo(1000);
        }
    }
}
