/*
 *  soapUI, copyright (C) 2004-2011 smartbear.com 
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.eclipse.forms;

import java.awt.Font;
import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.eviware.soapui.actions.EditorPrefs;
import com.l2fprod.common.swing.JFontChooser;

/**
 * 
 * @author Dain Nilsson
 */
public class FontValue implements FormValue {
	private Button button;
	private Label label;
	private Text textField;

	public FontValue(Composite parent, String title, String value) {
		label = new Label(parent, SWT.LEFT);
		label.setText(title);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		textField = new Text(parent, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		if (value != null)
			textField.setText(value);
		textField.setEditable(false);
		textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		button = new Button(parent, SWT.NONE);
		button.setText("Select Font...");
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Font font = JFontChooser.showDialog(null,
						"Select XML Editor Font", Font
								.decode(textField.getText()));

				if (font != null)
					textField.setText(EditorPrefs.encodeFont(font));
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eviware.soapui.eclipse.preferences.NameValue#getName()
	 */
	public String getName() {
		return label.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eviware.soapui.eclipse.preferences.NameValue#getValue()
	 */
	public String getValue() {
		return textField.getText();
	}
}
