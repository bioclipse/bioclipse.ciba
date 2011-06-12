package net.bioclipse.bioinformatics.ciba.wizards;

import java.util.List;

import net.bioclipse.bioinformatics.ciba.main.CibaJob;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;

public class CibaWorkflowWizard extends AbsCibaWizard {

	CibaWorkflowInitPage initPage;
	
	public CibaWorkflowWizard() {
		
		// Always show the previous & next buttons.
		this.setForcePreviousAndNextButtons(true);
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
        
		System.out.println("NextPage Called.");
		
		if(page.equals(this.initPage)) {
			for(AbsCibaWizard wiz : initPage.getWorkflowWizards()) {
				for(IWizardPage wizPage : wiz.getWorkflowWizardPages()) {
					this.addPage(wizPage);
				}
			}
		}
		
		return super.getNextPage(page);
    }
	
	
	@Override
	public void addPages() {
		addPage(this.initPage);
	}

	@Override
	public String buildCommand(Object... objects) {
		return null;
	}

	@Override
	public CibaJob getCLIJob() {
		return null;
	}

	@Override
	public void init(IWorkbench wb, IStructuredSelection selection) {
		initPage = new CibaWorkflowInitPage("Initialize Workflow");

	}

	@Override
	public boolean prepareForFinish() {
		System.out.println("Finishing");
		return true;
	}

	public List<AbsCibaWizardPage> getWorkflowWizardPages() {
		return null;
	}

}
