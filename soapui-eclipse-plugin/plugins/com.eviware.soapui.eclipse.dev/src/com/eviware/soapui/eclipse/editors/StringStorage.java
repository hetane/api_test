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

package com.eviware.soapui.eclipse.editors;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.*;

/**
 * 
 * @author Lars H
 */
public class StringStorage implements IStorage
{
	private String contents;
	private boolean readOnly;

	public StringStorage( String contents, boolean readOnly )
	{
		this.contents = contents;
		this.readOnly = readOnly;
	}

	public InputStream getContents() throws CoreException
	{
		if( contents == null )
			contents = "";
		return new ByteArrayInputStream( contents.getBytes() );
	}

	public IPath getFullPath()
	{
		return new Path( "no path" );
	}

	public String getName()
	{
		return "no name";
	}

	public boolean isReadOnly()
	{
		return readOnly;
	}

	public Object getAdapter( Class adapter )
	{
		return null;
	}
}
