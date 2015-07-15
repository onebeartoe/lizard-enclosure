
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
    
    public ArduinoSensorTypes sensorType;
    
    private String details;
    
    public Long id;
    
    public Double sensorValue;
    
    public static ArduinoMessage fromLine(String line)
    {
        String [] strs = line.split(":");
        
        if(strs.length != 4)
        {
            throw new IllegalArgumentException("the line must contain at least 4 parameters");
        }
        
        String s = strs[0];
        Long id = new Long(s);
        ArduinoMessage message = new ArduinoMessage();
        message.id = id;
        
        s = strs[1];
        ArduinoMessageTypes messageType = ArduinoMessageTypes.valueOf(s);
        message.messageType = messageType;

        s = strs[2];
        ArduinoSensorTypes sensorType = ArduinoSensorTypes.valueOf(s);
        message.sensorType = sensorType;
        
        s = strs[3];        
        if( s.toUpperCase().endsWith("F") )
        {
            s = s.substring(0, s.length()-1);
        }
        Double d = new Double(s);
        message.sensorValue = d;
        
        return message;
    }
    
    public String toLine()
    {
        return id + ":" + ArduinoMessageTypes.SENSOR_READING.name() + ":" + sensorType.name() + ":" + sensorValue;
    }
    
}
