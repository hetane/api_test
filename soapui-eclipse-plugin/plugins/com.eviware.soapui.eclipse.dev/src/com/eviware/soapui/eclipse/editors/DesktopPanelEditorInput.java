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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.ui.desktop.DesktopPanel;

/**
 * Editor input for a SOAP request and response. TODO Eclipse: Close the editor
 * if the request/operation/interface/project is removed. TODO Eclipse: Change
 * the editor title if the request is renamed.
 * 
 * @author Lars H
 */
public class DesktopPanelEditorInput implements IEditorInput
{
	private DesktopPanel desktopPanel;

	public DesktopPanelEditorInput( DesktopPanel panel )
	{
		if( panel == null )
			throw new IllegalArgumentException( "panel is null" );
		this.desktopPanel = panel;
	}

	public DesktopPanel getDesktopPanel()
	{
		return desktopPanel;
	}

	public ImageDescriptor getImageDescriptor()
	{
		return ImageDescriptor.getMissingImageDescriptor();
	}

	public String getToolTipText()
	{
		ModelItem modelItem = desktopPanel.getModelItem();
		String toolTip = null;
		if( modelItem != null )
			toolTip = modelItem.getDescription();
		if( toolTip == null )
			toolTip = desktopPanel.getTitle();
		return toolTip;
	}

	public Object getAdapter( Class adapter )
	{
		return null;
	}

	public boolean equals( Object object )
	{
		if( object == null )
			return false;
		if( object.getClass() != getClass() )
			return false;
		DesktopPanelEditorInput that = ( DesktopPanelEditorInput )object;
		return this.desktopPanel.equals( that.desktopPanel );
	}

	public boolean exists()
	{
		return desktopPanel != null;
	}

	public String getName()
	{
		return desktopPanel.getTitle();
	}

	public IPersistableElement getPersistable()
	{
		return null;
	}
}
