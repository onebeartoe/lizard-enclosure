
package org.onebeartoe.lizard.enclosure.servlet;

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

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

@WebServlet(urlPatterns = {"/control-panel"} )
public class ControlPanelServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    
    public static final String humidifierId = "Humidifier";
    
    public static final String ultravioletLightsId = "UltravioletLights";
            
    GpioController gpio;

    GpioPinDigitalOutput humidifierPin;
    
    GpioPinDigitalOutput uvLightPin;
    
    @Inject
    private LizardEnclosure enclosure;
    
    public ControlPanelServlet() 
    {
        super();
//        SystemInfo.getProcessor();
        
        try
        {
            gpio = GpioFactory.getInstance();            
        
            humidifierPin = gpio.provisionDigitalOutputPin( RaspiPin.GPIO_01, 
                                                            humidifierId, 
                                                            PinState.LOW);
            System.out.println("control panel servlet constructor");
ServletContext servletContext     =   getServletConfig().getServletContext();
//            ServletContext servletContext = getServletContext();
           servletContext.setAttribute(humidifierId, humidifierPin);
            
            
            uvLightPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, 
                                                        ultravioletLightsId, 
                                                        PinState.LOW);
            
            servletContext.setAttribute(ultravioletLightsId, uvLightPin);
        }
        catch(UnsatisfiedLinkError e)
        {
            System.err.println("An error occured while provisioning the GPIO pins.");
            e.printStackTrace();            
        }

    }

    @Override
    public void destroy()
    {
        gpio.shutdown();
    }
    
//    public synchronized void close() 
//    {
//    	gpio.shutdown();
//    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        String power = request.getParameter("uvLight");

// not what we want!        
        // Send the appropriate message to the GPIO on the Pi
        if( power != null && humidifierPin != null && uvLightPin != null) 
        {
            if( power.equalsIgnoreCase("on") ) 
            {
                humidifierPin.high();
                uvLightPin.high();                
            }
            else if( power.equalsIgnoreCase("off") )
            {
                humidifierPin.low();
                uvLightPin.low();
            }
        }
        
        request.setAttribute("uvLightState", power);
        
        ServletContext c = getServletContext();
        RequestDispatcher rd = c.getRequestDispatcher("/control-panel.jsp");
        rd.forward(request, response);
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

}
