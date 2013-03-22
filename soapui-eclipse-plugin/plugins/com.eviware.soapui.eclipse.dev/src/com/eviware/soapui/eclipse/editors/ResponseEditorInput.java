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
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.settings.WsdlSettings;
import com.eviware.soapui.support.xml.XmlUtils;

/**
 * 
 * @author Lars H
 */
public class ResponseEditorInput extends MessageEditorInput
{
	private StringStorage storage;

	public ResponseEditorInput( WsdlRequest request )
	{
		super( request );
		Response response = request.getResponse();
		String content;
		if( response == null )
		{
			content = "";
		}
		else
		{
			content = response.getContentAsString();

			if( request.getSettings().getBoolean( WsdlSettings.PRETTY_PRINT_RESPONSE_MESSAGES ) )
			{
				content = XmlUtils.prettyPrintXml( content );
			}
		}

		this.storage = new StringStorage( content, true );
	}

	public ImageDescriptor getImageDescriptor()
	{
		return ImageDescriptor.getMissingImageDescriptor();
	}

	public String getToolTipText()
	{
		return "SOAP response";
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
