
package org.onebeartoe.lizard.enclosure.controls;

import org.onebeartoe.lizard.enclosure.LizardEnclosure;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

@WebServlet(urlPatterns = {"/controls"}, loadOnStartup = 1)
public class ControlPanelServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    
    public static final String LIZARD_ENCLOSURE_ID = "lizardEnclosure";
    
    public static final String humidifierId = "Humidifier";
    
    public static final String ultravioletLightsId = "UltravioletLights";
            
    private GpioController gpio;
    
    private GpioPinDigitalOutput uvLightPin;
    
    @Override
    public void destroy()
    {
        gpio.shutdown();
    }
    
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        processRequest(request, response);
    }
    
//    @Override
    public ServletContext getApplicationContext() throws Exception
    {
        
/*        
        ServletContext servletContext;
*/        
        ServletConfig servletConfig = getServletConfig();
/*        
        if(servletConfig == null)
        {
            servletContext = getServletContext();
        }
        else
        {
            servletContext = servletConfig.getServletContext();
        }
*/        
        ServletContext servletContext = servletConfig.getServletContext();              
//        ServletContext servletContext = getServletConfig().getServletContext();
        
        return servletContext;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() 
    {
        return "This is webapp to control and monitor a lizard enclosure.";
    }

    private void humidifier(LizardEnclosure enclosure, HttpServletRequest request) 
    {        
        String humidifierNextState;
        String humidifierImagePath;
        
        if(enclosure.humidifierPin == null)
        {
            humidifierNextState = "State Unknown";
            humidifierImagePath = "unknown.png";
        }
        else
        {
            if( enclosure.humidifierPin.isHigh() )
            {
                 humidifierNextState = "off";                 
                 humidifierImagePath = "off.png";
            }
            else
            {
                humidifierNextState = "on";
                humidifierImagePath = "on.png";
            }
        }

        request.setAttribute("humidifierNextState", humidifierNextState);
        request.setAttribute("humidifierImagePath", humidifierImagePath);
    }    
    
    @Override
    public void init()
    {
        System.out.println("control panel servlet initialization");
        
        LizardEnclosure enclosure = new LizardEnclosure();
        
        try
        {
            gpio = GpioFactory.getInstance();            
        
            GpioPinDigitalOutput humidifierPin = gpio.provisionDigitalOutputPin( RaspiPin.GPIO_01, 
                                                            humidifierId, 
                                                            PinState.LOW);
                        
            enclosure.humidifierPin = humidifierPin;
            
            uvLightPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, 
                                                        ultravioletLightsId, 
                                                        PinState.LOW);
        }
        catch(UnsatisfiedLinkError e)
        {
            System.err.println("An error occured while provisioning the GPIO pins.");
            e.printStackTrace();            
        }
        
        ServletContext servletContext;      
        try 
        {
            servletContext = getApplicationContext();
            //        ServletContext servletContext = getServletConfig().getServletContext();
            servletContext.setAttribute(LIZARD_ENCLOSURE_ID, enclosure);
        //            ServletContext servletContext = getServletContext();
//            servletContext.setAttribute(humidifierId, humidifierPin);
            servletContext.setAttribute(ultravioletLightsId, uvLightPin);
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(ControlPanelServlet.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {            
        ServletContext servletContext = getServletContext();

        LizardEnclosure enclosure = (LizardEnclosure) servletContext.getAttribute("lizardEnclosure");
        
        humidifier( enclosure, request);
        
        uvLight(request);
        
        String DEFAULT_PAGE = "/controls/index.jsp";        
//        ServletContext servletContext = getServletContext();
        RequestDispatcher rd = servletContext.getRequestDispatcher(DEFAULT_PAGE);
        rd.forward(request, response);
    }
    
    private void uvLight(HttpServletRequest request)
    {
        String power = request.getParameter("uvLight");

        // Send the appropriate message to the GPIO on the Pi
        if( power != null && uvLightPin != null)
        {
            if( power.equalsIgnoreCase("on") ) 
            {
                uvLightPin.high();                
            }
            else if( power.equalsIgnoreCase("off") )
            {
                uvLightPin.low();
            }
        }        
        request.setAttribute("uvLightState", power);
    }
    
}
