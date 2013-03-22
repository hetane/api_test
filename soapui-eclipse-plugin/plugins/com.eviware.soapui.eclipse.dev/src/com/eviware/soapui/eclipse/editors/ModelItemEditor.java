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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.eviware.soapui.eclipse.SoapUIActivator;

/**
 * 
 * @author Lars H
 */
public class ModelItemEditor extends MultiPageEditorPart
{
	/** The editor id as defined in plugin.xml */
	public static final String EDITOR_ID = "com.eviware.soapui.editors.SoapUIEditor";

	private static final int REQUEST_PAGE = 0;
	private static final int RESPONSE_PAGE = 1;

	/** The text editor used in page 0. */
	private MessageEditor requestEditor;

	/** The text editor used in page 1. */
	private MessageEditor responseEditor;

	/**
	 * 
	 */
	public ModelItemEditor()
	{
		super();
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages()
	{
		try
		{
			/*
			 * ModelItemEditorInput editorInput = (ModelItemEditorInput)
			 * getEditorInput(); setPartName(editorInput.getName());
			 * 
			 * requestEditor = new MessageEditor(); int index =
			 * addPage(requestEditor, editorInput.getRequestEditorInput());
			 * setPageText(index, "Request");
			 * 
			 * responseEditor = new MessageEditor(); index =
			 * addPage(responseEditor, editorInput.getResponseEditorInput());
			 * setPageText(index, "Response");
			 */
		}
		catch( Exception e )
		{
			SoapUIActivator.logError( "Error creating editor", e );
		}
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave( IProgressMonitor monitor )
	{
		requestEditor.doSave( monitor );
	}

	/**
	 * Does nothing. Save as is not allowed.
	 */
	public void doSaveAs()
	{
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker( IMarker marker )
	{
		setActivePage( REQUEST_PAGE );
		IDE.gotoMarker( requestEditor, marker );
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	public void showResponseView()
	{
		setActivePage( RESPONSE_PAGE );
		DesktopPanelEditorInput editorInput = ( DesktopPanelEditorInput )getEditorInput();
		// WsdlRequest request = editorInput.getRequest();
		// responseEditor.setInput(new ResponseEditorInput(request));
	}

	/**
	 * Update the request editor. This is used when a request has been Recreated.
	 */
	public void updateRequest()
	{
		requestEditor.updateRequest();
	}
}
