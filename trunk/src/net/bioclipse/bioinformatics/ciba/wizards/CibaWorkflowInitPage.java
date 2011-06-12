package net.bioclipse.bioinformatics.ciba.wizards;

import java.util.ArrayList;
import java.util.HashMap;

import net.bioclipse.bioinformatics.ciba.wizards.clustalw.ClustalWizard;
import net.bioclipse.bioinformatics.ciba.wizards.phylip.DNADist.DNADistWizard;
import net.bioclipse.bioinformatics.ciba.wizards.phylip.Drawtree.DrawtreeWizard;
import net.bioclipse.bioinformatics.ciba.wizards.phylip.Neighbor.NeighborWizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class CibaWorkflowInitPage extends AbsCibaWizardPage {
	private List availableWizardList;
	private List selectedWizardList;
	private Button addAllButton;
	private Button addSelectedButton;
	private Button removeSelectedButton;
	private Button removeAllButton;
	private Button selectedUpButton;
	private Button selectedDownButton;
	
	HashMap<String, AbsCibaWizard> wizardHash;

	protected CibaWorkflowInitPage(String pageName) {
		super(pageName);
		setMessage("Select and order the desired wizards for the workflow.");
		setTitle("Ciba Workflow Wizard");
		
		wizardHash = new HashMap<String, AbsCibaWizard>();
	}
	
	public java.util.List<AbsCibaWizard> getWorkflowWizards(){
		
		java.util.List<AbsCibaWizard> wizards = new ArrayList<AbsCibaWizard>();
		
		for(String key : this.selectedWizardList.getItems()) {
			wizards.add(wizardHash.get(key));
		}
		return wizards;
	}
	
	@Override
	protected void initPageValues() {
		this.availableWizardList.add("ClustalW 2.0.12");
		this.availableWizardList.add("Phylip - DNADist");
		this.availableWizardList.add("Phylip - Neighbor");
		this.availableWizardList.add("Phylip - Drawtree");
		
		wizardHash.put("ClustalW 2.0.12", new ClustalWizard());
		wizardHash.put("Phylip - DNADist", new DNADistWizard());
		wizardHash.put("Phylip - Neighbor", new NeighborWizard());
		wizardHash.put("Phylip - Drawtree", new DrawtreeWizard());
		
		updateButtons();
	}
	
	@Override
	public boolean canFlipToNextPage(){
		
		// Enable the next button when the user adds 2 or more 
		//  wizards to add to the workflow.
		return this.selectedWizardList.getItemCount() >= 2;
	}
	
	
	protected void updateButtons() {
		
		this.addAllButton.setEnabled(this.availableWizardList.getItemCount() > 0);
		this.addSelectedButton.setEnabled(this.availableWizardList.getSelectionCount() > 0);
		
		this.removeAllButton.setEnabled(this.selectedWizardList.getItemCount() > 0);
		this.removeSelectedButton.setEnabled(this.selectedWizardList.getSelectionCount() > 0);
		
		this.selectedUpButton.setEnabled( (this.selectedWizardList.getSelectionCount() > 0)
				                           && (this.selectedWizardList.getSelectionIndex() > 0) );
		
		this.selectedDownButton.setEnabled( (this.selectedWizardList.getSelectionCount() > 0)
              && (this.selectedWizardList.getSelectionIndex() != (this.selectedWizardList.getItemCount() - 1) ));
		
		getWizard().getContainer().updateButtons();
		
	}

	@Override
	public void initGUI(Composite parent) {
		//public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		
		Label lblAvailableWizards = new Label(container, SWT.NONE);
		lblAvailableWizards.setBounds(10, 10, 95, 15);
		lblAvailableWizards.setText("Available Wizards:");
		
		Label lblSelectedWizards = new Label(container, SWT.NONE);
		lblSelectedWizards.setBounds(293, 10, 95, 15);
		lblSelectedWizards.setText("Selected Wizards:");
		
		availableWizardList = new List(container, SWT.BORDER);
		availableWizardList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtons();
			}
		});
		availableWizardList.setBounds(10, 31, 175, 247);
		
		selectedWizardList = new List(container, SWT.BORDER);
		selectedWizardList.setBounds(293, 31, 175, 247);
		selectedWizardList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtons();
			}
		});
		
		addAllButton = new Button(container, SWT.NONE);
		addAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(String s : availableWizardList.getItems()) {
					selectedWizardList.add(s);
					availableWizardList.remove(s);
				}
				
				updateButtons();
			}
		});
		addAllButton.setBounds(191, 30, 96, 25);
		addAllButton.setText("Add All -->");
		
		addSelectedButton = new Button(container, SWT.NONE);
		addSelectedButton.setBounds(191, 61, 96, 25);
		addSelectedButton.setText("Add -->");
		addSelectedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(String s : availableWizardList.getSelection()) {
					selectedWizardList.add(s);
					availableWizardList.remove(s);
				}
				
				updateButtons();
			}
		});
		
		removeAllButton = new Button(container, SWT.NONE);
		removeAllButton.setBounds(192, 155, 95, 25);
		removeAllButton.setText("<-- Remove All");
		removeAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(String s : selectedWizardList.getItems()) {
					availableWizardList.add(s);
					selectedWizardList.remove(s);
				}
				
				updateButtons();
			}
		});
		
		removeSelectedButton = new Button(container, SWT.NONE);
		removeSelectedButton.setBounds(191, 124, 96, 25);
		removeSelectedButton.setText("<-- Remove");
		removeSelectedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(String s : selectedWizardList.getSelection()) {
					availableWizardList.add(s);
					selectedWizardList.remove(s);
				}
				
				updateButtons();
			}
		});
		
		selectedUpButton = new Button(container, SWT.NONE);
		selectedUpButton.setBounds(474, 31, 96, 25);
		selectedUpButton.setText("Up");
		selectedUpButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(selectedWizardList.getSelectionCount() > 0) {
					int selectedIndex = selectedWizardList.getSelectionIndex();
					String selection = selectedWizardList.getSelection()[0];
					selectedWizardList.remove(selection);
					selectedWizardList.add(selection, selectedIndex - 1 );
					selectedWizardList.setSelection(selectedIndex - 1);
				}
					
				updateButtons();
			}
		});
		
		selectedDownButton = new Button(container, SWT.NONE);
		selectedDownButton.setBounds(474, 61, 96, 25);
		selectedDownButton.setText("Down");
		selectedDownButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(selectedWizardList.getSelectionCount() > 0) {
					int selectedIndex = selectedWizardList.getSelectionIndex();
					String selection = selectedWizardList.getSelection()[0];
					selectedWizardList.remove(selection);
					selectedWizardList.add(selection, selectedIndex + 1 );
					selectedWizardList.setSelection(selectedIndex + 1);
				}
				
				updateButtons();
			}
		});
	}

	@Override
	public String getArgs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPageComplete() {
		return this.selectedWizardList.getItemCount() >= 2;
	}
}
