
package org.onebeartoe.lizard.enclosure.schedules;

import java.io.IOException;
import java.util.List;
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
@WebServlet(urlPatterns = {"/schedule"})
public class ViewScheduleServlet extends HttpServlet
{
    
    private CrontabService crontabService = new CrontabService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        doPost(request, response);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        String cronTable = "# y m d h m s command" + System.lineSeparator() + "* * * * * 3 curl http://localhost:8080/lizard-enclosure/control-panel?uvLight=on";
                
        String entries = crontabService.formattedCurrentUserEntries();
        request.setAttribute("cronTable", entries);
        
        ServletContext c = getServletContext();
        RequestDispatcher rd = c.getRequestDispatcher("/schedule/index.jsp");
        rd.forward(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() 
    {
        return "This is servlet to update the cron table schedule for the lizard enclosure.";
    }
    
    @Override
    public void init()
    {
        crontabService = new CrontabService();
    }
}
