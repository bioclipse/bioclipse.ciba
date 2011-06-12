package net.bioclipse.bioinformatics.ciba.wizards.clustalw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import net.bioclipse.bioinformatics.ciba.wizards.AbsCibaWizardPage;

import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class SeqInputPage extends AbsCibaWizardPage {
	
	private TabFolder tabFolder;
	private TabItem multSeqAlignTab;
	private TabItem profileAlignTab;
	private final int NEW_ALIGNMENT_TAB_INDEX = 0;
	private final int MERGE_ALIGNMENTS_TAB_INDEX = 1;
	
	/** 
	 * A boolean flag to distinguish whether the user wants to perform a new 
	 * multiple sequence alignment or wants to merge two previous alignments.
	 **/
	private boolean isNewAlignment = false;
	
	private Text sequenceTextbox;
	private Button btnUpload;
	private Button btnRemove;
	private Button btnClear;
	private List seqList;
	private HashMap<String, RichSequence> sequences = new HashMap<String, RichSequence>();
	private Text descriptionTextbox;
	
	/**  Shortcut to 'this' variable because when adding listeners and other methods
	     the widgetSelected() method does't have access to 'this,' but will have 
	     access to the final 'thisPage' shortcut  */
	private final SeqInputPage thisPage = this;
	private Text profile1Textbox;
	private Text profile2Textbox;
	private Button btnProfileBrowseFile1;
	private Button btnProfileBrowseFile2;
	private Button btnDisplayPreview;
	private Group profileOne;
	private Group grpAlignmentTwo;
	
	private String selectedFileName;

	/**
	 * Create the wizard.
	 */
	public SeqInputPage() {
		super("ClustalW 2.0");
		setTitle("ClustalW 2.0 - Sequence/Alignment Input Options");
		setDescription("Create a new alignment, or merge two previous alignments.");
	}
	
//	public SeqInputPage(IFile file) {
//		this();
//		
//		// If the user selected a previously-run *.fasta file
//		if(file != null && file.getFileExtension().equals(".fasta")) {
//			this.selectedFileName = file.getRawLocation().toOSString();
//		}
//	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void initGUI(Composite parent) {
		//public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.setSize(709, 288);
		tabFolder.addSelectionListener(new SelectionListener() {

			@Override 
			public void widgetDefaultSelected(SelectionEvent e) { 
				isNewAlignment = tabFolder.getSelectionIndex() == NEW_ALIGNMENT_TAB_INDEX;
				updateWizardNavigation();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				isNewAlignment = tabFolder.getSelectionIndex() == NEW_ALIGNMENT_TAB_INDEX;
				updateWizardNavigation();
			}
			
		});
		
		multSeqAlignTab = new TabItem(tabFolder, SWT.NONE);
		multSeqAlignTab.setText("New Multiple Sequence Alignment");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		multSeqAlignTab.setControl(composite_1);
		
		sequenceTextbox = new Text(composite_1, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		sequenceTextbox.setBounds(226, 40, 465, 210);
		
		descriptionTextbox = new Text(composite_1, SWT.BORDER);
		descriptionTextbox.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				
				// Only change the description if the user has selected one sequence
				String[] array = seqList.getSelection();
				if (array.length == 1) {
					sequences.get(array[0]).setDescription(descriptionTextbox.getText());
				}
			}
		});
		descriptionTextbox.setBounds(226, 9, 465, 21);
		
		seqList = new List(composite_1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		seqList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				// Clear any errors that may have been displayed
				thisPage.setErrorMessage(null);
				
				// Only display a preview of the sequence if the checkbox is selected, and 
				//   the user has only selected one sequence.
				if ( btnDisplayPreview.getSelection() && (seqList.getSelectionCount() == 1)){
					updateTextBoxes(sequences.get(seqList.getSelection()[0]));
				} else {
					updateTextBoxes(null);
				}
				
			}
		});
		seqList.setBounds(81, 40, 139, 210);
		
		btnUpload = new Button(composite_1, SWT.NONE);
		btnUpload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				thisPage.setErrorMessage(null);
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				
				String[] extensions = {"*.fasta", "*.txt", "*.*"};
				fd.setFilterExtensions(extensions);
				
				uploadSequencesFromFile(fd.open());
			}
		});
		btnUpload.setBounds(0, 7, 75, 25);
		btnUpload.setText("Upload");
		
		btnRemove = new Button(composite_1, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				thisPage.setErrorMessage(null);
				removeSelectedSequences(seqList.getSelection());
			}
		});
		btnRemove.setEnabled(false);
		btnRemove.setBounds(0, 37, 75, 25);
		btnRemove.setText("Remove");
		
		btnClear = new Button(composite_1, SWT.NONE);
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				thisPage.setErrorMessage(null);
				clearSequences();
			}
		});
		btnClear.setEnabled(false);
		btnClear.setBounds(0, 68, 75, 25);
		btnClear.setText("Clear List");
		
		btnDisplayPreview = new Button(composite_1, SWT.CHECK);
		btnDisplayPreview.setSelection(true);
		btnDisplayPreview.setBounds(81, 9, 139, 21);
		btnDisplayPreview.setText("Display Preview");
		
		profileAlignTab = new TabItem(tabFolder, SWT.NONE);
		profileAlignTab.setText("Merge Profile/Structure Alignments");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		profileAlignTab.setControl(composite_2);
		
		profileOne = new Group(composite_2, SWT.NONE);
		profileOne.setText("Alignment One");
		profileOne.setBounds(0, 10, 345, 250);
		
		profile1Textbox = new Text(profileOne, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		profile1Textbox.setLocation(10, 20);
		profile1Textbox.setSize(325, 190);
		
		btnProfileBrowseFile1 = new Button(profileOne, SWT.NONE);
		btnProfileBrowseFile1.setLocation(260, 215);
		btnProfileBrowseFile1.setSize(75, 25);
		btnProfileBrowseFile1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				uploadProfile1(new FileDialog(getShell(), SWT.OPEN).open());
			}
		});
		btnProfileBrowseFile1.setText("Browse");
		
		grpAlignmentTwo = new Group(composite_2, SWT.NONE);
		grpAlignmentTwo.setText("Alignment Two");
		grpAlignmentTwo.setBounds(351, 10, 345, 250);
		
		profile2Textbox = new Text(grpAlignmentTwo, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		profile2Textbox.setLocation(10, 20);
		profile2Textbox.setSize(325, 190);
		
		btnProfileBrowseFile2 = new Button(grpAlignmentTwo, SWT.NONE);
		btnProfileBrowseFile2.setLocation(260, 216);
		btnProfileBrowseFile2.setSize(75, 25);
		btnProfileBrowseFile2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				uploadProfile2(new FileDialog(getShell(), SWT.OPEN).open());
			}
		});
		btnProfileBrowseFile2.setText("Browse");
		
//		TODO: Future implementation to link the selected file in the workspace to this wizard.
//		ISSUE: Calling upload() calls the updateButtons() which calls the wizard framework's updateWizardButtons()
//		       and at the time of this method call (i.e. page creation) the next page is null, so one cannot check
//		       the isComplete() method for all pages and therefore enable the finish button.
//		if(this.selectedFileName != null) {
//			uploadSequencesFromFile(selectedFileName);
//		}
	}
	
	private void uploadSequencesFromFile(String file) {
		
		// Check if the user canceled the "Open" dialog.
		if( file == null ) {
			return;
		}
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			RichSequenceIterator iterator = RichSequence.IOTools.readFastaProtein( reader, RichObjectFactory.getDefaultNamespace()); 
			RichSequence rSeq;
			String key;
			double length;

			while (iterator.hasNext()) {
				rSeq = iterator.nextRichSequence();
				length = rSeq.length();
				key = rSeq.getAccession();
				
				// Anything 100,000 bases or more display as 0.1 M bases
				if (length > 100000) {
					length = Math.round(length/100000)/10.;
					key += (" (" + length + " M bases)");
				} else {
					key += (" (" + (int)length + " bases)");
				}
				
				if (!sequences.containsKey(key)){
					seqList.add(key);
					sequences.put(key, rSeq);
				}
			}
			reader.close();
			
		} catch(Exception e) {
			this.setErrorMessage("Unable to upload " + file + ":\n " + e.getMessage());
		}
		
		updateButtonEnabling();
	}
	
	private int getNumSequences() {
		return this.sequences.size();
	}
	
	private void uploadProfile1(String file) {
		profile1Textbox.setText("");
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String s;
			while ( (s = reader.readLine()) != null) {
				profile1Textbox.append(s + "\n");
			}
			reader.close();
			
		} catch(Exception e) {
			this.setErrorMessage("Unable to load " + file + ":\n " + e.getMessage());
		}
		
		updateWizardNavigation();
	}
	
	private void uploadProfile2(String file) {
		profile2Textbox.setText("");
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String s;
			while ( (s = reader.readLine()) != null) {
				profile2Textbox.append(s + "\n");
			}
			reader.close();
			
		} catch(Exception e) {
			this.setErrorMessage("Unable to load " + file + ":\n " + e.getMessage());
		}
		
		updateWizardNavigation();
	}
	
	private void removeSelectedSequences(String[] selectionList) {
		for (String item : selectionList) {
			sequences.remove(item);
			seqList.remove(item);
		}
		updateTextBoxes(null);
		updateButtonEnabling();
	}
	
	private void clearSequences() {
		sequences.clear();
		seqList.removeAll();
		updateTextBoxes(null);
		updateButtonEnabling();
	}
	
	private void updateTextBoxes(RichSequence se) {
		
		if (se == null) {
			descriptionTextbox.setText("");
			sequenceTextbox.setText("");
			
		} else if (se.length() > 500000) {
			thisPage.setErrorMessage("Cannot preview sequences > 500,000 bases");
			descriptionTextbox.setText("");
			sequenceTextbox.setText("");
			
		} else {
			descriptionTextbox.setText(se.getDescription());
			sequenceTextbox.setText(se.seqString());
			
		}
		
		updateWizardNavigation();
	}
	
	private void updateButtonEnabling() {
		btnUpload.setEnabled(true);
		btnRemove.setEnabled(seqList.getItemCount() > 0);
		btnClear.setEnabled(seqList.getItemCount() > 0);
		
		updateWizardNavigation();
	}
	
	private void updateWizardNavigation() {
		
		IWizard wzd = getWizard();
		
		if(wzd != null) {
			
			IWizardContainer container = wzd.getContainer();
			if(container != null) {
				container.updateButtons();
			}
		}
	}
	
	public boolean isNewAlignment() {
		return this.isNewAlignment;
	}
	
	public String getProfileOneData() {
		return this.profile1Textbox.getText();
	}
	
	public String getProfileTwoData() {
		return this.profile2Textbox.getText();
	}
	
	
	public String getNewAlignmentArgs() {
		
		StringBuilder buffer = new StringBuilder();
		RichSequence seq;
		for(String key : this.sequences.keySet()) {
			seq = this.sequences.get(key);
			buffer.append(">" + seq.getAccession() + " ");
			buffer.append(seq.getDescription() + "\n");
			buffer.append(seq.seqString() + "\n\n");
		}
		
		return buffer.toString();
	}
	
	
	
	@Override
	public boolean isPageComplete() {
		
		boolean result = false;
		
		if(this.tabFolder.getSelectionIndex() == NEW_ALIGNMENT_TAB_INDEX) {
			result = this.getNumSequences() > 1;
		} else if (this.tabFolder.getSelectionIndex() == MERGE_ALIGNMENTS_TAB_INDEX){
			result = !(this.profile1Textbox.getText().isEmpty() || this.profile2Textbox.getText().isEmpty());
		}
		
		return result;
	}

	
	/**
	 * This method does nothing.  See the following methods instead:<br>
	 * <code>getNewAlignmentArgs()</code><br/>
	 * <code>getProfileOneData()</code><br/>
	 * <code>getProfileTwoData()</code><br/>
	 */
	@Override
	public String getArgs() {
		
		// This has to be ignored since there are 2 different sets of arguments needed.
		return null;
	}
}
