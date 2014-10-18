
package org.onebeartoe;

import java.util.List;

/**
 *
 * @author Roberto Marquez
 * @deprecated Use https://github.com/onebeartoe/java-libraries instead
 */
public class CommandResults 
{
    public List<String> stderr;
    
    public List<String> stdout;
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        for(String s : stdout)
        {
            sb.append(s + System.lineSeparator());
        }
        
        for(String s : stderr)
        {
            sb.append(s + System.lineSeparator());
        }
        
        return sb.toString();
    }
}
