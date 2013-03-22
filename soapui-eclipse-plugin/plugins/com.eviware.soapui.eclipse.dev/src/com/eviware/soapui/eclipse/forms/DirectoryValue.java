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

import org.eclipse.swt.widgets.Composite;

import com.eviware.soapui.support.UISupport;

/**
 * 
 * @author Lars H
 */
public class DirectoryValue extends AbstractFileValue
{
	public DirectoryValue( Composite parent, String title, String value, String tooltip )
	{
		super( parent, title, value, tooltip );
	}

	protected File selectFile()
	{
		File currentDirectory = null;
		if( getValue().length() > 0 )
			currentDirectory = new File( getValue() );
		return UISupport.getFileDialogs().openDirectory( this, "Select the location of " + getName(), currentDirectory );
	}
}
