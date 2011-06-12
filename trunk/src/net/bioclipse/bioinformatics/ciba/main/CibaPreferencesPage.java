/*
 * Note: This Dialog was copied from the UpdateSitesEditDialog and modified to
 *       be specific to the CIBA plugin by Daniel F. Surdyk 
 */
package net.bioclipse.bioinformatics.ciba.main;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.bioinformatics.ciba.Activator;
import net.bioclipse.bioinformatics.ciba.wizards.clustalw.ClustalWizard;
import net.bioclipse.bioinformatics.ciba.wizards.phylip.DNADist.DNADistWizard;
import net.bioclipse.bioinformatics.ciba.wizards.phylip.Drawtree.DrawtreeWizard;
import net.bioclipse.bioinformatics.ciba.wizards.phylip.Neighbor.NeighborWizard;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class CibaPreferencesPage extends PreferencePage implements
IWorkbenchPreferencePage {

	public CibaPreferencesPage() {
		super.setTitle("Ciba Preferences");
	}

	//Init logger
	private static final Logger logger = Logger.getLogger(CibaPreferencesPage.class.toString());

	private static Preferences prefsStore = CibaPreferences.getStore();
	private List<String[]> appList;
	private TableViewer checkboxTableViewer;


	/**
	 * The label provider for the table that displays 2 columns: program and location
	 * @author ola
	 *
	 */
	class ApplicationsLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int index) {
			if (!(element instanceof String[])) return "Wrong type in column text";
			String[] retList = (String[]) element;

			if (index==0){
				if (retList.length>0)
					return retList[0];
				else
					return "NA";
			}
			else if (index==1){
				if (retList.length>1)
					return retList[1];
				else
					return "NA";

			}
			else
				return "???";
		}

	}

	class ApplicationsContentProvider implements IStructuredContentProvider {
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof ArrayList) {
				ArrayList retList = (ArrayList) inputElement;
				return retList.toArray();
			}
			return new Object[0];

		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}


	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setSize(new Point(600,420));
		container.setSize(600,420);
		container.setLayout(new FormLayout());

		checkboxTableViewer = new TableViewer(container, SWT.BORDER | SWT.SINGLE);
		checkboxTableViewer.setContentProvider(new ApplicationsContentProvider());
		checkboxTableViewer.setLabelProvider(new ApplicationsLabelProvider());
		final Table table = checkboxTableViewer.getTable();
		FormData formData = new FormData();
		formData.bottom = new FormAttachment(100, -201);
		formData.top = new FormAttachment(0, 10);
		formData.left = new FormAttachment(0, 11);
		table.setLayoutData(formData);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnProgram = new TableColumn(table, SWT.LEFT);
		tblclmnProgram.setText("Program");
		tblclmnProgram.setWidth(180);
		TableColumn tableColumn2 = new TableColumn(table, SWT.LEFT);
		tableColumn2.setText("Executable");
		tableColumn2.setWidth(411);

		appList=getPreferencesFromStore();
		checkboxTableViewer.setInput(appList);
		formData.right = new FormAttachment(100, -10);

		final Button editButton = new Button(container, SWT.NONE);
		FormData formData_2 = new FormData();
		formData_2.top = new FormAttachment(table, 6);
		formData_2.right = new FormAttachment(table, 0, SWT.RIGHT);
		formData_2.left = new FormAttachment(0, 539);
		editButton.setLayoutData(formData_2);
		editButton.setText("Edit");
		editButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {

				//Get selection from viewer
				ISelection sel=checkboxTableViewer.getSelection();
				if (!(sel instanceof IStructuredSelection)) {
					logger.debug("Item of wrong type selected.");
					showMessage("Please select an entry to edit first.");
					return;
				}

				IStructuredSelection ssel = (IStructuredSelection) sel;
				Object obj=ssel.getFirstElement();

				if (!(obj instanceof String[])) {
					logger.debug("Object of wrong type selected.");
					showMessage("Please select an entry to edit first.");
					return;
				}

				String[] chosen = (String[]) obj;

				CibaPreferencesEditDialog dlg=new CibaPreferencesEditDialog(getShell(), chosen[0], chosen[1]);

				dlg.open();

				String[] ret=dlg.getCustomExecutable();
				//If OK pressed
				if (dlg.getReturnCode()==0){
					if (ret.length==2){
						chosen[0]=ret[0]; // program
						chosen[1]=ret[1]; // location
						checkboxTableViewer.refresh();
						
					}
					else{
						logger.debug("Error getting result from dialog!");
						showMessage("Error getting result from dialog.");
					}
				}

			}
		});


		if (table.getItemCount()>0)
			table.setSelection(0);
		container.pack();
		return container;
	}


	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/**
	 * Override to store results
	 */
	public boolean performOk() {

		String val = appList.get(0)[1];
		this.prefsStore.put(ClustalWizard.CLUSTALW_PREFERENCES_KEY, val);
		System.out.println("Clustal = " + val);

		val = appList.get(1)[1];
		this.prefsStore.put(DNADistWizard.DNADIST_PREFERENCES_KEY, val);
		System.out.println("Phylip = " + val);

		CibaPreferences.save();

		return true;
	}

	/**
	 * @return List<String[]> containing the preferences
	 * 
	 */
	public static List<String[]> getPreferencesFromStore() {

		IEclipsePreferences prefs = Platform.getPreferencesService().getRootNode();
		Preferences instancePrefs = prefs.node("instance");
		Preferences cibaPrefs = instancePrefs.node(Activator.PLUGIN_ID);


		try {
			for( String key : cibaPrefs.childrenNames()) {
				System.out.println("Key: " + key);
			}
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		List<String[]> list = new ArrayList<String[]>();
		list.add(new String[] {"ClustalW 2.0.12", CibaPreferences.getStore().get(ClustalWizard.CLUSTALW_PREFERENCES_KEY, "<unknown> Please set now.")});
		list.add(new String[] {"Phylip - DNADist", CibaPreferences.getStore().get(DNADistWizard.DNADIST_PREFERENCES_KEY, "<unknown> Please set now.")});
		list.add(new String[] {"Phylip - Neighbor", CibaPreferences.getStore().get(NeighborWizard.NEIGHBOR_PREFERENCES_KEY, "<unknown> Please set now.")});
		list.add(new String[] {"Phylip - Drawtree", CibaPreferences.getStore().get(DrawtreeWizard.DRAWTREE_PREFERENCES_KEY, "<unknown> Please set now.")});

		return list;
	}


	@Override
	protected void performDefaults() {
		super.performDefaults();

		CibaPreferences.storeDefaults();

		appList=getPreferencesFromStore();
		checkboxTableViewer.setInput(appList);


	}

	

	private void showMessage(String message) {
		MessageDialog.openInformation(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Custom Executable Message",
				message);
	}
}
