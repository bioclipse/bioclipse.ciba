package net.bioclipse.bioinformatics.ciba.wizards.phylip.DNADist;

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

public class DNADistWizard extends AbsCibaWizard {

	public static final String DNADIST_PREFERENCES_KEY = new StringBuilder(Activator.PLUGIN_ID).append(".preferences").append(".dnaDistExecutable").toString();
	
	public static enum DistanceModel { F84, KIMURA, JUKES, LOG_DET, SIMILARITY}
	
	private WizardNewFolderMainPage newFolderPage;
	private DNADistWizardPage dnaDistPage;
	private IFolder runFolder = null;
	private CibaJob cliJob;
	
	public DNADistWizard() {
		setWindowTitle("New Phylip DNADist Wizard");
		
		// Create the "outfile" file if it doesn't already exist.
		try {
			
			/* The location of "outfile" should be the root install directory (not sure why, but this
			 *  is just where phylip's execution directory is the root install dir).  Therefore, get
			 *  the root bundle directory, then get its first parent (the plugins dir), and then the 
			 *  parent of the plugins dir.
			 */
			URL url = Platform.asLocalURL(Platform.getBundle(Activator.PLUGIN_ID).getEntry("/"));
			File exec = (new File(url.toURI().getPath().toString())).getParentFile().getParentFile();
			
			// Either create the file, or don't .. doesn't matter as long as phylip can find it when needed.
			new File(exec.getAbsolutePath() + "/outfile").createNewFile();
			
		} catch (Exception e) {
			
		    MessageDialog.openError(getShell(), "Error creating \"outfile\".", "Could not create the outfile.\n" + e.getMessage());
		    
		}
	}
	
	@Override
	public List<AbsCibaWizardPage> getWorkflowWizardPages() {
		List<AbsCibaWizardPage> pages = new ArrayList<AbsCibaWizardPage>(2);
		
		// Can't just use the "dnaDistPage" variable - no guarantee of when "init()" is called.
		pages.add(new DNADistWizardPage(this.runFolder));
		
		return pages;
	}
	
	@Override
	public void addPages() {
		addPage( newFolderPage );
		addPage( dnaDistPage );
	}

	@Override
	public boolean prepareForFinish() {
		try{
			
			this.runFolder = this.newFolderPage.createNewFolder();
			

			// Create a handle on the output file
			IFile outputFile = AbsCibaWizard.getNewFile(this.runFolder, "drawtreeOutput.dist");
			dnaDistPage.setOutputFileName(outputFile.getRawLocation().toOSString());
			
			
			// create a new data file (copied from the source .phy file)
			// create a new input file for the options
			// set up the command "phylip <inputParams.txt"
			InputStream input = new ByteArrayInputStream(dnaDistPage.getArgs().getBytes());
			
			// Get a handle on the new input-file and then create it
			IFile inputFile = getNewFile(this.runFolder, "dnaDistInput.txt");
			inputFile.create(input, true, null);
			
			// Build the command based on the parameters from each WizardPage
			String command = buildCommand();

			// Write the command used to generate the run out to a batch file
			IFile commandFile = getNewFile(this.runFolder, "dnaDistCommand.txt");
			commandFile.create(new ByteArrayInputStream(command.toString().getBytes()), false, null);

			// Create and schedule the job.  The Eclipse Scheduler will handle actually executing the job
			//  when it has the resources to do so.
			this.cliJob = new CibaJob("Phylip 3.69 - " + this.runFolder.getName(), command, inputFile.getRawLocation().toFile());
			
		} catch (CoreException ce) {

			this.dnaDistPage.setErrorMessage("Unable to create a necessary resource in the workspace.  Please check the workspace folder permissions and re-run.");
			return false;
		}
		
		return true;
	}
	
	public String buildCommand( Object ... args) {
		return CibaPreferences.getStore().get(DNADIST_PREFERENCES_KEY, null);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		// Initialize the pages to be added
		Object obj = selection.getFirstElement();
		if(obj instanceof IFolder) {
			this.dnaDistPage = new DNADistWizardPage((IFolder)obj);
		} else {
			this.dnaDistPage = new DNADistWizardPage( null );
		}
		
		this.newFolderPage = new WizardNewFolderMainPage("Create a new Folder for this Phylip run", selection);
		
	}

	@Override
	public CibaJob getCLIJob() {
		return this.cliJob;
	}
	
	
	

}
