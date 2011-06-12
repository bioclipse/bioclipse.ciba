package net.bioclipse.bioinformatics.ciba.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

public class CibaJob extends Job {

	String command = null;
	
	File inputFile = null;
	
	/**	  The default timeout is 24 hours   **/
	long jobTimeout = 24*60*60*1000;
	
	ExecuteWatchdog watchdog = null;
	
	/*
	 * Force the user to use a non-default constructor
	 */
	private CibaJob(String name) {
		super(name);
		setUser(true);
	}
	
	/**
	 * Create a new Bioclipse Job for an external executable with the given name and
	 *   the given command.  Also define the default timeout for this job should wait
	 *   before recognizing the process a rogue/zombie process and killing it.
	 * @param name The name for the Bioclipse Job to be displayed in the "Process" view.
	 * @param cmd The external process/executable to run.  Typically an executable file
	 *            on the OS Filesystem.
	 */
	public CibaJob(String name, String cmd) {
		this(name);
		this.command = cmd;
	}
	
	/**
	 * Create a new Bioclipse Job for an external executable with the given name and
	 *   the given command.  Also define the default timeout for this job should wait
	 *   before killing it.
	 * @param name The name for the Bioclipse Job to be displayed in the "Process" view.
	 * @param cmd The external process/executable to run.  Typically an executable file
	 *            on the OS Filesystem.
	 * @param timeout The time limit to give the process before it is killed.
	 */
	public CibaJob(String name, String cmd, long timeout) {
		this(name);
		this.command = cmd;
		this.jobTimeout = timeout;
	}
	
	/**
	 * Create a new Bioclipse Job for an external executable with the given name and
	 *   the given command.  Also define the default timeout for this job should wait
	 *   before killing it.
	 * @param name The name for the Bioclipse Job to be displayed in the "Process" view.
	 * @param cmd The external process/executable to run.  Typically an executable file
	 *            on the OS Filesystem.
	 * @param timeout The time limit to give the process before it is killed.
	 * @param inFile The input file to use.  Typically this is to the right of the piped-input
	 *               symbol such as given in the form <code>"command.exe < inFile"</code>
	 */
	public CibaJob(String name, String cmd, long timeout, File inFile) {
		this(name);
		this.command = cmd;
		this.jobTimeout = timeout;
		this.inputFile = inFile;
	}
	
	/**
	 * Create a new Bioclipse Job for an external executable with the given name and
	 *   the given command.
	 * @param name The name for the Bioclipse Job to be displayed in the "Process" view.
	 * @param cmd The external process/executable to run.  Typically an executable file
	 *            on the OS Filesystem.
	 * @param inFile The input file to use.  Typically this is to the right of the piped-input
	 *               symbol such as given in the form <code>"command.exe < inFile"</code>
	 */
	public CibaJob(String name, String cmd, File inFile) {
		this(name);
		this.command = cmd;
		this.inputFile = inFile;
	}
	
	
	
	/**
	 * Get the name of the Ciba Job.
	 * @return
	 */
	public String getJobName() {
		return super.getName();
	}
	
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		if(command == null) {
			throw new IllegalArgumentException("Command must be defined before scheduling the job.");
		}
		
		try {
			// Get a handle on the Bioclipse Console Manager
			IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
            MessageConsole ioc = new MessageConsole(getJobName(), null);
            manager.addConsoles(new IConsole[] {ioc});
            manager.showConsoleView(ioc);
            
            // Create the CommandLine executable, and get a DefaultExecutor.
            CommandLine cl = CommandLine.parse(this.command);
            DefaultExecutor executor = new DefaultExecutor();
            
            // Set the execution timeout
    		this.watchdog = new ExecuteWatchdog(this.jobTimeout);
    		executor.setWatchdog(this.watchdog);
    		
    		// Link the executable's outputstream to the MessageConsole in Bioclipse, and if the user indicated
    		//  the input will be coming from a file, then set the executable's inputstream as well.
    		PumpStreamHandler streamHandler;
            if(this.inputFile != null) {
            	// TODO: Don't link this to System.err -- instead link it to the logger??
        		streamHandler = new PumpStreamHandler(ioc.newOutputStream(), System.err, new FileInputStream(inputFile));
                
            } else {
            	streamHandler = new PumpStreamHandler(ioc.newOutputStream());
            }
            
            executor.setStreamHandler(streamHandler);
            
            // Set the exit values to accept 0 or 1 for better control of the error popup - forcibly halting should display
            //  a more specific message.
            executor.setExitValues(new int[]{0, 1});
            
            // Capture the execution results.
            // NOTE: This method call is blocking
            int returnVal = executor.execute(cl);
            
            if (returnVal == 1) {
            	String message = "The CIBA Job " + getName() + " was forcibly halted.";
    			return new Status(Status.ERROR, net.bioclipse.bioinformatics.ciba.Activator.PLUGIN_ID, message);
            }
			
			
		} catch (IOException e) {
			String message = "Error occured while reading the input file for the CIBA Job: " + e.getMessage();
			return new Status(Status.ERROR, net.bioclipse.bioinformatics.ciba.Activator.PLUGIN_ID, message, e);
		} 
		
		return Status.OK_STATUS;
	}
	
	@Override
	protected void canceling() {
		this.watchdog.destroyProcess();
	}
}
