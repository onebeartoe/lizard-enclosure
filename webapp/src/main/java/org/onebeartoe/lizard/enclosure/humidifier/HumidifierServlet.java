
package org.onebeartoe.lizard.enclosure.humidifier;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.onebeartoe.lizard.enclosure.LizardEnclosure;
import org.onebeartoe.lizard.enclosure.controls.ControlPanelServlet;

/**
 * @author roberto
 */
@WebServlet(urlPatterns = {"/controls/humidifier"})
public class HumidifierServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        ServletConfig servletConfig = getServletConfig();        
        ServletContext servletContext = servletConfig.getServletContext();
        Object attribute = servletContext.getAttribute(ControlPanelServlet.LIZARD_ENCLOSURE_ID);
        LizardEnclosure enclosure = (LizardEnclosure) attribute;
        GpioPinDigitalOutput humidifierPin = enclosure.humidifierPin;
        
        String parameter = request.getParameter("power");
                                        
        switch(parameter)
        {
            case "on":
            {
                humidifierPin.high();
                break;                
            }
            case "off":
            {
                humidifierPin.low();
                break;
            }
            default:
            {
                System.err.println("The humidifier servlet was called, but not with a valid request: " + parameter);
            }
       }
        
//       request.setAttribute("cronTable", cronTable);
        
       ServletContext c = getServletContext();
       RequestDispatcher rd = c.getRequestDispatcher("/controls");
       rd.forward(request, response);
    }    
    
}
