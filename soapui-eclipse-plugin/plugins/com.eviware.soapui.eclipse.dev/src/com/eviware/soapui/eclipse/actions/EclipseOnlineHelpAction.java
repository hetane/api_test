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

package com.eviware.soapui.eclipse.actions;

import org.eclipse.ui.IWorkbenchPart;

import com.eviware.soapui.impl.wsdl.support.HelpUrls;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.support.Tools;

/**
 * 
 * @author Dain Nilsson
 */
public class EclipseOnlineHelpAction extends AbstractObjectAction<Project>
{
	public EclipseOnlineHelpAction()
	{
		super( Project.class );
	}

	@Override
	public void run( IWorkbenchPart activePart, Project selectedElement ) throws Exception
	{
		Tools.openURL( HelpUrls.HELP_URL_ROOT + "projects/index.html" );
	}
}
