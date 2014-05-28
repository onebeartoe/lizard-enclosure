
package org.onebeartoe.lizard.enclosure.dashboard;

import java.io.IOException;
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
import org.onebeartoe.lizard.enclosure.LizardEnclosure;
import org.onebeartoe.lizard.enclosure.arduino.ArduinoMessage;
import static org.onebeartoe.lizard.enclosure.arduino.ArduinoSensorTypes.EXTERNAL_TEMPERATURE;
import static org.onebeartoe.lizard.enclosure.arduino.ArduinoSensorTypes.INTERNAL_HUMIDITY;
import static org.onebeartoe.lizard.enclosure.arduino.ArduinoSensorTypes.INTERNAL_TEMPERATURE;
import org.onebeartoe.lizard.enclosure.arduino.ArduinoServlet;
import org.onebeartoe.lizard.enclosure.controls.ControlPanelServlet;
import static org.onebeartoe.lizard.enclosure.controls.ControlPanelServlet.LIZARD_ENCLOSURE_ID;

/**
 * This servlet only has display responsibilities; all request are idempotent.
 * @author roberto
 */
@WebServlet(urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet
{
    private Logger logger;

    @Override
    public void init() throws ServletException 
    {
        super.init();
        
        logger = Logger.getLogger(DashboardServlet.class.getName());
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        ServletContext servletContext = getServletContext();
        LizardEnclosure enclosure = (LizardEnclosure) servletContext.getAttribute(LIZARD_ENCLOSURE_ID);
        
        ControlPanelServlet.humidifier(enclosure, request);
        
        ControlPanelServlet.uvLight(request);

//        ArduinoMessage internHumidity;
//        ArduinoMessage internTemerature;
//        ArduinoMessage externalTemperature;         
        ArduinoMessage internHumidity = new ArduinoMessage();
        ArduinoMessage internTemerature = new ArduinoMessage();
        ArduinoMessage externalTemperature = new ArduinoMessage(); 
        
        ServletContext context = getServletContext();
        List<String> allMessages = (List<String>) context.getAttribute(ArduinoServlet.ARDUINO_MESSAGES);
        if(allMessages == null)
        {
            internHumidity.sensorValue = -1.0;
            internTemerature.sensorValue = -1.0;
            externalTemperature.sensorValue = -1.0;
        }
        else
        {
            for(String s : allMessages)
            {
                try 
                {
                    ArduinoMessage m = ArduinoMessage.fromLine(s);
                    switch(m.sensorType)
                    {
                        case INTERNAL_HUMIDITY:
                        {
                            internHumidity = m;
                            break;
                        }
                        case INTERNAL_TEMPERATURE:
                        {
                            internTemerature = m;
                            break;
                        }
                        case EXTERNAL_TEMPERATURE:
                        {
                            externalTemperature = m;
                            break;
                        }
                    }
                } 
                catch (Exception ex) 
                {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
        
        request.setAttribute("internHumidity", internHumidity.sensorValue);
        request.setAttribute("internTemperature", internTemerature.sensorValue);
        request.setAttribute("externalTemperature", externalTemperature.sensorValue);
        
        RequestDispatcher rd = context.getRequestDispatcher("/dashboard.jsp");
        rd.forward(request, response);
    }
}
