package net.bioclipse.bioinformatics.ciba.wizards.phylip.DNADist;

import java.util.HashMap;

import net.bioclipse.bioinformatics.ciba.wizards.AbsCibaWizardPage;
import net.bioclipse.bioinformatics.ciba.wizards.phylip.DNADist.DNADistWizard.DistanceModel;

import org.eclipse.core.resources.IFolder;
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

public class DNADistWizardPage extends AbsCibaWizardPage {
	
	private Text inputFile;
	private Text txtTiTvRatio;
	private Text frequency_A;
	private Text frequency_C;
	private Text frequency_G;
	private Text frequency_TU;
	private Text categoryRate1;
	private Text categoryRate2;
	private Text categoryRate3;
	private Text categoryRate4;
	private Text categoryRate5;
	private Text categoryRate6;
	private Text categoryRate7;
	private Text categoryRate8;
	private Text categoryRate9;
	private HashMap<Integer, Text> categoryRateMap;
	private Spinner spinSubsRateCategories;
	private Label lblA;
	private Label lblC;
	private Label lblG;
	private Label lblTu;
	private Label lblDistance;
	private Label lblGammaDistributedRates;
	private Label lblTiTvRatio;
	private Label lblNumCategoriesOfSubs;
	private Label lblSubstitutionRates;
	private Label lblUseWeights;
	private Label lblUseEmpiricalBaseFreqs;
	private Label lblDistanceMatrix;
	private Label lblInterleavedSeqs;
	private Button radioInputInterleaved_No;
	private Button radioInputInterleaved_Yes;
	private Combo comboDistanceMatrix;
	private Button radioUseEmpFreqs_Yes;
	private Button radioUseEmpFreqs_No;
	private Button radioUseWeights_No;
	private Button radioUseWeights_Yes;
	private Combo gammaAcrossSites;
	private Combo comboDistMethod;
	private Button btnFileBrowse;
	private Composite empiricalComposite;

	private String inputFileName = null;
	private String outputFileName = null;
	private DistanceModel model;
	
	
	/**
	 * Create the wizard.
	 */
	public DNADistWizardPage(IFolder root) {
		super("Phylip DNADist Wizard");
		setTitle("Phylip - DNADist");
		setDescription("Choose the DNADist parameters below...");
		if(root != null) {
			setInputFileName(root.getRawLocation().toOSString());
		}
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
		
		inputFile = new Text(container, SWT.BORDER);
		inputFile.setBounds(10, 12, 599, 20);
		
		if(inputFileName != null) {
			inputFile.setText(inputFileName);
		}
		
		btnFileBrowse = new Button(container, SWT.NONE);
		btnFileBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(new String[] {"*.phy"});
				fd.setFilterNames(new String[] {"Phylip Alignment (*.phy)"});
				fd.setFileName(getInputFileName());
				
				String fdResult = fd.open();
				
				if( fdResult != null ) {
					inputFile.setText(fdResult);
					inputFileName = fdResult;
				}
				
				getWizard().getContainer().updateButtons();
			}
		});
		btnFileBrowse.setBounds(615, 10, 75, 23);
		btnFileBrowse.setText("Browse");
		
		lblTiTvRatio = new Label(container, SWT.NONE);
		lblTiTvRatio.setBounds(10, 100, 267, 21);
		lblTiTvRatio.setText("Transition/transversion ratio (Default = 2.0):");
		
		lblDistance = new Label(container, SWT.NONE);
		lblDistance.setBounds(10, 45, 236, 20);
		lblDistance.setText("Distance Model:");
		
		lblGammaDistributedRates = new Label(container, SWT.NONE);
		lblGammaDistributedRates.setBounds(10, 74, 236, 20);
		lblGammaDistributedRates.setText("Gamma distributed rates across sites:");
		
		comboDistMethod = new Combo(container, SWT.READ_ONLY);
		comboDistMethod.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateVisibleOptions();
			}
		});
		comboDistMethod.setItems(new String[] {"F84", "Kimura", "Jukes-Cantor", "Log Det", "Similarity Table"});
		comboDistMethod.setBounds(283, 44, 120, 23);
		comboDistMethod.select(0);
		
		lblUseWeights = new Label(container, SWT.NONE);
		lblUseWeights.setBounds(10, 180, 236, 20);
		lblUseWeights.setText("Use weights for sites:");
		
		lblNumCategoriesOfSubs = new Label(container, SWT.NONE);
		lblNumCategoriesOfSubs.setBounds(10, 128, 267, 21);
		lblNumCategoriesOfSubs.setText("How many categories of substitution rates:");
		
		lblUseEmpiricalBaseFreqs = new Label(container, SWT.NONE);
		lblUseEmpiricalBaseFreqs.setBounds(10, 206, 236, 20);
		lblUseEmpiricalBaseFreqs.setText("Use empirical base frequencies:");
		
		lblDistanceMatrix = new Label(container, SWT.NONE);
		lblDistanceMatrix.setBounds(10, 232, 236, 20);
		lblDistanceMatrix.setText("Distance matrix format:");
		
		lblInterleavedSeqs = new Label(container, SWT.NONE);
		lblInterleavedSeqs.setBounds(10, 258, 236, 20);
		lblInterleavedSeqs.setText("Input sequences interleaved:");
		
		gammaAcrossSites = new Combo(container, SWT.READ_ONLY);
		gammaAcrossSites.setItems(new String[] {"No", "Yes", "Gamma-Invariant"});
		gammaAcrossSites.setBounds(283, 71, 120, 23);
		gammaAcrossSites.select(0);
		
		txtTiTvRatio = new Text(container, SWT.BORDER);
		txtTiTvRatio.setText("2.0");
		txtTiTvRatio.setBounds(283, 100, 120, 21);
		
		Composite weightsComposite = new Composite(container, SWT.NONE);
		weightsComposite.setBounds(283, 179, 120, 20);
		
		radioUseWeights_Yes = new Button(weightsComposite, SWT.RADIO);
		radioUseWeights_Yes.setBounds(60, 0, 60, 20);
		radioUseWeights_Yes.setText("Yes");
		
		radioUseWeights_No = new Button(weightsComposite, SWT.RADIO);
		radioUseWeights_No.setBounds(0, 0, 60, 20);
		radioUseWeights_No.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		radioUseWeights_No.setSelection(true);
		radioUseWeights_No.setText("No");
		
		empiricalComposite = new Composite(container, SWT.NONE);
		empiricalComposite.setBounds(283, 205, 407, 20);
		
		radioUseEmpFreqs_No = new Button(empiricalComposite, SWT.RADIO);
		radioUseEmpFreqs_No.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO: refresh
			}
		});
		radioUseEmpFreqs_No.setBounds(60, 0, 60, 20);
		radioUseEmpFreqs_No.setText("No");
		
		radioUseEmpFreqs_Yes = new Button(empiricalComposite, SWT.RADIO);
		radioUseEmpFreqs_Yes.setBounds(0, 0, 60, 20);
		radioUseEmpFreqs_Yes.setSelection(true);
		radioUseEmpFreqs_Yes.setText("Yes");
		
		lblC = new Label(empiricalComposite, SWT.NONE);
		lblC.setBounds(190, 4, 10, 20);
		lblC.setText("C:");
		
		lblA = new Label(empiricalComposite, SWT.NONE);
		lblA.setBounds(120, 2, 10, 20);
		lblA.setText("A:");
		
		frequency_A = new Text(empiricalComposite, SWT.BORDER);
		frequency_A.setBounds(135, 0, 35, 20);
		
		frequency_C = new Text(empiricalComposite, SWT.BORDER);
		frequency_C.setBounds(205, 0, 35, 20);
		
		lblG = new Label(empiricalComposite, SWT.NONE);
		lblG.setBounds(260, 4, 10, 20);
		lblG.setText("G:");
		
		lblTu = new Label(empiricalComposite, SWT.NONE);
		lblTu.setBounds(330, 4, 25, 20);
		lblTu.setText("T/U:");
		
		frequency_G = new Text(empiricalComposite, SWT.BORDER);
		frequency_G.setBounds(275, 0, 35, 20);
		
		frequency_TU = new Text(empiricalComposite, SWT.BORDER);
		frequency_TU.setBounds(360, 0, 35, 20);
		
		comboDistanceMatrix = new Combo(container, SWT.READ_ONLY);
		comboDistanceMatrix.setItems(new String[] {"Square", "Lower-triangular", "Human-readable"});
		comboDistanceMatrix.setBounds(283, 232, 120, 23);
		comboDistanceMatrix.select(0);
		
		Composite interleavedComposite = new Composite(container, SWT.NONE);
		interleavedComposite.setBounds(283, 258, 120, 20);
		
		radioInputInterleaved_Yes = new Button(interleavedComposite, SWT.RADIO);
		radioInputInterleaved_Yes.setText("Yes");
		radioInputInterleaved_Yes.setBounds(60, 0, 60, 20);
		
		radioInputInterleaved_No = new Button(interleavedComposite, SWT.RADIO);
		radioInputInterleaved_No.setText("No");
		radioInputInterleaved_No.setSelection(true);
		radioInputInterleaved_No.setBounds(0, 0, 60, 20);
		
		spinSubsRateCategories = new Spinner(container, SWT.BORDER | SWT.READ_ONLY);
		spinSubsRateCategories.setToolTipText("Default = 1");
		spinSubsRateCategories.setPageIncrement(5);
		spinSubsRateCategories.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshCategoryRateDisplay();
			}
		});
		spinSubsRateCategories.setTextLimit(1);
		spinSubsRateCategories.setMaximum(9);
		spinSubsRateCategories.setMinimum(1);
		spinSubsRateCategories.setBounds(283, 127, 120, 22);
		
		lblSubstitutionRates = new Label(container, SWT.NONE);
		lblSubstitutionRates.setBounds(109, 154, 168, 20);
		lblSubstitutionRates.setText("Enter categroy rates here:");
		
		categoryRate1 = new Text(container, SWT.BORDER);
		categoryRate1.setBounds(283, 155, 35, 20);
		
		categoryRate2 = new Text(container, SWT.BORDER);
		categoryRate2.setBounds(324, 155, 35, 20);
		
		categoryRate3 = new Text(container, SWT.BORDER);
		categoryRate3.setBounds(365, 155, 35, 20);
		
		categoryRate4 = new Text(container, SWT.BORDER);
		categoryRate4.setBounds(406, 155, 35, 20);
		
		categoryRate5 = new Text(container, SWT.BORDER);
		categoryRate5.setBounds(447, 155, 35, 20);
		
		categoryRate6 = new Text(container, SWT.BORDER);
		categoryRate6.setBounds(488, 155, 35, 20);
		
		categoryRate7 = new Text(container, SWT.BORDER);
		categoryRate7.setBounds(529, 155, 35, 20);
		
		categoryRate8 = new Text(container, SWT.BORDER);
		categoryRate8.setBounds(570, 155, 35, 20);
		
		categoryRate9 = new Text(container, SWT.BORDER);
		categoryRate9.setBounds(611, 155, 35, 20);
		
		refreshCategoryRateDisplay();
	}
	
	private void refreshCategoryRateDisplay() {
		
		if (categoryRateMap == null) {
			categoryRateMap = new HashMap<Integer, Text>();
			categoryRateMap.put(1, categoryRate1);
			categoryRateMap.put(2, categoryRate2);
			categoryRateMap.put(3, categoryRate3);
			categoryRateMap.put(4, categoryRate4);
			categoryRateMap.put(5, categoryRate5);
			categoryRateMap.put(6, categoryRate6);
			categoryRateMap.put(7, categoryRate7);
			categoryRateMap.put(8, categoryRate8);
			categoryRateMap.put(9, categoryRate9);
		}
		
		int numCategories = spinSubsRateCategories.getSelection();
		
		for (int i = 1; i <= 9 ; i++) {
			if (i <= numCategories && numCategories != 1) {
				categoryRateMap.get(i).setVisible(true);
			} else {
				categoryRateMap.get(i).setVisible(false);
			}
		}
	}
	
	private void updateVisibleOptions() {
		int model = comboDistMethod.getSelectionIndex();
		
		lblGammaDistributedRates.setEnabled((model == 1) || (model == 2) || (model == 3));
		gammaAcrossSites.setEnabled((model == 1) || (model == 2) || (model == 3));
		
		lblTiTvRatio.setEnabled((model == 1) || (model == 2));
		txtTiTvRatio.setEnabled((model == 1) || (model == 2));
		
		lblNumCategoriesOfSubs.setEnabled((model == 1) || (model == 2) || (model == 3));
		spinSubsRateCategories.setEnabled((model == 1) || (model == 2) || (model == 3));
		
		lblSubstitutionRates.setEnabled((model == 1) || (model == 2) || (model == 3));
		
		// do for all rate
		for( Text txtBox : categoryRateMap.values()) {
			txtBox.setEnabled((model == 1) || (model == 2) || (model == 3));
		}
		
		lblUseEmpiricalBaseFreqs.setEnabled(model == 1);
		lblA.setEnabled(model == 1);	frequency_A.setEnabled(model == 1);
		lblC.setEnabled(model == 1);	frequency_C.setEnabled(model == 1);
		lblG.setEnabled(model == 1);	frequency_G.setEnabled(model == 1);
		lblTu.setEnabled(model == 1);	frequency_TU.setEnabled(model == 1);
		
	}
	
	public String getOutputFileName() {
		return outputFileName;
	}
	
	public String getInputFileName() {
		return inputFileName;
	}
	
	public void setOutputFileName(String newFileName) {
		this.outputFileName = newFileName;
	}
	
	public void setInputFileName(String newFileName) {
		this.inputFileName = newFileName;
		
		if(inputFile != null) {
			inputFile.setText(inputFileName);
		}
	}
	
	@Override
	public String getArgs() {
		
		StringBuilder args = new StringBuilder(inputFile.getText()).append("\n");
		args.append("F\n");
		
		if( (outputFileName != null) && !(outputFileName.isEmpty()) ){
			args.append(outputFileName).append("\n");
		} else {
			args.append(inputFile.getText()).append(".dist\n");
		}
		
		// Set up the DNADist mode.
		// Note: Changing the order in the combo box will invalidate this method
		for(int i=0; i<comboDistMethod.getSelectionIndex(); i++) {
			args.append("D\n");
		}
		
		// Set up the gamma distribution
		// Note: Changing the order in the combo box will invalidate this method
		for(int j=0; j<gammaAcrossSites.getSelectionIndex(); j++) {
			args.append("G\n");
		}
		
		// Set up the transition/transversion ratio only if its not the default
		if(Double.parseDouble(txtTiTvRatio.getText()) != 2.0) {
			args.append("T\n").append(txtTiTvRatio.getText()).append("\n");
		}
		
		// Set up the substitution rates
		if(spinSubsRateCategories.getSelection() != 1) {
			args.append("C\n").append(spinSubsRateCategories.getSelection()).append("\n");
			
			for(int i=1; i<spinSubsRateCategories.getSelection(); i++) {
				args.append(categoryRateMap.get(i) + " ");
			}
		}
		
		// Set up the weighted sites selection
		if(radioUseWeights_Yes.getSelection()) {
			args.append("W\n");
		}
		
		// Set up the empirical base frequencies
		if(radioUseEmpFreqs_No.getSelection()) {
			args.append("F\n").append(frequency_A.getText()).append(" " + frequency_C.getText());
			args.append(" " + frequency_G.getText()).append(" " + frequency_TU.getText()).append("\n");
		}
		
		// Set up the distance matrix format
		// Note: Changing the order in the combo box will invalidate this method
		for(int j=0; j<comboDistanceMatrix.getSelectionIndex(); j++) {
			args.append("L\n");
		}
		
		// Set up the interleaving of sequences
		if(radioInputInterleaved_Yes.getSelection()) {
			args.append("I\n");
		}
		
		// To accept the defaults, and run the phylip program
		args.append("Y\n");
		
		// Add another return for the last line "Press enter to exit" piece of DNADist
		args.append("\n\n");
		
		return args.toString();
	}
	
	@Override
	public boolean isPageComplete() {
		return ( (this.inputFile.getText() != null) && !(this.inputFile.getText().isEmpty()) );
	}

}
