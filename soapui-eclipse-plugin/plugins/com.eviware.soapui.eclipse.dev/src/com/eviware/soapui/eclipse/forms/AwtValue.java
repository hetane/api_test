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

import javax.swing.JComponent;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import com.eviware.soapui.eclipse.util.SwtUtils;

/**
 * 
 * @author Dain Nilsson
 */
public class AwtValue implements FormValue
{

	public AwtValue( Composite parent, JComponent component, GridData layout )
	{
		if( layout == null )
			layout = new GridData( GridData.FILL_HORIZONTAL );
		layout.horizontalSpan = 3;
		SwtUtils.createAwtFrame( parent, component, layout );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eviware.soapui.eclipse.preferences.NameValue#getName()
	 */
	public String getName()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eviware.soapui.eclipse.preferences.NameValue#getValue()
	 */
	public String getValue()
	{
		return null;
	}
}
