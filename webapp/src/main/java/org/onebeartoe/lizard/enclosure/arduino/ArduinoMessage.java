
package org.onebeartoe.lizard.enclosure.arduino;

/**
 * An ArduinoMessage is transmitted over the serial port.
 * 
 * @see ArduinoMessageTypes
 * 
 * Each line that comes over the Arduino's format should have this format:
 * 
 * <MESSAGE_TYPE>|<OPERAND>|[PARAMERTER1]|[PARAMERTER2]
 *
 * @author Roberto Marquez
 */
public class ArduinoMessage 
{
    private ArduinoMessageTypes messageType;
    
    private String details;
}
