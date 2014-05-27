
package org.onebeartoe.lizard.enclosure.schedules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.onebeartoe.CommandResults;
import org.onebeartoe.Commander;

/**
 * @author Roberto Marquez
 */
public class CrontabService 
{
    
    public List<String> currentUserEntries()
    {
        List<String> entries = new ArrayList();
        
        String command = "crontab -l";
        
        Commander commander = new Commander(command);
        try 
        {
            commander.execute();
            List<String> stdout = commander.getStdout();
            entries.addAll(stdout);
            
            List<String> stderr = commander.getStderr();
            entries.addAll(stderr);
        } 
        catch (Exception ex) 
        {
            entries.add( ex.getMessage() );
            Logger.getLogger(CrontabService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return entries;
    }

    public String formattedCurrentUserEntries()
    {
        List<String> entries = currentUserEntries();
        StringBuilder sb = new StringBuilder();
        for(String e : entries)
        {
            sb.append(e);
            sb.append(System.lineSeparator());
        }
        
        return sb.toString();
    }
    
    public CommandResults updateCurrentUserCrontab(File crontabFile) throws Exception
    {        
//        List<String> output = new ArrayList();
        String command = "crontab " + crontabFile.getAbsolutePath();
        System.out.println("ucuc executing: " + command);
        Commander commander = new Commander(command);
        commander.execute();

        List<String> stdout = commander.getStdout();
        List<String> stderr = commander.getStderr();
        CommandResults results = new CommandResults();
        results.stderr = stderr;
        results.stdout = stdout;
        
        return results;
    }
    
}
