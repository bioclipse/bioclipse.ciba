package net.bioclipse.bioinformatics.ciba.wizards.phylip.Drawtree;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.bioinformatics.ciba.Activator;
import net.bioclipse.bioinformatics.ciba.main.CibaJob;
import net.bioclipse.bioinformatics.ciba.main.CibaPreferences;
import net.bioclipse.bioinformatics.ciba.wizards.AbsCibaWizard;
import net.bioclipse.bioinformatics.ciba.wizards.AbsCibaWizardPage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFolderMainPage;

public class DrawtreeWizard extends AbsCibaWizard {
	public static final String DRAWTREE_PREFERENCES_KEY = new StringBuilder(Activator.PLUGIN_ID).append(".preferences").append(".drawtreeExecutable").toString();

	WizardNewFolderMainPage newFolderPage;
	DrawtreeWizardPage drawtreeWizardPage;
	IFolder runFolder;
	private CibaJob cliJob;

	public DrawtreeWizard() {
		setWindowTitle("New Phylip Drawtree Wizard");
		
		// Create the "outtree" file if it doesn't already exist.
		try {
			
			/* The location of "outtree" should be the root install directory (not sure why, but this
			 *  is just where phylip's execution directory is the root install dir).  Therefore, get
			 *  the root bundle directory, then get its first parent (the plugins dir), and then the 
			 *  parent of the plugins dir.
			 */
			URL url = Platform.asLocalURL(Platform.getBundle(Activator.PLUGIN_ID).getEntry("/"));
			File exec = (new File(url.toURI().getPath().toString())).getParentFile().getParentFile();
			
			// Either create the file, or don't .. doesn't matter as long as phylip can find it when needed.
			new File(exec.getAbsolutePath() + "/plotfile").createNewFile();
			
		} catch (Exception e) {
			
		    MessageDialog.openError(getShell(), "Error creating \"outtree\".", "Could not create the outtree file.\n" + e.getMessage());
		    
		}
	}
	
	
	@Override
	public List<AbsCibaWizardPage> getWorkflowWizardPages() {
		List<AbsCibaWizardPage> pages = new ArrayList<AbsCibaWizardPage>(2);
		pages.add(new DrawtreeWizardPage());
		
		return pages;
	}

	@Override
	public void addPages() {
		addPage( this.newFolderPage );
		addPage( this.drawtreeWizardPage );
	}


	@Override
	public boolean prepareForFinish() {

		try{
			// TODO - Check for null here?  What if the user clicks finish, but it errors, and then
			//        they fix the error and click finish again?
			this.runFolder = this.newFolderPage.createNewFolder();
			
			IFile outputFile = getNewFile(this.runFolder, "drawtreeOutput.bmp");
			drawtreeWizardPage.setOutputFileName(outputFile.getRawLocation().toOSString());
			
			InputStream input = new ByteArrayInputStream(drawtreeWizardPage.getArgs().getBytes());
			
			// Get a handle on a new input file and create it with the actual argument input.
			IFile inputFile = getNewFile(this.runFolder, "drawtreeInput.txt");
			inputFile.create(input, true, null);
			
			// Build the command based on the parameters from each WizardPage
			String command = buildCommand();

			// Write the command used to generate the run out to a batch file
			//  Could also use "System.getenv("ComSpec")" to get the cmd.exe file, and then use cmd.exe as the external process
			IFile commandFile = getNewFile(this.runFolder, "drawtreeCommand.txt");
			commandFile.create(new ByteArrayInputStream(command.toString().getBytes()), false, null);

			// Create and schedule the job.  The Eclipse Scheduler will handle actually executing the job
			//  when it has the resources to do so.
			this.cliJob = new CibaJob("Phylip 3.69 - " + this.runFolder.getName(), command, inputFile.getRawLocation().toFile());
			
		} catch (CoreException ce) {

			this.drawtreeWizardPage.setErrorMessage("Unable to create a necessary resource in the workspace.  Please check the workspace folder permissions and re-run.");
			return false;
		}

		return true;
	}


	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

		this.newFolderPage = new WizardNewFolderMainPage("Phylip Drawtree - Folder Creation", selection);
		this.drawtreeWizardPage = new DrawtreeWizardPage();
	}
	

	@Override
	public String buildCommand(Object ... args) {
		return CibaPreferences.getStore().get(DRAWTREE_PREFERENCES_KEY, null);
	}

	
	@Override
	public CibaJob getCLIJob() {
		return this.cliJob;
	}
}
