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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.editors.text.TextEditor;

import com.eviware.soapui.eclipse.SoapUIActivator;

/**
 * An editor for a SOAP request or response.
 * 
 * @author Lars H
 */
class MessageEditor extends TextEditor
{
	private ColorManager colorManager;

	MessageEditor()
	{
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration( new XMLConfiguration( colorManager ) );
		setDocumentProvider( new RequestDocumentProvider() );
	}

	public void dispose()
	{
		colorManager.dispose();
		super.dispose();
	}

	/**
	 * Update the request editor. This is used when a request has been Recreated.
	 * TODO Eclipse: Unsaved changes are lost.
	 */
	public void updateRequest()
	{
		try
		{
			doSetInput( getEditorInput() );
		}
		catch( CoreException e )
		{
			SoapUIActivator.logError( "Update failed", e );
		}
	}
}
