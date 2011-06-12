/*******************************************************************************
 * Copyright (c) 2010  Daniel F. Surdyk, III <dfsurdyk@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.bioinformatics.ciba;

import net.bioclipse.bioinformatics.ciba.business.ICibaManager;
import net.bioclipse.bioinformatics.ciba.business.IJavaCibaManager;
import net.bioclipse.bioinformatics.ciba.business.IJavaScriptCibaManager;
import net.bioclipse.bioinformatics.ciba.main.CibaPreferences;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The Activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IWorkbenchListener {

	public static final String PLUGIN_ID = "net.bioclipse.bioinformatics.ciba";
	
    private static final Logger logger = Logger.getLogger(Activator.class);
    
    // The shared instance
    private static Activator plugin;

    // Trackers for getting the managers
    private ServiceTracker javaFinderTracker;
    private ServiceTracker jsFinderTracker;

    public Activator() {
    	
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        
        plugin = this;
        javaFinderTracker
            = new ServiceTracker( context,
                                  IJavaCibaManager.class.getName(),
                                  null );

        javaFinderTracker.open();
        jsFinderTracker
            = new ServiceTracker( context,
                                  IJavaScriptCibaManager.class.getName(),
                                  null );

        jsFinderTracker.open();
        
        Workbench.getInstance().addWorkbenchListener(this);
        
    }
    
    public void stop(BundleContext context) throws Exception {
        this.plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    public ICibaManager getJavaCibaManager() {
        ICibaManager manager = null;
        try {
            manager = (ICibaManager)
                      javaFinderTracker.waitForService(1000*10);
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(
                          "Could not get the Java CibaManager",
                          e );
        }
        if (manager == null) {
            throw new IllegalStateException(
                          "Could not get the Java CibaManager");
        }
        
        return manager;
    }

    public IJavaScriptCibaManager getJavaScriptCibaManager() {
        IJavaScriptCibaManager manager = null;
        try {
            manager = (IJavaScriptCibaManager)
                      jsFinderTracker.waitForService(1000*10);
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(
                          "Could not get the JavaScript CibaManager",
                          e );
        }
        if (manager == null) {
            throw new IllegalStateException(
                          "Could not get the JavaScript CibaManager");
        }
        return manager;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	@Override
	public void postShutdown(IWorkbench workbench) {
	}

	
	/**
	 * This is the hook that will veto any shutdown events if the user wants to keep Bioclipse
	 *  open while the processes are running.
	 */
	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {

		// TODO: This is wrong for preference access.  Fix.
		Preferences preferences = CibaPreferences.getStore();
//		Activator act = getDefault();
//		ScopedPreferenceStore preferences = (ScopedPreferenceStore) act.getPreferenceStore();
		
		String existingPreference = preferences.get(CibaPreferences.CONTINUE_PROCS_POST_CLOSE, "default-didn't exist");
    	
		
		System.out.println("Existing: " + existingPreference);
		
		// If the user wishes to always continue the executing process post-close, then just return
		//  true and exit the SW.
		if( existingPreference.equals(MessageDialogWithToggle.ALWAYS)) {
			return true;
		}
		
		// Or else, promt the user
    	MessageDialogWithToggle mdt = 
    		MessageDialogWithToggle.openYesNoQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
        		                                      "Background Process",
        		                                      "There are currently CIBA Processes running in the background.\nExiting now will stop all processes.\n\nDo you wish to exit now?",
        		                                      "Always allow processes to continue after exit (not recommended)", 
        		                                      false,
        		                                      null,
        		                                      CibaPreferences.CONTINUE_PROCS_POST_CLOSE);
        
    	
    	
        int retCode = mdt.getReturnCode();
    	boolean toggleState = mdt.getToggleState();
    	
    	/*
    	 * 
    	 * STORE A NEW BOOLEAN USING preferences.setValue("different.string.for.key.", true);
    	 * AS THE CHECKED/UNCHECKED STATUS OF THE BOXba
    	 * 
    	 */
    	
    	System.out.println("Toggle State: " + toggleState);
    	
    	// The user wishes to continue letting Bioclipse close, AND let the processes run in
    	//  the background.
    	if(toggleState && (retCode == IDialogConstants.YES_ID) ) {
    		
    		CibaPreferences.save();
    		
    	}
    	
    	return retCode == IDialogConstants.YES_ID;
	}
}
