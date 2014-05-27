
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
import org.onebeartoe.Commander;

/**
 * This servlet only has display responsibilities; all request are idempotent.
 * @author roberto
 */
@WebServlet(urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet
{
    Logger logger;

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
//        StringBuilder stderr = null;
//        StringBuilder stdout = null;
            
        String command = "crontab -l";
        Commander c = new Commander(command);
        try 
        {                                
            int exitCode = c.execute();
            List<String> stdoutList = c.getStdout();
            List<String> stderrList = c.getStderr();

            StringBuilder stderr = new StringBuilder();
            StringBuilder stdout = new StringBuilder();
            
            for(String s : stdoutList)
            {
                stdout.append(s);
            }
            
            for(String s : stderrList)
            {
                stderr.append(s);
            }
            
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
