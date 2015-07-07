
package org.onebeartoe.lizard.enclosure;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 *
 * @author Roberto Marquez
 */
public interface LizardEnclosureSevice 
{
    public static final String SELFIE_SENSOR_PIN = "SELFIE_SENSOR_PIN";
    
    GpioPinDigitalInput provisionSelfiePin(GpioController gpio);
    
    GpioPinListenerDigital newSelfieListener();
}
