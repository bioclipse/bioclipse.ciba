package net.bioclipse.bioinformatics.ciba.wizards.clustalw;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.bioclipse.bioinformatics.ciba.Activator;
import net.bioclipse.bioinformatics.ciba.main.CibaJob;
import net.bioclipse.bioinformatics.ciba.main.CibaPreferences;
import net.bioclipse.bioinformatics.ciba.wizards.AbsCibaWizard;
import net.bioclipse.bioinformatics.ciba.wizards.AbsCibaWizardPage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFolderMainPage;

public class ClustalWizard extends AbsCibaWizard {

	public static final String CLUSTALW_PREFERENCES_KEY = new StringBuilder(Activator.PLUGIN_ID).append(".preferences").append(".clustalExecutable").toString();
	
	//WizardNewProjectCreationPage
	WizardNewFolderMainPage newFolderPage;
	SeqInputPage sequenceInput;
	PairwisePage pairwiseParams;
	MultAlignPage multAlignParams;
	private boolean createNewFolder = false;
	IFolder selectedFolder = null;
	IFile selectedFile = null;
	CibaJob cliJob;
	
	public ClustalWizard() {
		setWindowTitle("New ClustalW 2.0 Alignment Wizard");
	}

	public IFolder getWorkingDir() {
		return this.selectedFolder;
	}
	
	private void setWorkingDir(IFolder dir) {
		this.selectedFolder = dir;
	}
	
	
	@Override
	public List<AbsCibaWizardPage> getWorkflowWizardPages() {
		
		// Can't just use the parameterized pages - no guarantee of when "init()" will be called to initialize them.
		return Arrays.asList(new SeqInputPage(), new MultAlignPage(), new PairwisePage());
	}
	
	
	@Override
	public void addPages() {
		if(createNewFolder) {
			addPage( newFolderPage );
		}
		addPage( sequenceInput );
		addPage( pairwiseParams );
		addPage( multAlignParams );
	}
	
	@Override
	public boolean prepareForFinish() {
		
		try {
			if(createNewFolder) {
				setWorkingDir(this.newFolderPage.createNewFolder());
			} else if (selectedFolder != null) {
				setWorkingDir(selectedFolder);
			} else {
				return false;
			}
			
			
			String command;
			
			if(sequenceInput.isNewAlignment()) {

				IFile newFile;
				
				if(this.selectedFile != null) {
					
					newFile = this.selectedFile;
					
				} else {
					
					newFile = getNewFile(getWorkingDir(), "sequences.fasta");
					
					// Create the file now that it definitely doesn't exist
					newFile.create(new ByteArrayInputStream(sequenceInput.getNewAlignmentArgs().getBytes()), true, null);
				}
				
				// Build the command based on the parameters from each WizardPage
				command = buildCommand(newFile);
				
			} else {
				IFile profileOne = getNewFile(getWorkingDir(), "profileOne.aln");
				IFile profileTwo = getNewFile(getWorkingDir(), "profileTwo.aln");
				
				// Create the files now that they definitely doen't exist
				profileOne.create(new ByteArrayInputStream(sequenceInput.getProfileOneData().getBytes()), true, null);
				profileTwo.create(new ByteArrayInputStream(sequenceInput.getProfileTwoData().getBytes()), true, null);
				
				// Build the command based on the parameters from each WizardPage
				command = buildCommand(profileOne, profileTwo);
			}
			
			
			// Write the command used to generate the run out to 'command.txt'
			IFile commandFile = getWorkingDir().getFile("command.txt");
			commandFile.create(new ByteArrayInputStream(command.getBytes()), false, null);
			
			// Create and schedule the job.  The Eclipse Scheduler will handle actually executing the job
			//  when it has the resources to do so.
			this.cliJob = new CibaJob(new StringBuilder("ClustalW 2.0.12 - ").append(getWorkingDir().getName()).toString(), command);
			
			
		} catch (CoreException ce) {
			
			MessageDialog.openError(getShell(), 
					                "Error finishing ClusatlW Run",
					                "Unable to create a required file.  Please see message below:\n" + ce.getMessage());
			
			return false;
			
		}
        
		return true;
	}
	
	/**
	 * This needs to return an array for the phylip package - the args need to be separated out for the file piping.
	 * @param args
	 * @return
	 */
	public String buildCommand( Object ... args) {
		
		// Get the executable from the preferences store.
		String execPath = CibaPreferences.getStore().get(CLUSTALW_PREFERENCES_KEY, null);
		
		// Build the command
		StringBuilder commandBuilder = new StringBuilder();
		commandBuilder.append("\"").append(execPath).append("\"");
		
		if(sequenceInput.isNewAlignment()) {
			commandBuilder.append(" -INFILE=\"").append(new File(((IFile) args[0]).getLocationURI()).getAbsolutePath()).append("\"");	
		} else {
			commandBuilder.append(" -PROFILE -PROFILE1=\"").append(new File(((IFile) args[0]).getLocationURI()).getAbsolutePath()).append("\"");
			commandBuilder.append(" -PROFILE2=\"").append(new File(((IFile) args[1]).getLocationURI()).getAbsolutePath()).append("\"");
		}
		
		commandBuilder.append(" ").append(pairwiseParams.getArgs());
		commandBuilder.append(" ").append(multAlignParams.getArgs());
		
		return commandBuilder.toString();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		createNewFolder = !( (selection.getFirstElement() instanceof IFolder) || (selection.getFirstElement() instanceof IFile) );
		
		// Initialize the pages to be added
		if(createNewFolder) {
			this.newFolderPage = new WizardNewFolderMainPage("Create a new Folder for this run", selection);
			
		} else {
			
			if (selection.getFirstElement() instanceof IFolder) {
				this.selectedFolder = (IFolder) selection.getFirstElement();
				
			} else {
				this.selectedFile = (IFile) selection.getFirstElement();
				this.selectedFolder = (IFolder) this.selectedFile.getParent();
			}
		}
		
		this.sequenceInput = new SeqInputPage();
		this.pairwiseParams = new PairwisePage();
		this.multAlignParams = new MultAlignPage();
		
		// Set the parent Wizard for each WizardPage
		for(IWizardPage page : this.getPages()) {
			page.setWizard(this);
		}
		
		// Indicate that this wizard should use the progress monitor.
		setNeedsProgressMonitor(true);
	}

	@Override
	public CibaJob getCLIJob() {
		return this.cliJob;
	}

	

	

}
