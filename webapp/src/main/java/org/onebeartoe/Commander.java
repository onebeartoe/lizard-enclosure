
package onebeartoe;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Commander 
{
	private String command;
	
	private StringBuilder stdout;
	
	public StringBuilder getStdout() {
		return stdout;
	}

	public StringBuilder getStderr() {
		return stderr;
	}


	private StringBuilder stderr;

	static final String defaultFile = "errors.txt"; 
             // This is the name of the file that is used for outputting
             // error messages, unless the user specifies a different name with 
             // the "-f" option.                  
 
	public Commander(String command)
	{
		this.command = command;
		stdout = new StringBuilder();
		stderr = new StringBuilder();
	}
	
	public int execute() throws IOException, InterruptedException 
	{
		String os = System.getProperty("os.name");
		if( os.contains("Windows") && ! command.toLowerCase().startsWith("cmd /c") )
		{
			command = "cmd /C " + command;				
		}
	   
		Process jobProcess = null;
		Runtime runtime = Runtime.getRuntime();		
		jobProcess = runtime.exec(command);
		jobProcess.waitFor();				
		
		// read the output from the command
		InputStream instream = jobProcess.getInputStream();
		InputStreamReader insteamReader = new InputStreamReader(instream);
		BufferedReader stdInput = new BufferedReader(insteamReader);		
		String s = null;
		System.out.println("Here is the standard output of the command:\n");
		while ((s = stdInput.readLine()) != null) 
		{
			stdout.append(s);
			System.out.println(s);				
		}

		// read any errors from the attempted command
		InputStream is = jobProcess.getErrorStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader stdError = new BufferedReader(isr);
		System.out.println("Here is the standard error of the command (if any):\n");
		while ((s = stdError.readLine()) != null) 
		{
			stderr.append(s);
			System.out.println(s);
		}
			
		int exitCode = jobProcess.exitValue();
		
		return exitCode;
   }
   
   public static void executeCommand(String args) 
   {


      String errorFile = defaultFile;  // The name of the file to which error 
                                       //              messages will be written.

      String command = args;  
//      String command = "notepad order5.txt";  // The words in the command line to be executed,
                                              //    consising of the command and the file name(s)
      
      Process compiler;  // A process object that will run the javac compiler.
      
      InputStream errorInput;   // A stream for reading error output from 
                                //    the compiler process.
      
      PrintStream errorOutput = null;  // A stream for writing error messages to the
                                       //    file.  Lines from the errorInput stream
                                       //    are copied to this stream.
                                       
      boolean foundErrors = false;   // This will be set to true if any error messages
                                     //    are produced by the compiler.
      
      String errorline = null;  // One line of error output from the compiler process.
                                // (The last line of output is written to the screen
                                // as well as to the file.  It should contain a message
                                // such as "17 errors, 1 warning".)
      
      Runtime runtime = Runtime.getRuntime();  // The Runtime object, which is needed to
                                               //    create a process.

/*      int paramStart = 0;  // Position of (first) file name in the command-line parameter
                           //   array.  This is set to 2 if the parameters begin with
                           //   a "-f" option. */  
   
      
      try {
            // Create a stream for writing the error messages to a file.
            // If an error occurs, errorOutput is null.
         errorOutput = new PrintStream(new FileOutputStream(errorFile));
      }
      catch (IOException e) {
         errorOutput = null;
      }

      try {
            // Create the compiler process to run the compiler, and get a stream for
            // reading the error messages from the compiler process.
         compiler = runtime.exec(command);
         errorInput = compiler.getErrorStream();
      }
      catch (Exception e) {
         System.out.println("*** Error while trying to start the compiler:");
         System.out.println(e.toString());
         if (errorOutput != null)
            errorOutput.println("*** Error while trying to start the compiler.");
         return;
      }

      try {         
             // Read error messages, if any, from the compiler process, and
             // write them to the file.
         checkLF = false;
         while (true) {
            String line = readLine(errorInput);
            if (line == null)  // Signal that all data has been read from the stream.
               break;
            foundErrors = true;
            errorline = line;  // For saving the LAST line of the error messages.
            if (errorOutput == null) {
                   // errorOutput is null only if the output stream to the file
                   // couldn't be created, AND this is the first error message.
                   // In this case, write the errors to standard output.
               errorOutput = System.out;
               System.out.println("*** Can't open file \"" 
                                + errorFile + "\" -- sending errors to standard out.\n");
               errorFile = "Standard Output";
            }
            errorOutput.println(line.trim());
         } 
      }
      catch (Exception e) {
         System.out.println("*** Error while trying to get error messages from call to notpad:");
         System.out.println(e.toString());
         if (foundErrors && errorOutput != null)
            errorOutput.println("*** Error while trying to get error messages from call to notpad.");
         return;
      }
      
      try {
            // Wait for the compiler process to finish.  (Actually, it should
            // already be done when the program gets here...)
         compiler.waitFor();
      }
      catch (Exception e) {
         System.out.println("*** Error while waiting for notpad to finish.");
         System.out.println("*** Output might be incorrect or incomplete.");
         return;
      }
      
      if (foundErrors == false) {
         System.out.println("call to notpad finished with no errors.");
         errorOutput.println("No errors");
      }
      else {
         System.out.println("call to notpad finished with errors.  Error messages sent to " + errorFile + ".");
         System.out.println(errorline);
      }
      
   } // end main()
   
   
   static boolean checkLF;  // A kludge to take care of the fact that text files
                            // on different files can have different formats.  Lines can end
                            // with either a carriage return, or a line feed, or a carriage
                            // return followed by a line feed.  This variable is used by the
                            // following subroutine so that it can remember to throw away
                            // a line feed that follows a carriage return, rather than
                            // treat it as an empty line.
   
   static String readLine(InputStream in) {
         // This subroutine reads one line from the input stream, in.
         // If the end-of-stream has been reached, null is returned.
         // (Null is also returned if an input error occurs.)
      try {
         int ch = in.read();
         if (checkLF && ch == '\n')
            ch = in.read();
         if (ch == -1)
            return null;
         StringBuffer b = new StringBuffer();
         while (ch != -1 && ch != '\r' && ch != '\n') {
            b.append( (char)ch );
            ch = in.read();
         }
         return b.toString();
      }
      catch (IOException e) {
         return null;
      }
   }
      

}  // end class cef
