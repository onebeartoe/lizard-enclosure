
package org.onebeartoe.lizard.enclosure.arduino;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
@WebServlet(urlPatterns = {"/arduino/sensor/readings/raw"})
public class RawArduinoServlet extends HttpServlet
{
    private Logger logger;
    
        @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {        
        String s = request.getParameter("lastId");
        if(s == null || s.trim().equals(""))
        {
            s = "0";
        }
        long lastId = Long.valueOf(s);
        
        List<String> newMessages = new ArrayList();
        ServletContext context = getServletContext();
        List<String> allMessages = (List<String>) context.getAttribute(ArduinoServlet.ARDUINO_MESSAGES);
        for(String message : allMessages)
        {
            try
            {
                ArduinoMessage am = ArduinoMessage.fromLine(message);
                if(am.id > lastId)
                {
                    newMessages.add(message);
                }
            }
            catch(Exception e)
            {
                logger.log(Level.SEVERE, e.getMessage(), e.getCause());
            }
        }
        
        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) 
        {
            for(String message : newMessages)
            {
                out.println(message);
            }
        }
    }    

    @Override
    public void init() throws ServletException 
    {
        super.init();
        
        logger = Logger.getLogger(RawArduinoServlet.class.getName());
    }
}
