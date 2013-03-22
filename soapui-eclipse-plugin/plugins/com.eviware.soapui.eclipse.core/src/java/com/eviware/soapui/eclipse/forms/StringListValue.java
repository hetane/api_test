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

import java.io.File;

import org.apache.xmlbeans.XmlException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.types.StringList;

/**
 *
 * @author Dain Nilsson
 */
public class StringListValue implements FormValue {
	private Label label;
	private List listField;
	private Button add, edit, remove;
	private String defaultValue = null;
	
	public StringListValue(Composite parent, String title, String tooltip, String value) {
		this(parent,title, tooltip, value, null);
	}
	
	public StringListValue(Composite parent, String title, String tooltip, String value, String defaultValue) {
		this.defaultValue = defaultValue;
		label = new Label(parent, SWT.LEFT);
		label.setText(title);
		label.setToolTipText(tooltip);  // TODO Eclipse: Tooltip does not work.
		GridData layout = new GridData(GridData.FILL_HORIZONTAL);
		layout.verticalSpan = 3;
		label.setLayoutData(layout);
		
		listField = new List(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		
		try {
			StringList stringList = StringList.fromXml( value );
			String[] files = stringList.toStringArray();
			for( String file : files )
				if( file.trim().length() > 0)
					listField.add(file);
		} catch (XmlException e) {
			SoapUI.logError( e );
		}
		
		layout = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		layout.verticalSpan = 3;
		listField.setLayoutData(layout);
		
		add = new Button(parent, SWT.NONE);
		add.setText("Add...");
		add.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				String value = UISupport.prompt( "Specify value to add", "Add...", "" );
				listField.add(value);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		edit = new Button(parent, SWT.NONE);
		edit.setText("Edit...");
		edit.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				int selectedIndex = listField.getSelectionIndex();
				if(selectedIndex != -1) {
					String value = UISupport.prompt( "Specify value", "Edit..", listField.getItem(selectedIndex) );
					if(value != null) {
						listField.setItem(selectedIndex, value);
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		remove = new Button(parent, SWT.NONE);
		remove.setText("Remove...");
		remove.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				int selectedIndex = listField.getSelectionIndex();
				if(selectedIndex != -1) {
					if( UISupport.confirm( "Remove [" + listField.getItem(selectedIndex) + "] from list", "Remove" ))
					{
						listField.remove( selectedIndex );
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
	}

	public String getName() {
		return label.getText();
	}

	public String getValue() {
		StringList result = new StringList(listField.getItems());
		return result.toXml();
	}

}
