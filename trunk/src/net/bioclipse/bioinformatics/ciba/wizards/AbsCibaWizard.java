package net.bioclipse.bioinformatics.ciba.wizards;

import net.bioclipse.bioinformatics.ciba.main.CibaJob;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Implement this interface to indicate a class is a CIBA Wizard.  Implementing classes
 *  must provide an appropriate GUI Input component (<code>org.eclipse.swt.widgets.Composite</code>)
 *  as well as a method to get the appropriate CLI Arguments.
 * 
 * 
 * @author Daniel F. Surdyk, III
 */
public abstract class AbsCibaWizard extends Wizard implements INewWizard {

	/**
	 * This is an internal job to simply refresh the workspace resources
	 *  after the Command Line executable has completed.
	 */
	private Job refreshJob = new Job("Refresh Project Post Execution") {
		@Override 
		protected IStatus run(IProgressMonitor monitor) {
			try {
				// Join the wizard's job before refreshing so the refresh will wait for the
				//  wizard job to finish before commencing the refresh.
				AbsCibaWizard.this.getCLIJob().join();
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
				
			} catch (Exception e) {
				// Do nothing - if it can't join or refresh - then just return
				//  without doing anything.
			}

			return Status.OK_STATUS;
		}
	};
	
	/**
	 * Initialize resource-specific information for this wizard.  For example,
	 * if the user accessed the wizard via R-Clicking, it would be reflected
	 * in the <code>selection</code> argument.  Any fields that can be 
	 * pre-populated (such as the file to be imported) should be set in this 
	 * method.  This method should also, by convention, initialize each of the
	 * WizardPages used by this wizard. 
	 */
	@Override
	public abstract void init(IWorkbench wb, IStructuredSelection selection);

	
	/**
	 * Add the pages this wizard will use.  The order they are added here
	 * will be the same order they are displayed in the final Wizard.
	 */
	@Override
	public abstract void addPages();
	
	
	/**
	 * Perform any actions appropriate in response to the user having pressed the 
	 * Finish button, or refuse if finishing immediately and executing the CLIJob
	 * should not occur.  The CLIJob will not run if this method returns false. 
	 * @return
	 */
	public abstract boolean prepareForFinish();
	
	
	/**
	 * Build the command to be used for the CLIJob if needed.
	 * @param objects
	 * @return
	 */
	public abstract String buildCommand(Object ... objects);
	

	/**
	 * Create and return the CLIJob.
	 * @return The job that will ultimately execute the Command Line executable.
	 * <br><br>
	 * See the ICibaJob class for further information.
	 */
	public abstract CibaJob getCLIJob();
	
	
	
	
	
	/**
	 * This method should not be overridden by implementing classes.  It calls the
	 * <code>prepareForFinish()</code> method, schedules the CLIJob, and then 
	 * schedules the <code>refreshJob</code> to join the CLIJob.
	 * <br><br>
	 * <b>From IWizard's performFinish() documentation:</b>
	 * Performs any actions appropriate in response to the user having pressed the 
	 * Finish button, or refuse if finishing now is not permitted. Normally this 
	 * method is only called on the container's current wizard. However if the current 
	 * wizard is a nested wizard this method will also be called on all wizards in 
	 * its parent chain. Such parents may use this notification to save state etc. 
	 * However, the value the parents return from this method is ignored.
	 * 
	 */
	@Override
	public boolean performFinish() {
		
		// Call the finish method and save its value
		//  for the return below.
		boolean retVal = this.prepareForFinish();
		
		// If the prepareForFinish() method indicates this wizard should finish,
		//  then schedule the CLI Job with the refreshJob to follow.
		if(retVal) {
			
			if(this.getCLIJob() == null) {
				// TODO: Warn the user why this failed.
				return false;
			}
			this.getCLIJob().schedule();
			this.refreshJob.schedule();
		}
		
		return retVal;
	}
	
	
	/**
	 * Returns a new, non-existent file whose parent is the folder named in root.  Neither can be null.
	 * 
	 * @param root - The base folder the new file should be under
	 * @param fileName - The name of the new file.
	 * @return A new, non-existent file named from the <code>fileName</code> argument, and if the named
	 *         file already exists, then a file with a number appended to it
	 */
	protected static IFile getNewFile(IFolder root, String fileName) {
		IFile retFile = root.getFile(fileName);
		
		if(retFile.exists()) {
			String extension = fileName.substring(fileName.indexOf("."), fileName.length());
			
			// The file existed, so add a # to the file name
			for( int i=1; retFile.exists(); i++)
				retFile = root.getFile(fileName.replace(extension, ("(" + i + ")" + extension)));
		}
		
		return retFile;
	}
}
