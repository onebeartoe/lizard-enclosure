
package org.onebeartoe.lizard.enclosure.servlet;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Roberto Marquez
 */
@WebServlet(urlPatterns = {"/arduino/sensor/readings"}, loadOnStartup = 1)
public class ArduinoServlet extends HttpServlet implements SerialPortEventListener
{
    private Logger logger;
    
    private static List<String> messages;

    private SerialPort serialPort;

    /**
     * The port we're normally going to use.
     */
    private static final String [] PORT_NAMES = 
    {
            "/dev/tty.usbserial-A9007UX1", // Mac OS X
            "/dev/ttyACM0", // Raspberry Pi
            "/dev/ttyUSB0", // Linux
            "COM3", // Windows
    };

    /**
     * A BufferedReader which will be fed by a InputStreamReader converting
     * the bytes into characters making the displayed results codepage
     * independent
     */
    private BufferedReader input;

    /**
     * The output stream to the port
     */
    private OutputStream output;

    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;

    /**
     * Default bits per second for COM port.
     */
    private static final int DATA_RATE = 9600;

    @Override
    public void destroy()
    {
        
        if (serialPort != null) 
        {
                serialPort.removeEventListener();
                serialPort.close();
        }        
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        request.setAttribute("messages", messages);
                
        ServletContext context = getServletContext();
        RequestDispatcher rd = context.getRequestDispatcher("/arduino/sensor/readings/index.jsp");
        rd.forward(request, response);
    }    

    @Override
    public void init() throws ServletException 
    {
        super.init();
        
        logger = Logger.getLogger(DashboardServlet.class.getName());
        
        messages = new ArrayList();
        messages.add("servelt started up");
        
        // the next line is for Raspberry Pi and 
        // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) 
        {
                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
                System.out.println("port ID: " + currPortId);
                for (String portName : PORT_NAMES) 
                {
                        System.out.println("trying: " + portName);
                        if (currPortId.getName().equals(portName)) 
                        {
                                portId = currPortId;
                                break;
                        }
                }
        }
        if (portId == null) 
        {
                System.out.println("Could not find COM port.");
                return;
        }

        try 
        {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            // open the streams
            InputStream is = serialPort.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            input = new BufferedReader(isr);

            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } 
        catch (Exception e) 
        {
                System.err.println(e.toString());
        }        
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) 
    {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) 
        {
            try 
            {
                String inputLine = input.readLine();
                
                messages.add(inputLine);
                
                System.out.println(inputLine);
            } 
            catch (Exception e) 
            {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }    
    
}
