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

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.resource.ImageDescriptor;

import com.eviware.soapui.impl.wsdl.WsdlRequest;

/**
 * Editor input for a SOAP request.
 * 
 * @author Lars H
 */
public class RequestEditorInput extends MessageEditorInput
{
	private StringStorage storage;

	public RequestEditorInput( WsdlRequest request )
	{
		super( request );

		String content = request.getRequestContent();
		this.storage = new StringStorage( content, false );
	}

	public ImageDescriptor getImageDescriptor()
	{
		return ImageDescriptor.getMissingImageDescriptor();
	}

	public String getToolTipText()
	{
		return "SOAP request";
	}

	public Object getAdapter( Class adapter )
	{
		return null;
	}

	/**
	 * Returns the underlying IStorage object.
	 * 
	 * @return an IStorage object.
	 */
	public IStorage getStorage()
	{
		return storage;
	}
}
