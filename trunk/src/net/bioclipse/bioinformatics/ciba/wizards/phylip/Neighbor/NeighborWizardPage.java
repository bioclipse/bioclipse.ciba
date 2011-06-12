package net.bioclipse.bioinformatics.ciba.wizards.phylip.Neighbor;

import net.bioclipse.bioinformatics.ciba.wizards.AbsCibaWizardPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

public class NeighborWizardPage extends AbsCibaWizardPage {
	private Text txtBoxDistFile;
	private Text txtBoxRawDistance;
	private Button btnDistanceFile;
	private Button btnRawDistance;
	private Button btnBrowse;
	private Button btnUppertriangularDataMatrix;
	private Button btnSubreplictes;
	private Button btnRandomizeInputOrder;
	private Button btnPrintTree;
	private Composite composite_1;
	private Button btnNeighborjoining;
	private Button btnUpgma;
	private Button btnUseOutgroupRoot;
	private Text txtOutgroupRoot;
	private Button btnLowertriangularDataMatrix;

	private String fileName;
	private String outputFilename = null;
	
	
	/**
	 * Create the wizard.
	 */
	public NeighborWizardPage() {
		super("Phylip Neighbor Wizard");
		setTitle("Phylip - Neighbor");
		setDescription("Choose the Neighbor parameters below...");
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
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setBounds(0, 0, 704, 139);
		
		btnDistanceFile = new Button(composite, SWT.RADIO);
		btnDistanceFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				txtBoxDistFile.setEnabled(btnDistanceFile.getSelection());
				btnBrowse.setEnabled(btnDistanceFile.getSelection());
				txtBoxRawDistance.setEnabled(!btnDistanceFile.getSelection());
				getWizard().getContainer().updateButtons();
			}
		});
		btnDistanceFile.setSelection(true);
		btnDistanceFile.setBounds(10, 10, 101, 17);
		btnDistanceFile.setText("Distance File:");
		
		btnRawDistance = new Button(composite, SWT.RADIO);
		btnRawDistance.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				txtBoxDistFile.setEnabled(!btnRawDistance.getSelection());
				btnBrowse.setEnabled(!btnRawDistance.getSelection());
				txtBoxRawDistance.setEnabled(btnRawDistance.getSelection());
				getWizard().getContainer().updateButtons();
			}
		});
		btnRawDistance.setBounds(10, 33, 101, 17);
		btnRawDistance.setText("Raw Distance:");
		
		txtBoxDistFile = new Text(composite, SWT.BORDER);
		txtBoxDistFile.setLocation(117, 7);
		txtBoxDistFile.setSize(492, 20);
		if(fileName != null) {
			txtBoxDistFile.setText(fileName);
		}
		
		btnBrowse = new Button(composite, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(new String[] {"*.dist"});
				fd.setFilterNames(new String[] {"Distance File (*.dist)"});
				
				String fdResult = fd.open();
				
				if( fdResult != null ) {
					txtBoxDistFile.setText(fdResult);
					fileName = fdResult;
				}
				
				getWizard().getContainer().updateButtons();
			}
		});
		btnBrowse.setLocation(615, 7);
		btnBrowse.setSize(75, 23);
		btnBrowse.setText("Browse");
		
		txtBoxRawDistance = new Text(composite, SWT.BORDER);
		txtBoxRawDistance.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});
		txtBoxRawDistance.setLocation(117, 32);
		txtBoxRawDistance.setSize(492, 97);
		txtBoxRawDistance.setEnabled(false);
		
		btnLowertriangularDataMatrix = new Button(container, SWT.CHECK);
		btnLowertriangularDataMatrix.setBounds(10, 168, 187, 17);
		btnLowertriangularDataMatrix.setText("Lower-triangular data matrix");
		
		btnUppertriangularDataMatrix = new Button(container, SWT.CHECK);
		btnUppertriangularDataMatrix.setBounds(10, 191, 187, 17);
		btnUppertriangularDataMatrix.setText("Upper-triangular data matrix");
		
		btnSubreplictes = new Button(container, SWT.CHECK);
		btnSubreplictes.setBounds(352, 168, 187, 17);
		btnSubreplictes.setText("Subreplictes");
		
		btnRandomizeInputOrder = new Button(container, SWT.CHECK);
		btnRandomizeInputOrder.setBounds(352, 191, 187, 17);
		btnRandomizeInputOrder.setText("Randomize Input Order");
		
		btnPrintTree = new Button(container, SWT.CHECK);
		btnPrintTree.setSelection(true);
		btnPrintTree.setBounds(352, 216, 187, 17);
		btnPrintTree.setText("Print Tree in final output?");
		
		composite_1 = new Composite(container, SWT.NONE);
		composite_1.setBounds(0, 145, 220, 17);
		
		btnNeighborjoining = new Button(composite_1, SWT.RADIO);
		btnNeighborjoining.setSelection(true);
		btnNeighborjoining.setBounds(10, 0, 138, 17);
		btnNeighborjoining.setText("Neighbor-Joining");
		
		btnUpgma = new Button(composite_1, SWT.RADIO);
		btnUpgma.setBounds(154, 0, 66, 17);
		btnUpgma.setText("UPGMA");
		
		txtOutgroupRoot = new Text(container, SWT.BORDER);
		txtOutgroupRoot.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				int root = Integer.parseInt(txtOutgroupRoot.getText());
				if(root >50){
					txtOutgroupRoot.setText("50");
					setErrorMessage("The outgroup root must be between 1-50.");
				} else if (root < 1) {
					txtOutgroupRoot.setText("1");
					setErrorMessage("The outgroup root must be between 1-50.");
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				setErrorMessage(null);
			}
		});
		txtOutgroupRoot.setEnabled(false);
		txtOutgroupRoot.setBounds(155, 215, 52, 21);
		
		btnUseOutgroupRoot = new Button(container, SWT.CHECK);
		btnUseOutgroupRoot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setErrorMessage(null);
				txtOutgroupRoot.setEnabled(btnUseOutgroupRoot.getSelection());
			}
		});
		btnUseOutgroupRoot.setBounds(10, 216, 139, 17);
		btnUseOutgroupRoot.setText("Use Outgroup Root:");
	}
	
	public String getOutputFileName() {
		return outputFilename;
	}

	public String getInputFileName() {
		return fileName;
	}
	
	public void setOutputFileName(String newFileName) {
		this.outputFilename = newFileName;
	}
	
	public void setInputFile(String newFileName) {
		this.fileName = newFileName;
		
		if(txtBoxDistFile != null) {
			txtBoxDistFile.setText(fileName);
		}
	}
	
	@Override
	public boolean isPageComplete() {
		
		if (btnDistanceFile != null && btnDistanceFile.getSelection()) {
			return ( (this.txtBoxDistFile.getText() != null) && !(this.txtBoxDistFile.getText().isEmpty()) );
			
		} else if (btnRawDistance != null && btnRawDistance.getSelection()) {
			return ( (this.txtBoxRawDistance.getText() != null) && !(this.txtBoxRawDistance.getText().isEmpty()) );
			
		} else {
			// Should never get here since the user should only be able to select
			//  a distance file or upload raw distance information.
			return false;
			
		}
	}
	
	@Override
	public String getArgs() {
		
		String outputNbrFile;
		String outputTreeFile;
		
		if(outputFilename.indexOf(".") > 0) {
			outputNbrFile = outputFilename.substring(0, outputFilename.indexOf(".")) + ".nbr";
			outputTreeFile = outputFilename.substring(0, outputFilename.indexOf(".")) + ".tree";
		} else {
			outputNbrFile = outputFilename.concat(".nbr");
			outputTreeFile = outputFilename.concat(".tree");
		}
		
		StringBuilder args = new StringBuilder(txtBoxDistFile.getText()).append("\n");
		args.append("F\n");
		if( (outputNbrFile != null) && !(outputNbrFile.isEmpty())) {
			args.append(outputNbrFile).append("\n");
		} else {
			args.append(txtBoxDistFile.getText()).append(".nbr\n");
		}
		
		if(btnUpgma.getSelection()) {
			args.append("N\n");
		}
		
		if(btnLowertriangularDataMatrix.getSelection()) {
			args.append("L\n");
		}
		
		if(btnUppertriangularDataMatrix.getSelection()) {
			args.append("R\n");
		}
		
		if(btnSubreplictes.getSelection()) {
			args.append("S\n");
		}
		
		if(btnUseOutgroupRoot.getSelection()) {
			args.append("O\n");
			args.append(txtOutgroupRoot.getText() + "\n");
		}
		
		if(btnRandomizeInputOrder.getSelection()) {
			args.append("R\n").append("7\n");
		}
		
		if(!btnPrintTree.getSelection()) {
			args.append("3\n");
		}
		
		// Accept the menu as displayed
		args.append("Y\n");
		
		// Indicate the file to write the output tree to
		args.append("F\n");
		if( (outputTreeFile != null) && !(outputTreeFile.isEmpty())) {
			args.append(outputTreeFile).append("\n");
		} else {
			args.append(txtBoxDistFile.getText()).append(".nbr\n");
		}
		
		// Add an extra return to exit phylip normally
		args.append("\n\n");
		
		return args.toString();
	}
}
