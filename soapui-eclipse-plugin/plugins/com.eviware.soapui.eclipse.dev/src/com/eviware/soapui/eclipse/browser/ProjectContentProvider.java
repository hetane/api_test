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

package com.eviware.soapui.eclipse.browser;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;

import com.eviware.soapui.eclipse.project.SoapuiAdapterFactory;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.PanelBuilder;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.util.PanelBuilderRegistry;
import com.eviware.soapui.support.UISupport;

/**
 * This class provides content in a SoapUI project to an Eclipse tree.
 * 
 * @author Lars H
 */
public class ProjectContentProvider extends AbstractContentProvider
{
	private final static Logger log = Logger.getLogger( ProjectContentProvider.class );

	public ProjectContentProvider()
	{
	}

	@Override
	protected void open( ModelItem modelItem )
	{
		try
		{
			PanelBuilder<ModelItem> panelBuilder = PanelBuilderRegistry.getPanelBuilder( modelItem );
			if( panelBuilder != null && panelBuilder.hasDesktopPanel() )
			{
				UISupport.showDesktopPanel( modelItem );
			}
		}
		catch( Exception e )
		{
			log.error( "Could not open request", e );
		}
	}

	public boolean hasChildren( Object element )
	{
		// Cannot do this at inputChanged, because the menu is not created at that
		// time.
		initMenuAppender();

		return super.hasChildren( element );
	}

	public Object[] getChildren( Object parent )
	{
		if( parent instanceof IFile )
		{
			Project soapuiProject = SoapuiAdapterFactory.findSoapuiProject( ( IFile )parent );
			if( soapuiProject != null )
			{
				return getChildren( soapuiProject );
			}
		}

		return super.getChildren( parent );
	}
}
