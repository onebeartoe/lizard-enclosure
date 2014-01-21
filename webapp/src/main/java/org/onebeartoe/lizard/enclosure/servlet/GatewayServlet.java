
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
import com.pi4j.system.SystemInfo;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

@WebServlet(urlPatterns = {"/control-panel"})
public class GatewayServlet extends HttpServlet 
{
    private static final long serialVersionUID = 1L;

    GpioController gpio;

    GpioPinDigitalOutput heatLampPin;
    
    GpioPinDigitalOutput uvLightPin;
    
    @Inject
    private LizardEnclosure enclosure;
    
    public GatewayServlet() 
    {
        super();
        
//        SystemInfo.getProcessor();
        
        try
        {
            gpio = GpioFactory.getInstance();
        
            heatLampPin = 
                gpio.provisionDigitalOutputPin( RaspiPin.GPIO_01, 
                                            "HeatLamp", 
                                            PinState.LOW);
        
            uvLightPin = 
                gpio.provisionDigitalOutputPin( RaspiPin.GPIO_04, 
                                            "UV-Light", 
                                            PinState.LOW);
        }
        catch(UnsatisfiedLinkError e)
        {
            System.err.println("An error occured while provisioning the GPIO pins.");
            e.printStackTrace();            
        }

    }

    public synchronized void close() 
    {
    	gpio.shutdown();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        String power = request.getParameter("uvLight");
        
        // Send the appropriate message to the GPIO on the Pi
        if( power != null && heatLampPin != null && uvLightPin != null) 
        {
            if( power.equalsIgnoreCase("on") ) 
            {
                heatLampPin.high();
                uvLightPin.high();                
            }
            else if( power.equalsIgnoreCase("off") )
            {
                heatLampPin.low();
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
            throws ServletException, IOException {
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
            throws ServletException, IOException {
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
