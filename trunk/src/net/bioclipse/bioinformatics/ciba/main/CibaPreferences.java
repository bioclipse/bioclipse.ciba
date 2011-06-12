package net.bioclipse.bioinformatics.ciba.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import net.bioclipse.bioinformatics.ciba.Activator;
import net.bioclipse.bioinformatics.ciba.wizards.clustalw.ClustalWizard;
import net.bioclipse.bioinformatics.ciba.wizards.phylip.DNADist.DNADistWizard;
import net.bioclipse.bioinformatics.ciba.wizards.phylip.Drawtree.DrawtreeWizard;
import net.bioclipse.bioinformatics.ciba.wizards.phylip.Neighbor.NeighborWizard;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class CibaPreferences extends AbstractPreferenceInitializer {

	public static final String CONTINUE_PROCS_POST_CLOSE = "net.bioclipse.bioinformatics.ciba.runProcessesAfterClose";
	public static final String EXECUTABLE_FILE_EXTENSION = "net.bioclipse.bioinformatics.ciba.execFileExtension";
	
	private static Preferences preferences;
	
	public static Preferences getStore() {
		// Lazy Loading - so the preferences are only loaded once.
		//  At the same time, it is assured this will never return null.
		if(CibaPreferences.preferences == null) {
			CibaPreferences.preferences = Platform.getPreferencesService()
			                                      .getRootNode()
			                                      .node("instance")
			                                      .node(Activator.PLUGIN_ID);
			
		}
		
		return CibaPreferences.preferences;
	}
	
	public static void setValue(String key, String value) {
		getStore().put(key, value);
		save();
	}
	
	public static void save() {
		
		try { getStore().flush(); }
		catch (BackingStoreException bse) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					                "Error saving preferences", bse.getMessage());
		}
	}
	
	
	public static void storeDefaults() {
		try{
			String extension = null;
			
			String os = System.getenv("OS");
			
			if(os.toLowerCase().contains("win")) {
				os = "win32";
				extension = ".exe";
				
			} else if(os.toLowerCase().contains("osx")) {
				os = "osx";
				extension = ".app";
				
			} else if( os.toLowerCase().contains("lin") || os.toLowerCase().contains("uni") ) {
				os = "linux";
				extension = ".sh";
				
			} else {
				MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
						                "Default Preferences Error",
						                "Unable to determine the operating system.\n" 
						                      + "Please set the executable locations manually in the CIBA Preferences page.");
				
			}
			
//			MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
//					                      "Confirmation",
//					                      "Setting Preferences for " + os + " to run \"" + extension + "\" applications.");
			
			// Set the default value for the operating system's executable file extension
			//setInternalExecLocation(EXECUTABLE_FILE_EXTENSION, extension);
			
			// Set the default value for the Clustalw executable
			setInternalExecLocation(ClustalWizard.CLUSTALW_PREFERENCES_KEY, "applications/" + os + "/clustalw_2.0.12/clustalw2" + extension);

			// Set the default value for the DNADist executable
			setInternalExecLocation(DNADistWizard.DNADIST_PREFERENCES_KEY, "applications/" + os + "/phylip3.69/exe/dnadist" + extension);

			// Set the default value for the DNADist executable
			setInternalExecLocation(NeighborWizard.NEIGHBOR_PREFERENCES_KEY, "/applications/" + os + "/phylip3.69/exe/neighbor" + extension);

            // Set the default value for the DNADist executable
			setInternalExecLocation(DrawtreeWizard.DRAWTREE_PREFERENCES_KEY, "/applications/" + os + "/phylip3.69/exe/drawtree" + extension);
			

		} catch (Exception e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
	                                "Default Preferences Error",
	                                "Unable to set the preferences.\n"
	                                      + "Please set the executable locations manually in the CIBA Preferences page.");
		}
	}
	
	private static void setInternalExecLocation(String key, String internalLoc) throws URISyntaxException, IOException {
		
		URL url = Platform.asLocalURL(Platform.getBundle(Activator.PLUGIN_ID).getEntry(internalLoc));
		File exec = new File(url.toURI().getPath().toString());
		
		if(!exec.exists()) {
			throw new FileNotFoundException("The executable file could not be found.  Please see the CIBA Preferences page.");
		}
		
		CibaPreferences.setValue(key, exec.getAbsolutePath());
	}
	
	
	

	private CibaPreferences() {
		super();
	}

	@Override
	public void initializeDefaultPreferences() {
		
		storeDefaults();
		
	}
}
