package net.bioclipse.bioinformatics.ciba.wizards.phylip.Drawtree;

import java.io.File;

import net.bioclipse.bioinformatics.ciba.main.CibaPreferences;
import net.bioclipse.bioinformatics.ciba.wizards.AbsCibaWizardPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class DrawtreeWizardPage extends AbsCibaWizardPage {
	private Text txtVerticalResolution;
	private Text txtHorizontalResolution;
	private Combo comboLabelAngle;
	private Combo comboUseBranchLengths;
	private Text treeFile;
	
	private DrawtreeRefreshListener refreshListener = new DrawtreeRefreshListener();
	private Spinner spinnerTreeRotation;
	
	private File uploadFile = null;
	private String outputFilename = null;

	public DrawtreeWizardPage() {
		super("Phylip Drawtree Wizard");
		setTitle("Phylip - Drawtree");
		setDescription("Choose the Drawtree parameters below...");
	}

	@Override
	public void initGUI(Composite parent) {
		//public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label label = new Label(container, SWT.SEPARATOR | SWT.VERTICAL);
		label.setBounds(248, 48, 2, 230);
		
		Label lblUseBranchLengths = new Label(container, SWT.NONE);
		lblUseBranchLengths.setAlignment(SWT.RIGHT);
		lblUseBranchLengths.setBounds(10, 51, 122, 17);
		lblUseBranchLengths.setText("Angle of Labels:");
		
		Label lblUseBranchLengths_1 = new Label(container, SWT.NONE);
		lblUseBranchLengths_1.setBounds(10, 87, 122, 17);
		lblUseBranchLengths_1.setText("Use branch lengths:");
		lblUseBranchLengths_1.setAlignment(SWT.RIGHT);
		
		Label lblTreeRotation = new Label(container, SWT.NONE);
		lblTreeRotation.setBounds(10, 125, 122, 17);
		lblTreeRotation.setText("Tree rotation:");
		lblTreeRotation.setAlignment(SWT.RIGHT);
		
		Label lblImageResolution = new Label(container, SWT.NONE);
		lblImageResolution.setBounds(10, 163, 122, 17);
		lblImageResolution.setText("Image resolution:");
		lblImageResolution.setAlignment(SWT.RIGHT);
		
		comboLabelAngle = new Combo(container, SWT.NONE);
		comboLabelAngle.addSelectionListener(refreshListener);
		comboLabelAngle.setItems(new String[] {"Fixed", "Radial", "Along", "Middle"});
		comboLabelAngle.setBounds(138, 48, 104, 23);
		comboLabelAngle.select(3);
		
		comboUseBranchLengths = new Combo(container, SWT.NONE);
		comboUseBranchLengths.addSelectionListener(refreshListener);
		comboUseBranchLengths.setItems(new String[] {"Yes", "No"});
		comboUseBranchLengths.setBounds(138, 86, 104, 23);
		comboUseBranchLengths.select(0);
		
		txtVerticalResolution = new Text(container, SWT.BORDER);
		txtVerticalResolution.addFocusListener(new FocusAdapter() {
			
		});
		txtVerticalResolution.setText("1024");
		txtVerticalResolution.setBounds(138, 162, 41, 23);
		
		Label lblX = new Label(container, SWT.NONE);
		lblX.setBounds(187, 165, 5, 18);
		lblX.setText("x");
		
		txtHorizontalResolution = new Text(container, SWT.BORDER);
		txtHorizontalResolution.addFocusListener(refreshListener);
		txtHorizontalResolution.setText("768");
		txtHorizontalResolution.setBounds(201, 162, 41, 23);
		
		Label lblDegrees = new Label(container, SWT.NONE);
		lblDegrees.setBounds(187, 125, 55, 17);
		lblDegrees.setText("degrees");
		
		treeFile = new Text(container, SWT.BORDER);
		treeFile.setBounds(10, 12, 599, 20);
		
		Button button = new Button(container, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(new String[] {"*.tree"});
				fd.setFilterNames(new String[] {"Tree File (*.tree)"});
				
				String fileName = fd.open();
				
				if(fileName != null) {
					treeFile.setText(fileName);
				}
				
				getWizard().getContainer().updateButtons();
				
			}
		});
		button.setBounds(615, 10, 75, 23);
		button.setText("Browse");
		
		spinnerTreeRotation = new Spinner(container, SWT.BORDER);
		spinnerTreeRotation.setTextLimit(360);
		spinnerTreeRotation.setPageIncrement(15);
		spinnerTreeRotation.setMaximum(360);
		spinnerTreeRotation.setSelection(360);
		spinnerTreeRotation.setBounds(138, 124, 47, 22);
		
		Composite composite = new Composite(container, SWT.BORDER | SWT.NO_BACKGROUND);
		composite.setBounds(256, 48, 434, 230);
	}
	
	@Override
	public boolean isPageComplete() {
		return ( (this.treeFile.getText() != null) && !(this.treeFile.getText().isEmpty()) );
	}
	
	
	public String getOutputFilename() {
		return this.outputFilename;
	}
	
	public void setOutputFileName(String newFilename) {
		this.outputFilename = newFilename;
	}
	
	@Override
	public String getArgs() {
		
		StringBuilder args = new StringBuilder();
		
		args.append(treeFile.getText()).append("\n");
		
		String fontFile = CibaPreferences.getStore().get(DrawtreeWizard.DRAWTREE_PREFERENCES_KEY, "").replace("drawtree.exe", "font1");
		args.append(fontFile).append("\n");
		
		// Set up no preview device
		args.append("V\n").append("N\n");
		
		// Set up the Always try to avoid overlap
		args.append("D\n");
		
		// Set up Angle of the branch label
		args.append("L\n");
		if(this.comboLabelAngle.getSelectionIndex() == 0) {
			args.append("F\n");
			
			// TODO: Fix the angle here so its user-definable
			args.append("90\n");
			
			
		} else if(this.comboLabelAngle.getSelectionIndex() == 1) {
			args.append("R\n");
			
		} else if(this.comboLabelAngle.getSelectionIndex() == 2) {
			args.append("A\n");
			
		} else if(this.comboLabelAngle.getSelectionIndex() == 3) {
			args.append("M\n");
			
		}
		
		
		// Set up Rotation angle of the tree
		args.append("R\n").append(spinnerTreeRotation.getSelection()).append("\n");
		
		// Set up use branch lengths, but only if the user selected "No"
		if(this.comboUseBranchLengths.getSelectionIndex() == 1) {
			args.append("B\n");
		}
		
		// Set up the image resolution
		args.append("P\n").append("W\n");
		args.append(txtHorizontalResolution.getText()).append("\n");
		args.append(txtVerticalResolution.getText()).append("\n");
		
		// Accept the options set above and exit drawtree
		args.append("Y\n");
		
		// Indicate the output file since "plotfile" exists
		// TODO: Fix this path - I don't want to put the image in the same directory as the source tree file, but rather the new 
		//       folder created by the previous wizard page (i.e. this needs to be defined at the "Wizard" level not here at the "WizardPage" level.
		if( (outputFilename != null) && !(outputFilename.isEmpty()) ) {
			args.append("F\n").append(outputFilename);
		} else {
			args.append("F\n").append(treeFile.getText().replace(".tree", ".bmp"));
		}
		
		args.append("\n\n");
		
		return args.toString();
	}

	private void refreshImage() {
		System.out.println("Image Refreshed");
	}
	
	
	private class DrawtreeRefreshListener implements FocusListener, SelectionListener {

		@Override
		public void focusGained(FocusEvent e) {
			// Do nothing on focus gain
		}

		@Override
		public void focusLost(FocusEvent e) {
			refreshImage();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			refreshImage();
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			refreshImage();
		}
		
	}
}
