
package org.onebeartoe.lizard.enclosure.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.onebeartoe.Commander;

@WebServlet(urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        Logger logger = Logger.getLogger(DashboardServlet.class.getName());
        
        StringBuilder stderr = null;
        StringBuilder stdout = null;
            
        String command = "crontab -l";
        Commander c = new Commander(command);
        try 
        {                                
            int exitCode = c.execute();
            stdout = c.getStdout();
            stderr = c.getStderr();
            
            request.setAttribute("stdout", stdout.toString() );
            request.setAttribute("stderr", stderr.toString() );
        } 
        catch (InterruptedException ex) 
        {
            logger.log(Level.SEVERE, null, ex);
        }
        
        
        
        ServletContext context = getServletContext();
        RequestDispatcher rd = context.getRequestDispatcher("/index.jsp");
        rd.forward(request, response);
    }
}