package net.bioclipse.bioinformatics.ciba.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public abstract class AbsCibaWizardPage extends WizardPage {

	
	protected AbsCibaWizardPage(String pageName) {
		super(pageName);
	}


	/**
	 * This method should be used to communicate the pages values
	 *  to the parents ICibaWizard.  Typically this method will be
	 *  called in the <code>prepareForFinish()</code> method, and 
	 *  its results used to generate the command line arguments.
	 * @return
	 */
	public abstract String getArgs();
	
	
	/**
	 * This method should be used to initialize appropriate page defaults
	 *  after the <code>createControl(Composite)</code> method is called.
	 *  <br/><br/>
	 *  <b>NOTE:</b> You must call this method at the end of the 
	 *   createControl() code.
	 */
	protected void initPageValues() {
		// Does nothing - override to populate as needed.
	}
	
	
	/**
	 * This method is what sets up the graphics objects and components
	 * for this wizard page.  This method can usually be auto-generated 
	 * using a WYSIWYG editor.
	 * <br/><br/> See: http://code.google.com/javadevtools/download-wbpro.html
	 */
	@Override
	public void createControl(Composite parent) {
		initGUI(parent);
		initPageValues();
	}
//	public abstract void createControl(Composite parent);
	
	public abstract void initGUI(Composite parent);
	
	
	/**
	 * Indicate whether this page is complete and the wizard should
	 *  be allowed to continue.  This method is also used for enabling the
	 *  wizard's <code>finish</code> button before actually seeing all the 
	 *  pages, so its important that this method returns the correct value
	 *  whether the wizard page has been viewed or not.
	 */
	@Override
	public abstract boolean isPageComplete();
	
	
	
	
}
