
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
    
    public Long id;
    
    public static ArduinoMessage fromLine(String line)
    {
        int endIndex = line.indexOf(":");
        String s = line.substring(0, endIndex);
        Long id = new Long(s);
        ArduinoMessage message = new ArduinoMessage();
        message.id = id;
        
        return message;
    }
}
