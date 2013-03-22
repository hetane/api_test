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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * @author Lars H
 */
public class CheckBoxValue implements FormValue
{
	private Button checkBox;
	private Label label;

	public CheckBoxValue( Composite parent, String caption, String name, boolean selected )
	{
		// TODO Eclipse: Show both caption and label?
		label = new Label( parent, SWT.LEFT );
		label.setText( caption );
		label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

		checkBox = new Button( parent, SWT.CHECK );
		checkBox.setText( name );
		checkBox.setSelection( selected );
		GridData layout = new GridData( GridData.FILL_HORIZONTAL );
		layout.horizontalSpan = 2;
		checkBox.setLayoutData( layout );
		// checkBox.setLayoutData(new GridData(
		// GridData.FILL_HORIZONTAL, GridData.VERTICAL_ALIGN_BEGINNING, true,
		// false, 3, 1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eviware.soapui.eclipse.preferences.NameValue#getName()
	 */
	public String getName()
	{
		return label.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eviware.soapui.eclipse.preferences.NameValue#getValue()
	 */
	public String getValue()
	{
		return "" + checkBox.getSelection();
	}
}
