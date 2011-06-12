package net.bioclipse.bioinformatics.ciba.wizards.phylip.Neighbor;

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
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFolderMainPage;

public class NeighborWizard  extends AbsCibaWizard {


	public static final String NEIGHBOR_PREFERENCES_KEY = new StringBuilder(Activator.PLUGIN_ID).append(".preferences").append(".neighborExecutable").toString();

	WizardNewFolderMainPage newFolderPage;
	NeighborWizardPage neighborPage;

	private IFolder runFolder = null;
	private boolean createNewFolder = false;
	private CibaJob cliJob;

	public NeighborWizard () {
		setWindowTitle("New Phylip Neighbor Wizard");
		
		// Create the "plotfile" and "outfile" files if they don't already exist. 
		try {
			
			/* The location of the files should be the root install directory (not sure why, but this
			 *  is just where phylip's execution directory is the root install dir).  Therefore, get
			 *  the root bundle directory, then get its first parent (the plugins dir), and then the 
			 *  parent of the plugins dir.
			 */
			URL url = Platform.asLocalURL(Platform.getBundle(Activator.PLUGIN_ID).getEntry("/"));
			File exec = (new File(url.toURI().getPath().toString())).getParentFile().getParentFile();
			
			// Either create the files, or don't .. doesn't matter as long as phylip can find it when needed.
			new File(exec.getAbsolutePath() + "/outtree").createNewFile();
			new File(exec.getAbsolutePath() + "/outfile").createNewFile();
			
		} catch (Exception e) {
			
		    MessageDialog.openError(getShell(), "Error creating the required files.", "Could not create the plotfile or outfile.\n" + e.getMessage());
		    
		}
	}

	
	@Override
	public List<AbsCibaWizardPage> getWorkflowWizardPages() {
		
		List<AbsCibaWizardPage> pages = new ArrayList<AbsCibaWizardPage>(2);
		pages.add(new NeighborWizardPage());
		
		return pages;
	}
	

	@Override
	public boolean prepareForFinish() {

		try{
			
			this.runFolder = this.newFolderPage.createNewFolder();
			
			// Create a handle on the output file
			IFile outputFile = getNewFile(this.runFolder, "neighborOutput.nbr");
			neighborPage.setOutputFileName(outputFile.getRawLocation().toOSString());
			
			
			// create a new data file (copied from the source .phy file)
			// create a new input file for the options
			// set up the command "phylip <inputParams.txt"
			
			InputStream input = new ByteArrayInputStream(neighborPage.getArgs().getBytes());
			IFile inputFile = getNewFile(this.runFolder, "neighborInput.txt");
			inputFile.create(input, true, null);

			// Build the command based on the parameters from each WizardPage
			String command = buildCommand();

			// Write the command used to generate the run out to a batch file
			//  Could also use "System.getenv("ComSpec")" to get the cmd.exe file, and then use cmd.exe as the external process
			IFile commandFile = getNewFile(this.runFolder, "neighborCommand.txt");
			commandFile.create(new ByteArrayInputStream(command.toString().getBytes()), false, null);

			
			// Create the job.
			this.cliJob = new CibaJob("Phylip 3.69 - " + this.runFolder.getName(),
					                           command,
					                           inputFile.getRawLocation().toFile());
			
		} catch (CoreException ce) {

			this.neighborPage.setErrorMessage("Unable to create a necessary resource in the workspace.  Please check the workspace folder permissions and re-run.");
			return false;
		}
		
		return true;
	
	}
	
	
	@Override
	public String buildCommand( Object ... args) {
		return CibaPreferences.getStore().get(NEIGHBOR_PREFERENCES_KEY, null);
	}

	
	@Override
	public void addPages() {
		addPage( newFolderPage );
		addPage( neighborPage );
	}

	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
//		this.createNewFolder = !((selection.getFirstElement() instanceof IFile) 
//				&& ((IFile)selection.getFirstElement()).getFileExtension().equals("dist"));

		// Initialize the pages to be added
		this.neighborPage = new NeighborWizardPage();

//		if ( createNewFolder ){
			this.newFolderPage = new WizardNewFolderMainPage("Create a new Folder for this Phylip run", selection);
//		} else {
//			this.runFolder = (IFolder) ( (IFile) selection.getFirstElement()).getParent();
//			this.neighborPage.setInputFile( ( (IFile) selection.getFirstElement()).getRawLocation().toOSString());
//		}

		for( IWizardPage page : this.getPages() ) {
			page.setWizard(this);
		}
	}
	
	
	@Override 
	public CibaJob getCLIJob() {
		return this.cliJob;
	}
}
