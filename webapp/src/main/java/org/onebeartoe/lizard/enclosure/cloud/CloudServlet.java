
package org.onebeartoe.lizard.enclosure.cloud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.onebeartoe.lizard.enclosure.arduino.ArduinoMessage;
import org.onebeartoe.lizard.enclosure.arduino.ArduinoSensorTypes;
import static org.onebeartoe.lizard.enclosure.arduino.ArduinoServlet.ARDUINO_MESSAGES;

/**
 * @author Roberto Marquez
 */
@WebServlet(urlPatterns = {"/cloud/update"})
public class CloudServlet extends HttpServlet
{
    private LizardEnclosureCloudService lizardEnclosureCloudService;
    
    private Logger logger;
    
    private void addDummyData(List<ArduinoMessage> amList)
    {
        ArduinoMessage m1 = new ArduinoMessage();
        m1.sensorType = ArduinoSensorTypes.EXTERNAL_TEMPERATURE;
        m1.sensorValue = 5.0d;
        
        ArduinoMessage m2 = new ArduinoMessage();
        m2.sensorType = ArduinoSensorTypes.INTERNAL_TEMPERATURE;
        m2.sensorValue = 10.0d;
        
        ArduinoMessage m3 = new ArduinoMessage();
        m3.sensorType = ArduinoSensorTypes.INTERNAL_HUMIDITY;
        m3.sensorValue = 15.0d;
                
        amList.add(m1);
        amList.add(m2);
        amList.add(m3);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        updateCloudServices();
        
        ServletContext context = getServletContext();
        RequestDispatcher rd = context.getRequestDispatcher("/arduino/sensor/readings/index.jsp");
        rd.forward(request, response);
    }
    
    private List<ArduinoMessage> findLastestUpdates(List<String> lines)
    {
        List<ArduinoSensorTypes> targets = new ArrayList();
        targets.add(ArduinoSensorTypes.EXTERNAL_TEMPERATURE);
        targets.add(ArduinoSensorTypes.INTERNAL_TEMPERATURE);
        targets.add(ArduinoSensorTypes.INTERNAL_HUMIDITY);
        
        List<ArduinoMessage> amList = new ArrayList();

        if(lines == null || lines.isEmpty())
        {
            addDummyData(amList);
        }
        else
        {
            for(String l : lines)
            {
                try
                {
                    ArduinoMessage am = ArduinoMessage.fromLine(l);

                    amList.add(am);
                }
                catch(Exception e)
                {
                    String message = "could not obtain Arduino message from: >" + l + "<";
                    logger.log(Level.SEVERE, message, e);
                }
            }
        }
        
        
        
        List<ArduinoMessage> latestList = new ArrayList();
        
        for(int i=0; i<amList.size(); i++)
        {
            ArduinoMessage am = amList.get(i);
            
            if( targets.contains(am.sensorType) )
            {
                targets.remove(am.sensorType);
                
                latestList.add(am);
            }
            
            if( targets.isEmpty() )
            {
                // we found the last target
            
                break;
            }
        }
        
        return latestList;
    }
    
    @Override
    public void init()
    {
        logger = Logger.getLogger( getClass().getName() );
        
        try
        {
            lizardEnclosureCloudService = new LizardEnclosureCloudService();
            
        }
        catch(Exception e)
        {
            logger.log(Level.SEVERE, "erro creating Lizard enclosure clouse service", e);
        }        
    }
    
    private void updateCloudServices()
    {
        ServletContext servletContext = getServletContext();
        Object attribute = servletContext.getAttribute(ARDUINO_MESSAGES);
        
        List<String> messages = (List<String>) attribute;
        
        List<ArduinoMessage> amList = findLastestUpdates(messages);
        
        for(ArduinoMessage am : amList)
        {
            boolean postSucceeded;
        
            switch  (am.sensorType)
            {
                case EXTERNAL_TEMPERATURE:
                {                
                    postSucceeded = lizardEnclosureCloudService.addExternalTemperature(am.sensorValue);

                    break;
                }
                case INTERNAL_HUMIDITY:
                {
                    postSucceeded = lizardEnclosureCloudService.addTopHumidity(am.sensorValue);
                    break;
                }
                case INTERNAL_TEMPERATURE:
                {
                    postSucceeded = lizardEnclosureCloudService.addTopTemperature(am.sensorValue);
                    break;
                }
                default:
                {
                    postSucceeded = false;
                }
            }

            String logEntry = "update to cloud succeded: " + postSucceeded;
            logger.log(Level.INFO, logEntry);
        }
    }
}
