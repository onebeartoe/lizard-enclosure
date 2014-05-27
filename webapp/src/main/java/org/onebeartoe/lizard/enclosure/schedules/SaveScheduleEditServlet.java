
package org.onebeartoe.lizard.enclosure.schedules;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.onebeartoe.CommandResults;
import org.onebeartoe.Commander;

/**
 * @author Roberto Marquez
 */
@WebServlet(urlPatterns = {"/schedule/save"})
public class SaveScheduleEditServlet extends HttpServlet
{

    private CrontabService crontabService = new CrontabService();
    
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
        final String crontabPath = "/root/lizard-enclosure-crontab.text";
        File crontabFile = new File(crontabPath);
        String newSchedule = request.getParameter("newSchedlue");
        newSchedule = newSchedule.replace("\\cM", "");        
        
        FileOutputStream fos = new FileOutputStream(crontabFile, false);
        Charset cs = Charset.forName("UTF-8");
        try (OutputStreamWriter osw = new OutputStreamWriter(fos, cs)) 
        {
            osw.write(newSchedule);
            osw.write( System.lineSeparator() );
        }
        
        String saveMessage;
        String command = "dos2unix " + crontabFile.getAbsolutePath();
        System.out.println("executing: " + command);
        Commander commander = new Commander(command);
        try 
        {        
            commander.execute();
            
            try 
            {
                CommandResults results = crontabService.updateCurrentUserCrontab(crontabFile);
                saveMessage = "The edit to the cron table was successful! :" + results.toString();
            } 
            catch (Exception ex) 
            {
                saveMessage = ex.getMessage();
                Logger.getLogger(SaveScheduleEditServlet.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        } 
        catch (Exception ex) 
        {
            saveMessage = ex.getMessage();
            Logger.getLogger(SaveScheduleEditServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        request.setAttribute("saveMessage", saveMessage);
        
        ServletContext c = getServletContext();
        RequestDispatcher rd = c.getRequestDispatcher("/schedule");
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
}
