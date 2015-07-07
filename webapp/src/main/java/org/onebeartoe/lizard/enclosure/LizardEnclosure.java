
package org.onebeartoe.lizard.enclosure;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import org.onebeartoe.electronics.photorama.Camera;

/**
 *
 * @author rmarquez
 */
public class LizardEnclosure 
{
    /**
     * This pin controls the relay attached to the humidifier.
     */
    public GpioPinDigitalOutput humidifierPin;
    
    /**
     * This pin controls the relay attached to the UV lights.
     */
    public GpioPinDigitalOutput uvLightPin;
    
    /**
     * This pin is connected to the capacitive touch sensor for the lizard selfies.
     */
    public GpioPinDigitalInput selfieSensorPin;
    
    public Camera camera;
}
