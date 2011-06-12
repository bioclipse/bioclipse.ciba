package net.bioclipse.bioinformatics.ciba.wizards.clustalw;

import net.bioclipse.bioinformatics.ciba.wizards.AbsCibaWizardPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class MultAlignPage extends AbsCibaWizardPage {
	
	private Text proteinWeightMatrixFile;
	private Text dnaWeightMatrixFile;
	private Spinner gapOpenPenalty;
	private Spinner gapExtensionPenalty;
	private Spinner divergentDelay;
	private Spinner dnaTransitionWeight;
	private Combo jcomboProteinMatrix;
	private Button proteinWeightBrowse;
	private Combo jcomboDNAMatrix;
	private Button dnaWeightBrowse;

	/**
	 * Create the wizard.
	 */
	public MultAlignPage() {
		super("wizardPage");
		setTitle("ClustalW 2.0 - Multiple Alignment Parameter Selection");
		setDescription("Select the multiple alignment parameters for the ClusatalW run");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void initGUI(Composite parent) {
//	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Composite composite = new Composite(container, SWT.BORDER | SWT.NO_FOCUS | SWT.EMBEDDED);
		composite.setBounds(0, 0, 704, 288);
		
		Label lblGapExtensionPenalty = new Label(composite, SWT.NONE);
		lblGapExtensionPenalty.setText("Gap Extension Penalty (0.00 - 10.00):");
		lblGapExtensionPenalty.setBounds(10, 40, 211, 20);
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setText("Protein Weight Matrix:");
		label_2.setBounds(10, 151, 138, 20);
		
		Label lblDnaWeightMatrix = new Label(composite, SWT.NONE);
		lblDnaWeightMatrix.setText("DNA Weight Matrix:");
		lblDnaWeightMatrix.setBounds(10, 221, 138, 20);
		
		gapOpenPenalty = new Spinner(composite, SWT.BORDER);
		gapOpenPenalty.setMaximum(10000);
		gapOpenPenalty.setSelection(1000);
		gapOpenPenalty.setPageIncrement(1000);
		gapOpenPenalty.setTextLimit(5);
		gapOpenPenalty.setDigits(2);
		gapOpenPenalty.setBounds(287, 9, 110, 22);
		
		gapExtensionPenalty = new Spinner(composite, SWT.BORDER);
		gapExtensionPenalty.setTextLimit(4);
		gapExtensionPenalty.setPageIncrement(1);
		gapExtensionPenalty.setMaximum(1000);
		gapExtensionPenalty.setDigits(2);
		gapExtensionPenalty.setBounds(287, 39, 110, 22);
		
		proteinWeightMatrixFile = new Text(composite, SWT.BORDER);
		proteinWeightMatrixFile.setEnabled(false);
		proteinWeightMatrixFile.setBounds(20, 181, 599, 21);
		
		proteinWeightBrowse = new Button(composite, SWT.NONE);
		proteinWeightBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileChooser = new FileDialog(getShell(), SWT.OPEN);
				proteinWeightMatrixFile.setText(fileChooser.open() + "");
				getWizard().getContainer().updateButtons();
			}
		});
		proteinWeightBrowse.setEnabled(false);
		proteinWeightBrowse.setText("Browse");
		proteinWeightBrowse.setBounds(625, 180, 65, 25);
		
		dnaWeightMatrixFile = new Text(composite, SWT.BORDER);
		dnaWeightMatrixFile.setEnabled(false);
		dnaWeightMatrixFile.setBounds(20, 251, 599, 21);
		
		dnaWeightBrowse = new Button(composite, SWT.NONE);
		dnaWeightBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileChooser = new FileDialog(getShell(), SWT.OPEN);
				dnaWeightMatrixFile.setText(fileChooser.open() + "");
				getWizard().getContainer().updateButtons();
			}
		});
		dnaWeightBrowse.setEnabled(false);
		dnaWeightBrowse.setText("Browse");
		dnaWeightBrowse.setBounds(625, 250, 65, 25);
		
		jcomboProteinMatrix = new Combo(composite, SWT.READ_ONLY);
		jcomboProteinMatrix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				allowCustomProteinMatrix(jcomboProteinMatrix.getText().equals("Custom"));
			}
		});
		jcomboProteinMatrix.setItems(new String[] {"BLOSUM Series", "PAM Series", "Gonnet Series", "Identity Matrix", "Custom"});
		jcomboProteinMatrix.setBounds(580, 151, 110, 23);
		jcomboProteinMatrix.select(3);
		
		jcomboDNAMatrix = new Combo(composite, SWT.READ_ONLY);
		jcomboDNAMatrix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				allowCustomDNAMatrix(jcomboDNAMatrix.getText().equals("Custom"));
			}
		});
		jcomboDNAMatrix.setItems(new String[] {"IUB (Default)", "ClustalW (1.6)", "Custom"});
		jcomboDNAMatrix.setBounds(580, 221, 110, 23);
		jcomboDNAMatrix.select(0);
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("Open Gap Penalty (0.00 - 100.00):");
		label.setBounds(10, 10, 211, 20);
		
		Label lblDelayDivergentSequences = new Label(composite, SWT.NONE);
		lblDelayDivergentSequences.setBounds(10, 70, 225, 20);
		lblDelayDivergentSequences.setText("Delay Divergent Sequences (0 - 100%):");
		
		divergentDelay = new Spinner(composite, SWT.BORDER);
		divergentDelay.setSelection(30);
		divergentDelay.setBounds(287, 69, 110, 22);
		divergentDelay.setTextLimit(3);
		divergentDelay.setPageIncrement(5);
		
		Label lblDnaTransitionsWeight = new Label(composite, SWT.NONE);
		lblDnaTransitionsWeight.setBounds(10, 100, 211, 20);
		lblDnaTransitionsWeight.setText("DNA Transitions Weight (0.0 - 1.0):");
		
		dnaTransitionWeight = new Spinner(composite, SWT.BORDER);
		dnaTransitionWeight.setSelection(5);
		dnaTransitionWeight.setBounds(287, 99, 110, 22);
		dnaTransitionWeight.setTextLimit(2);
		dnaTransitionWeight.setPageIncrement(1);
		dnaTransitionWeight.setMaximum(10);
		dnaTransitionWeight.setDigits(1);
	}
	
	public String getArgs() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(" -MATRIX=" + getProteinWeightMatrix());
		builder.append(" -DNAMATRIX=" + getDNAWeightMatrix());
		builder.append(" -GAPOPEN=" + gapOpenPenalty.getText());
		builder.append(" -GAPEXT=" + gapExtensionPenalty.getText());
		builder.append(" -TRANSWEIGHT=" + dnaTransitionWeight.getText());
		builder.append(" -MAXDIV=" + divergentDelay.getText());
		
		return builder.toString();
	}
	
	private String getProteinWeightMatrix() {
		if(jcomboProteinMatrix.getSelectionIndex() == 4) {
			return this.proteinWeightMatrixFile.getText();
			
		} else if(jcomboProteinMatrix.getSelectionIndex() == 3) {
			return "ID";
			
		} else if(jcomboProteinMatrix.getSelectionIndex() == 2) {
			return "GONNET";
			
		} else if(jcomboProteinMatrix.getSelectionIndex() == 1) {
			return "PAM";
			
		} else if(jcomboProteinMatrix.getSelectionIndex() == 0) {
			return "BLOSUM";
			
		}
		
		return null;
	}
	
	private String getDNAWeightMatrix() {
		if(jcomboDNAMatrix.getSelectionIndex() == 2) {
			return this.dnaWeightMatrixFile.getText();
			
		} else if(jcomboDNAMatrix.getSelectionIndex() == 1) {
			return "CLUSTALW";
			
		} else if(jcomboDNAMatrix.getSelectionIndex() == 0) {
			return "IUB";
			
		}
		
		return null;
		
	}
	
	private void allowCustomDNAMatrix(boolean val) {
		dnaWeightBrowse.setEnabled(val);
		dnaWeightMatrixFile.setEnabled(val);
		getWizard().getContainer().updateButtons();
	}
	
	private void allowCustomProteinMatrix(boolean val) {
		proteinWeightBrowse.setEnabled(val);
		proteinWeightMatrixFile.setEnabled(val);
		getWizard().getContainer().updateButtons();
	}

	@Override
	public boolean isPageComplete() {
		// TODO: Check if the user uploaded custom weight matrices
		return true;
	}	
}
