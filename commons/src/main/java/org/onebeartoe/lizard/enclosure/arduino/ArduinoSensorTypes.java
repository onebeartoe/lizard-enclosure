
package org.onebeartoe.lizard.enclosure.arduino;

/**
 * @author roberto marquez
 */
public enum ArduinoSensorTypes 
{
    @Deprecated
            /**
             * @deprecated this needs to change to TOP_HUMIDITY, once the JavaFx app is ported to Maven
             */
    INTERNAL_HUMIDITY,
    
    @Deprecated
            /**
             * @deprecated this needs to change to TOP_TEMPERATURE, once the JavaFx app is ported to Maven
             */
    INTERNAL_TEMPERATURE,
    EXTERNAL_TEMPERATURE    
}
