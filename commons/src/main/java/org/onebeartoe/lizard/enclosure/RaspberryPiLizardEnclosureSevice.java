
package org.onebeartoe.lizard.enclosure;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.onebeartoe.electronics.photorama.Camera;

/**
 *
 * @author lando
 */
public class RaspberryPiLizardEnclosureSevice implements LizardEnclosureSevice
{
    private Camera camera;

    public RaspberryPiLizardEnclosureSevice(Camera camera)
    {
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
                    camera.takeSnapshot();
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
