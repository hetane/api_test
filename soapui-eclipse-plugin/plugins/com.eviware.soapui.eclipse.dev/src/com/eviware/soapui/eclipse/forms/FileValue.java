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
public class FileValue extends AbstractFileValue
{
	private String extension;
	private String fileType;

	public FileValue( Composite parent, String title, String value, String extension, String type, String tooltip )
	{
		super( parent, title, value, tooltip );
		this.extension = extension;
		this.fileType = type;
	}

	protected File selectFile()
	{
		String current = getValue();
		return UISupport.getFileDialogs()
				.open( this, "Select the location of " + getName(), extension, fileType, current );
	}
}
