
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
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.onebeartoe.electronics.photorama.Camera;
import org.onebeartoe.electronics.photorama.PhotoramaModes;
import org.onebeartoe.electronics.photorama.RaspberryPiCamera;
import org.onebeartoe.lizard.enclosure.LizardEnclosureSevice;
import org.onebeartoe.lizard.enclosure.RaspberryPiLizardEnclosureSevice;

@WebServlet(urlPatterns = {"/controls"}, loadOnStartup = 1)
public class ControlPanelServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    
    public static final String LIZARD_ENCLOSURE_ID = "lizardEnclosure";
    
    public static final String humidifierId = "Humidifier";
    
    public static final String ultravioletLightsId = "UltravioletLights";
            
    private GpioController gpio;
    
    private static GpioPinDigitalOutput uvLightPin;
    
    private Logger logger;
    
    private LizardEnclosureSevice lizardEnclosureSevice;
    
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
    
    public ServletContext getApplicationContext() throws Exception
    {
        ServletConfig servletConfig = getServletConfig();

        ServletContext servletContext = servletConfig.getServletContext();              
        
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

    public static void humidifier(LizardEnclosure enclosure, HttpServletRequest request) 
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
                 humidifierImagePath = "on.png";
                 
                 humidifierNextState = "off";
            }
            else
            {
                humidifierImagePath = "off.png";
                
                humidifierNextState = "on";
            }
        }

        request.setAttribute("humidifierNextState", humidifierNextState);
        request.setAttribute("humidifierImagePath", humidifierImagePath);
    }    
    
    @Override
    public void init()
    {
        System.out.println("control panel servlet initialization");

        logger = Logger.getLogger(ControlPanelServlet.class.getName());
        
        LizardEnclosure enclosure = new LizardEnclosure();
                
        Camera camera = initializeCamera(enclosure);

        lizardEnclosureSevice = new RaspberryPiLizardEnclosureSevice(camera);
        
        try
        {
            gpio = GpioFactory.getInstance();        
        
// move this provistion call to the service
            // This is GPIO pin #12 on the Raspberry Pi header.
            GpioPinDigitalOutput humidifierPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, 
                                                            humidifierId, 
                                                            PinState.LOW);                        

// move this provistion call to the service      
            // This is GPIO pin #16 on the Raspberry Pi header.
            uvLightPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, 
                                                        ultravioletLightsId, 
                                                        PinState.LOW);            
                        
            // selfies
// uncomment the next 3 lines to enable selfies            
//            GpioPinListenerDigital selfieListener = lizardEnclosureSevice.newSelfieListener();            
//            GpioPinDigitalInput selfieSensorPin = lizardEnclosureSevice.provisionSelfiePin(gpio);
//            selfieSensorPin.addListener(selfieListener);
            
            enclosure.humidifierPin = humidifierPin;
            enclosure.uvLightPin = uvLightPin;
// Uncomment this next line, if/when selfies are re-enabled.            
//            enclosure.selfieSensorPin = selfieSensorPin;
        }
        catch(UnsatisfiedLinkError e)
        {
            System.err.println("An error occured while provisioning the GPIO pins.");
            e.printStackTrace();            
        }
        
        try 
        {
            ServletContext servletContext = getApplicationContext();
            servletContext.setAttribute(LIZARD_ENCLOSURE_ID, enclosure);
            servletContext.setAttribute(ultravioletLightsId, uvLightPin);
        } 
        catch (Exception ex) 
        {
            logger.log(Level.SEVERE, null, ex);
        }        
    }
    
    private Camera initializeCamera(LizardEnclosure enclosure)
    {
        final Camera camera = new RaspberryPiCamera();
        camera.setMode(PhotoramaModes.FOOT_PEDAL);
        
        enclosure.camera = camera;
        
        return camera;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {            
        ServletContext servletContext = getServletContext();
        LizardEnclosure enclosure = (LizardEnclosure) servletContext.getAttribute(LIZARD_ENCLOSURE_ID);
        
        humidifier(enclosure, request);
        
        uvLight(request);
        
        String DEFAULT_PAGE = "/controls/index.jsp";        
        RequestDispatcher rd = servletContext.getRequestDispatcher(DEFAULT_PAGE);
        rd.forward(request, response);
    }
    
    /**
     * correct this the so that it is used the way that humidity() is used
     * @param the HTTP request received 
     */
    public static void uvLight(HttpServletRequest request)
    {
        String power = request.getParameter("uvLight");
        String imagePath = "unknown.png";
        // Send the appropriate message to the GPIO on the Pi
        if( power != null && uvLightPin != null)
        {
            if( power.equalsIgnoreCase("on") ) 
            {
                uvLightPin.high();                
                
                imagePath = "on.png";
            }
            else if( power.equalsIgnoreCase("off") )
            {
                uvLightPin.low();
                
                imagePath = "off.png";
            }
        }
        
        request.setAttribute("uvLightState", power);
        request.setAttribute("uvLightsImagePath", imagePath);
    }
}
