package net.bioclipse.bioinformatics.ciba.wizards.clustalw;

import net.bioclipse.bioinformatics.ciba.wizards.AbsCibaWizardPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class PairwisePage extends AbsCibaWizardPage {
	
	private Composite msaSlowParams;
	private Button btnSlowAlignment;
	private Combo jcomboProteinMatrix;
	private Button dnaMatrixBrowse;
	private Text customDNAMatrixFile;
	private Combo jcomboDNAMatrix;
	private Button proteinMatrixBrowse;
	private Text customProteinMatrixFile;
	private Spinner slowExtensionGapPenalty;
	private Spinner slowOpenGapPenalty;
	
	private Composite msaFastParams;
	private Button btnFastAlignment;
	private Spinner fastTopDiags;
	private Spinner fastWindowSize;
	private Spinner fastKTuple;
	private Spinner fastGapPenalty;
	
	private Label lblGapPenalty;
	private Label lblKtuplewordSize;
	private Label lblWindowSize;
	private Label lblNoOfTop;
	private Label lblGapOpenPenalty;
	private Label lblGapExtensionPenalty;
	private Label lblProteinWeightMatrix;
	private Label lblDnaWeightMetrix;
	private Button fastPercentScore;
	private Button fastAbsoluteScore;
	private Group outputOptions;
	private Button btnFastaOutput;
	private Button btnClustalOutput;
	private Button btnPirOutput;
	private Button btnGcgOutput;
	private Button btnPhylipOutput;
	private Button btnGdeOutput;
	private Button btnEnableClustalSeqNums;
	private Button btnEnableSeqRangeNums;
	private Button btnOutputOrder;

	/**
	 * Create the wizard.
	 */
	public PairwisePage() {
		super("ClustalW 2.0");
		setTitle("ClustalW 2.0 - Parameter Selection");
		setDescription("Choose an Alignment Algorithm (Fast/Approximate or Slow/Accurate), and select the appropriate parameters.  You can also change the default output format below...");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	@Override
	public void initGUI(Composite parent) {
		//public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Composite profileComposite = new Composite(container, SWT.NONE);
		profileComposite.setLocation(0, 0);
		profileComposite.setSize(704, 368);
		
		Composite fastSlowComposite = new Composite(profileComposite, SWT.NONE);
		fastSlowComposite.setLocation(0, 0);
		fastSlowComposite.setSize(696, 263);
		
		msaFastParams = new Group(fastSlowComposite, SWT.NONE);
		msaFastParams.setLocation(10, 36);
		msaFastParams.setSize(259, 227);
		
		lblGapPenalty = new Label(msaFastParams, SWT.NONE);
		lblGapPenalty.setBounds(10, 10, 125, 20);
		lblGapPenalty.setText("Gap Penalty (0 - 500):");
		
		lblKtuplewordSize = new Label(msaFastParams, SWT.NONE);
		lblKtuplewordSize.setBounds(10, 50, 113, 20);
		lblKtuplewordSize.setText("K-tuple (word) size:");
		
		lblNoOfTop = new Label(msaFastParams, SWT.NONE);
		lblNoOfTop.setBounds(10, 130, 146, 20);
		lblNoOfTop.setText("# Top Diagonals (0 - 50):");
		
		lblWindowSize = new Label(msaFastParams, SWT.NONE);
		lblWindowSize.setBounds(10, 90, 123, 20);
		lblWindowSize.setText("Window Size (0 - 50):");
		
		fastGapPenalty = new Spinner(msaFastParams, SWT.BORDER);
		fastGapPenalty.setPageIncrement(25);
		fastGapPenalty.setTextLimit(3);
		fastGapPenalty.setSelection(250);
		fastGapPenalty.setMaximum(500);
		fastGapPenalty.setBounds(177, 10, 68, 22);
		
		fastKTuple = new Spinner(msaFastParams, SWT.BORDER);
		fastKTuple.setTextLimit(1);
		fastKTuple.setSelection(2);
		fastKTuple.setPageIncrement(1);
		fastKTuple.setMaximum(4);
		fastKTuple.setBounds(177, 49, 68, 22);
		
		fastWindowSize = new Spinner(msaFastParams, SWT.BORDER);
		fastWindowSize.setTextLimit(2);
		fastWindowSize.setMaximum(50);
		fastWindowSize.setBounds(177, 89, 68, 22);
		
		fastTopDiags = new Spinner(msaFastParams, SWT.BORDER);
		fastTopDiags.setTextLimit(2);
		fastTopDiags.setMaximum(50);
		fastTopDiags.setBounds(177, 129, 68, 22);
		
		Composite composite_1 = new Composite(msaFastParams, SWT.NONE);
		composite_1.setBounds(177, 170, 68, 40);
		
		fastPercentScore = new Button(composite_1, SWT.RADIO);
		fastPercentScore.setSelection(true);
		fastPercentScore.setBounds(0, 0, 72, 20);
		fastPercentScore.setText("Percent");
		
		fastAbsoluteScore = new Button(composite_1, SWT.RADIO);
		fastAbsoluteScore.setBounds(0, 20, 72, 20);
		fastAbsoluteScore.setText("Absolute");
		
		Label lblScoreAs = new Label(msaFastParams, SWT.NONE);
		lblScoreAs.setBounds(10, 170, 54, 20);
		lblScoreAs.setText("Score As:");
		
		msaSlowParams = new Group(fastSlowComposite, SWT.NONE);
		msaSlowParams.setBounds(275, 36, 411, 227);
		
		lblGapOpenPenalty = new Label(msaSlowParams, SWT.NONE);
		lblGapOpenPenalty.setBounds(10, 10, 211, 20);
		lblGapOpenPenalty.setText("Open Gap Penalty (0.00 - 100.00):");
		
		lblGapExtensionPenalty = new Label(msaSlowParams, SWT.NONE);
		lblGapExtensionPenalty.setBounds(10, 50, 211, 20);
		lblGapExtensionPenalty.setText("Extension Gap Penalty (0.00 - 10.00):");
		
		lblProteinWeightMatrix = new Label(msaSlowParams, SWT.NONE);
		lblProteinWeightMatrix.setBounds(10, 90, 138, 20);
		lblProteinWeightMatrix.setText("Protein Weight Matrix:");
		
		lblDnaWeightMetrix = new Label(msaSlowParams, SWT.NONE);
		lblDnaWeightMetrix.setBounds(10, 160, 138, 20);
		lblDnaWeightMetrix.setText("DNA Weight Metrix:");
		
		slowOpenGapPenalty = new Spinner(msaSlowParams, SWT.BORDER);
		slowOpenGapPenalty.setMaximum(10000);
		slowOpenGapPenalty.setDigits(2);
		slowOpenGapPenalty.setTextLimit(5);
		slowOpenGapPenalty.setBounds(287, 9, 110, 22);
		
		slowExtensionGapPenalty = new Spinner(msaSlowParams, SWT.BORDER);
		slowExtensionGapPenalty.setPageIncrement(1);
		slowExtensionGapPenalty.setMaximum(1000);
		slowExtensionGapPenalty.setDigits(2);
		slowExtensionGapPenalty.setTextLimit(4);
		slowExtensionGapPenalty.setBounds(287, 49, 110, 22);
		
		customProteinMatrixFile = new Text(msaSlowParams, SWT.BORDER);
		customProteinMatrixFile.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});
		customProteinMatrixFile.setBounds(20, 120, 306, 21);
		
		proteinMatrixBrowse = new Button(msaSlowParams, SWT.NONE);
		proteinMatrixBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileChooser = new FileDialog(getShell(), SWT.OPEN);
				customProteinMatrixFile.setText(fileChooser.open() + "");
				getWizard().getContainer().updateButtons();
			}
		});
		proteinMatrixBrowse.setBounds(332, 118, 65, 25);
		proteinMatrixBrowse.setText("Browse");
		
		customDNAMatrixFile = new Text(msaSlowParams, SWT.BORDER);
		customDNAMatrixFile.setBounds(20, 190, 306, 21);
		customDNAMatrixFile.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});
		
		dnaMatrixBrowse = new Button(msaSlowParams, SWT.NONE);
		dnaMatrixBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileChooser = new FileDialog(getShell(), SWT.OPEN);
				customDNAMatrixFile.setText(fileChooser.open() + "");
				getWizard().getContainer().updateButtons();
			}
		});
		dnaMatrixBrowse.setBounds(332, 188, 65, 25);
		dnaMatrixBrowse.setText("Browse");
		
		jcomboProteinMatrix = new Combo(msaSlowParams, SWT.READ_ONLY);
		jcomboProteinMatrix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				allowCustomProteinMatrix(jcomboProteinMatrix.getItem(jcomboProteinMatrix.getSelectionIndex()).equals("Custom"));
			}
		});
		jcomboProteinMatrix.setItems(new String[] {"BLOSUM 30", "PAM 350", "Gonnet 250", "Identity Matrix", "Custom"});
		jcomboProteinMatrix.setBounds(287, 89, 110, 23);
		jcomboProteinMatrix.select(3);
		
		jcomboDNAMatrix = new Combo(msaSlowParams, SWT.READ_ONLY);
		jcomboDNAMatrix.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				allowCustomDNAMatrix(jcomboDNAMatrix.getItem(jcomboDNAMatrix.getSelectionIndex()).equals("Custom"));
			}
		});
		jcomboDNAMatrix.setItems(new String[] {"IUB (Default)", "ClustalW (1.6)", "Custom"});
		jcomboDNAMatrix.setBounds(287, 159, 110, 23);
		jcomboDNAMatrix.select(0);
		
		btnSlowAlignment = new Button(fastSlowComposite, SWT.RADIO);
		btnSlowAlignment.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setFastSlowEnabling();
			}
		});
		btnSlowAlignment.setBounds(275, 8, 195, 20);
		btnSlowAlignment.setText("Slow Alignment");
		
		btnFastAlignment = new Button(fastSlowComposite, SWT.RADIO);
		btnFastAlignment.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setFastSlowEnabling();
			}
		});
		btnFastAlignment.setSelection(true);
		btnFastAlignment.setBounds(10, 8, 259, 20);
		btnFastAlignment.setText("Fast Alignment");
		
		outputOptions = new Group(profileComposite, SWT.NONE);
		outputOptions.setText("Output Options");
		outputOptions.setBounds(10, 269, 676, 94);
		
		btnFastaOutput = new Button(outputOptions, SWT.RADIO);
		btnFastaOutput.setBounds(10, 43, 151, 17);
		btnFastaOutput.setText("FASTA Output");
		
		btnClustalOutput = new Button(outputOptions, SWT.RADIO);
		btnClustalOutput.setBounds(10, 20, 151, 17);
		btnClustalOutput.setText("CLUSTAL Output");
		btnClustalOutput.setSelection(true);
		
		btnPirOutput = new Button(outputOptions, SWT.RADIO);
		btnPirOutput.setBounds(199, 20, 151, 17);
		btnPirOutput.setText("NBRF/PIR Output");
		
		btnGcgOutput = new Button(outputOptions, SWT.RADIO);
		btnGcgOutput.setBounds(199, 43, 151, 17);
		btnGcgOutput.setText("GCG/MSF Output");
		
		btnPhylipOutput = new Button(outputOptions, SWT.RADIO);
		btnPhylipOutput.setBounds(10, 66, 151, 17);
		btnPhylipOutput.setText("PHYLIP Output");
		
		btnGdeOutput = new Button(outputOptions, SWT.RADIO);
		btnGdeOutput.setBounds(199, 66, 151, 17);
		btnGdeOutput.setText("GDE Output");
		
		btnEnableClustalSeqNums = new Button(outputOptions, SWT.CHECK);
		btnEnableClustalSeqNums.setBounds(438, 43, 225, 17);
		btnEnableClustalSeqNums.setText("Enable CLUSTALW Sequence Numbers");
		
		btnEnableSeqRangeNums = new Button(outputOptions, SWT.CHECK);
		btnEnableSeqRangeNums.setBounds(438, 20, 225, 17);
		btnEnableSeqRangeNums.setText("Enable Sequence Range Numbers");
		
		btnOutputOrder = new Button(outputOptions, SWT.CHECK);
		btnOutputOrder.setBounds(438, 66, 225, 17);
		btnOutputOrder.setText("ALIGNED Output Order");
		btnOutputOrder.setSelection(true);
		
		// Must call init() here because the createControl() method is called after the constructor.
		initView();
		setFastSlowEnabling();
	}
	
	private void initView() {
		dnaMatrixBrowse.setEnabled(false);
		customDNAMatrixFile.setEnabled(false);
		proteinMatrixBrowse.setEnabled(false);
		customProteinMatrixFile.setEnabled(false);
	}
	
	private void allowCustomDNAMatrix(boolean val) {
		dnaMatrixBrowse.setEnabled(val);
		customDNAMatrixFile.setEnabled(val);
		getWizard().getContainer().updateButtons();
	}
	
	private void allowCustomProteinMatrix(boolean val) {
		proteinMatrixBrowse.setEnabled(val);
		customProteinMatrixFile.setEnabled(val);
		getWizard().getContainer().updateButtons();
	}
	
	private void setFastSlowEnabling() {
		
		boolean doFastAlignment = btnFastAlignment.getSelection();
		boolean doSlowAlignment = btnSlowAlignment.getSelection();
		
		// Enable/Disable slow parameter selection
		jcomboProteinMatrix.setEnabled(doSlowAlignment);
		  dnaMatrixBrowse.setEnabled(doSlowAlignment && jcomboDNAMatrix.getItem(jcomboDNAMatrix.getSelectionIndex()).equals("Custom"));
		  customDNAMatrixFile.setEnabled(doSlowAlignment && jcomboDNAMatrix.getItem(jcomboDNAMatrix.getSelectionIndex()).equals("Custom"));
		jcomboDNAMatrix.setEnabled(doSlowAlignment);
		  proteinMatrixBrowse.setEnabled(doSlowAlignment && jcomboProteinMatrix.getItem(jcomboProteinMatrix.getSelectionIndex()).equals("Custom"));
		  customProteinMatrixFile.setEnabled(doSlowAlignment && jcomboProteinMatrix.getItem(jcomboProteinMatrix.getSelectionIndex()).equals("Custom"));
		slowExtensionGapPenalty.setEnabled(doSlowAlignment);
		slowOpenGapPenalty.setEnabled(doSlowAlignment);
		
		// Enable/Disable fast parameter selection
		fastTopDiags.setEnabled(doFastAlignment);
		fastWindowSize.setEnabled(doFastAlignment);
		fastKTuple.setEnabled(doFastAlignment);
		fastGapPenalty.setEnabled(doFastAlignment);
		
		// Enable/Disable the labels
		lblGapPenalty.setEnabled(doFastAlignment);
		lblKtuplewordSize.setEnabled(doFastAlignment);
		lblWindowSize.setEnabled(doFastAlignment);
		lblNoOfTop.setEnabled(doFastAlignment);
		lblGapOpenPenalty.setEnabled(doSlowAlignment);
		lblGapExtensionPenalty.setEnabled(doSlowAlignment);
		lblProteinWeightMatrix.setEnabled(doSlowAlignment);
		lblDnaWeightMetrix.setEnabled(doSlowAlignment);
	}
	
	@Override
	public String getArgs() {
		StringBuilder retVal = new StringBuilder();
		
		if(btnFastAlignment.getSelection()) {
			retVal.append(" -PAIRGAP=" + fastGapPenalty.getText());
			retVal.append(" -KTUPLE=" + fastKTuple.getText());
			retVal.append(" -WINDOW=" + fastWindowSize.getText());
			retVal.append(" -TOPDIAGS=" + fastTopDiags.getText());
			
			String scoreType = "";
			if(fastAbsoluteScore.getSelection()) {
				scoreType = "ABSOLUTE";
			} else if (fastPercentScore.getSelection()) {
				scoreType = "PERCENT";
			}
			
			retVal.append(" -SCORE=" + scoreType);
			
		} else if (btnSlowAlignment.getSelection()) {
			
			retVal.append(" -PWGAPOPEN=" + slowOpenGapPenalty.getText());
			retVal.append(" -PWGAPEXT=" + slowExtensionGapPenalty.getText());
			retVal.append(" -PWMATRIX=" + getProteinWeightMatrix());
			retVal.append(" -PWDNAMATRIX=" + getDNAWeightMatrix());
			
		}
		
		if(btnClustalOutput.getSelection()) {
			// NOTE: Do not combine with above if - it defeates the purpose
			//       of this shortcut.
			if( btnEnableClustalSeqNums.getSelection() )
				retVal.append(" -SEQNOS=ON");

		} else if( btnFastaOutput.getSelection() ) {
			retVal.append(" -OUTPUT=FASTA");
			
		} else if( btnPhylipOutput.getSelection() ) {
			retVal.append(" -OUTPUT=PHYLIP");
			
		} else if( btnPirOutput.getSelection() ) {
			retVal.append(" -OUTPUT=PIR");
						
		} else if( btnGcgOutput.getSelection() ) {
			retVal.append(" -OUTPUT=GCG");
			
		} else if( btnGdeOutput.getSelection() ) {
			retVal.append(" -OUTPUT=GDE -CASE=UPPER");
			
		}
		
		if( btnOutputOrder.getSelection() ) {
			retVal.append("-OUTORDER=ALIGNED");
		} else {
			retVal.append("-OUTORDER=INPUT");
		}

		return retVal.toString();
	}
	
	private String getProteinWeightMatrix() {
		if(jcomboProteinMatrix.getSelectionIndex() == 4) {
			return this.customProteinMatrixFile.getText();
			
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
			return this.customDNAMatrixFile.getText();
			
		} else if(jcomboDNAMatrix.getSelectionIndex() == 1) {
			return "CLUSTALW";
			
		} else if(jcomboDNAMatrix.getSelectionIndex() == 0) {
			return "IUB";
			
		}
		
		return null;
		
	}
	
	@Override
	public boolean isPageComplete() {
		
		boolean result = jcomboProteinMatrix != null;
		
		if( result && jcomboProteinMatrix.getSelectionIndex() == 4) {
			result &= (!customProteinMatrixFile.getText().isEmpty());
		}
		
		if(jcomboDNAMatrix.getSelectionIndex() == 2) {
			result &= (!customDNAMatrixFile.getText().isEmpty());
		}
		
		return result;
	}
}
