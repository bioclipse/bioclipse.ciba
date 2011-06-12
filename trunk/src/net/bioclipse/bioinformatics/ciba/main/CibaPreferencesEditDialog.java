/* *****************************************************************************
 *Copyright (c) 2008-2009 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *     
 * Note:
 *     This Dialog was copied from the UpdateSitesEditDialog and modified to
 *     be specific to the CIBA plugin by Daniel F. Surdyk
 *******************************************************************************/
package net.bioclipse.bioinformatics.ciba.main;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


public class CibaPreferencesEditDialog extends TitleAreaDialog{

	private String[] programExecutable = new String[2];
	private Text txtName;
	private Text txtLocation;
	private String name;
	private String location;


	/**
	 * @wbp.parser.constructor
	 */
	public CibaPreferencesEditDialog(Shell parentShell) {
		this(parentShell,"","");
	}

	public CibaPreferencesEditDialog(Shell shell, String name, String loc) {
		super(shell);
		this.name=name;
		this.location=loc;
	}

	protected Control createDialogArea(Composite parent) {

		setTitle("Custom Executable Location");
		setMessage("Enter or browse to the location of the Executable you wish to use:");


		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Program:");

		txtName = new Text(container, SWT.BORDER);
		txtName.setEditable(false);
		GridData gridData_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gridData_1.widthHint = 482;
		txtName.setLayoutData(gridData_1);
		txtName.setText(name);

		final Label lblLocation = new Label(container, SWT.NONE);
		lblLocation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLocation.setText("Location:");

		txtLocation = new Text(container, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gridData.widthHint = 460;
		txtLocation.setLayoutData(gridData);
		txtLocation.setText(location);
		new Label(container, SWT.NONE);

		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnBrowse.setText("Browse");
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);

				// TODO: Fix the filter extensions for the given execution environment.
				fd.setFilterNames(new String[] {"Windows Executables", "OS-X Applications", "Linux/Unix Shell Scripts", "All"});
				fd.setFilterExtensions(new String[] {"*.exe", "*.app", "*.sh", "*.*"});

				String fileLoc = fd.open();
				
				if( fileLoc != null) {
					txtLocation.setText(fileLoc);
				}
			}
		});


		return area;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			if (txtLocation.getText().length()<=0){
				showMessage("Location cannot be empty");
				return;
			}

			programExecutable[0]=txtName.getText();
			programExecutable[1]=txtLocation.getText();
			okPressed();
			return;
		}
		super.buttonPressed(buttonId);
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Custom Executable Location",
				message);
	}

	public String[] getCustomExecutable() {
		return programExecutable;
	}


	@Override
	protected boolean isResizable() {
		return true;
	}
}