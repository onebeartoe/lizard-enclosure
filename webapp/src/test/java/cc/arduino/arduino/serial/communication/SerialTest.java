    
package cc.arduino.arduino.serial.communication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.InputStream;
import java.util.Enumeration;

/**
  540  java -Djava.library.path=/usr/lib/jni -jar arduino-serial-communication-1.0-SNAPSHOT-jar-with-dependencies.jar 
  541  sudo java -Djava.library.path=/usr/lib/jni -jar arduino-serial-communication-1.0-SNAPSHOT-jar-with-dependencies.jar 
 */


/**
 * This was originally the code found at
 * http://playground.arduino.cc/Interfacing/Java
 *
 * @author Roberto Marquez
 */
public class SerialTest implements SerialPortEventListener 
{
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

        public void initialize() 
        {
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
         * This should be called when you stop using the port. This will prevent
         * port locking on platforms like Linux.
         */
        public synchronized void close() 
        {
                if (serialPort != null) 
                {
                        serialPort.removeEventListener();
                        serialPort.close();
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
                                System.out.println(inputLine);
                        } catch (Exception e) {
                                System.err.println(e.toString());
                        }
                }
                // Ignore all the other eventTypes, but you should consider the other ones.
        }

        public static void main(String[] args) throws Exception 
        {
                System.out.println("Starting...");
                SerialTest main = new SerialTest();
                main.initialize();
                Thread t = new Thread() 
                {
                        public void run() 
                        {
                                //the following line will keep this app alive for 1000 seconds,
                                //waiting for events to occur and responding to them (printing incoming messages to console).
                                try 
                                {
                                        Thread.sleep(10,000);
                                } 
                                catch (InterruptedException ie) 
                                {
                                        ie.printStackTrace();
                                }
                        }
                };
                t.start();
                System.out.println("Started");
        }
}
