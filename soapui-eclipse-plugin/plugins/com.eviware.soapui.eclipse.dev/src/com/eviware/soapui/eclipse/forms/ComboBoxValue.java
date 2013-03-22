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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Not tested ComboBox in Eclipse preference pages
 * 
 * @author Lars H
 */
public class ComboBoxValue implements FormValue
{
	private Combo comboBox;
	private final String caption;
	private Label label;

	public ComboBoxValue( Composite parent, String caption, String[] list, String description, String value )
	{
		this.caption = caption;
		label = new Label( parent, SWT.LEFT );
		label.setText( caption );
		label.setToolTipText( description ); // TODO Eclipse: Tooltip does not
															// work.
		label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

		comboBox = new Combo( parent, SWT.DROP_DOWN );
		comboBox.setItems( list );

		int selection = getSelectedIndex( list, value );
		if( selection >= 0 )
			comboBox.select( selection );

		// comboBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridData layout = new GridData( GridData.FILL_HORIZONTAL );
		layout.grabExcessHorizontalSpace = true;
		layout.horizontalSpan = 2;
		comboBox.setLayoutData( layout );
		// comboBox.setLayoutData(new GridData(
		// GridData.FILL_HORIZONTAL, GridData.VERTICAL_ALIGN_BEGINNING, true,
		// false, 2, 1));
	}

	private int getSelectedIndex( String[] list, String value )
	{
		for( int i = 0; i < list.length; i++ )
		{
			if( value.equals( list[i] ) )
				return i;
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eviware.soapui.eclipse.preferences.NameValue#getName()
	 */
	public String getName()
	{
		return caption;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eviware.soapui.eclipse.preferences.NameValue#getValue()
	 */
	public String getValue()
	{
		int selection = comboBox.getSelectionIndex();
		if( selection >= 0 )
			return comboBox.getItem( selection );
		else
			return comboBox.getText();
	}
}
