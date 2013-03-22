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

import java.io.IOException;

import org.eclipse.jface.action.Action;

import com.eviware.soapui.impl.wsdl.actions.project.AddInterfaceActionFromURL;
import com.eviware.soapui.model.project.Project;

/**
 * 
 * @author Lars H
 */
public class EclipseAddInterfaceFromURLAction extends AdaptSoapuiObjectAction<Project>
{
	public EclipseAddInterfaceFromURLAction()
	{
		super( Project.class, AddInterfaceActionFromURL.class );
	}

	@Override
	protected void executeAction( Project selectedElement, Action action ) throws IOException
	{
		action.run();
		selectedElement.save();
	}
}
