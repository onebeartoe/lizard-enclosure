
package org.onebeartoe.lizard.enclosure.arduino;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Roberto Marquez
 */
@WebServlet(urlPatterns = {"/arduino/sensor/readings/raw/mock"})
public class MockRawArduinoServlet extends HttpServlet
{
    private Logger logger;
    
       
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {                
        ArduinoMessage internHumidity;
        ArduinoMessage internTemerature;
        ArduinoMessage externalTemperature; 
    
        long millis = (new Date()).getTime();
    
        internHumidity = new ArduinoMessage();
        internHumidity.id = millis;
        internHumidity.sensorType = ArduinoSensorTypes.INTERNAL_HUMIDITY;
        internHumidity.sensorValue = 46.3;
        
        internTemerature = new ArduinoMessage();
        internTemerature.id = millis;
        internTemerature.sensorType = ArduinoSensorTypes.INTERNAL_TEMPERATURE;
        internTemerature.sensorValue = 86.0;
        
        externalTemperature = new ArduinoMessage();
        externalTemperature.id = millis;
        externalTemperature.sensorType = ArduinoSensorTypes.EXTERNAL_TEMPERATURE;
        externalTemperature.sensorValue = 70.2;
        
        List<ArduinoMessage> newMessages = new ArrayList();
        newMessages.add(internHumidity);
        newMessages.add(internTemerature);
        newMessages.add(externalTemperature);        
        
        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) 
        {
            for(ArduinoMessage message : newMessages)
            {
                String s = message.toLine();
                out.println(s);
            }
        }
    }    

    @Override
    public void init() throws ServletException 
    {
        super.init();
        
        logger = Logger.getLogger(MockRawArduinoServlet.class.getName());
        
        
    }
}
