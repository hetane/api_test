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

import org.eclipse.jface.viewers.Viewer;

import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.model.workspace.WorkspaceListener;

/**
 * This class is an adapter from SoapUI projects to an Eclipse tree model
 * 
 * @author Lars H
 */
public class WorkspaceContentProvider extends AbstractContentProvider
{
	private Workspace workspace;
	private WorkspaceListener workspaceListener;

	public WorkspaceContentProvider()
	{
	}

	public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
	{
		super.inputChanged( viewer, oldInput, newInput );

		if( workspace != null )
			dispose();
		workspace = ( Workspace )newInput;
		if( workspace != null )
		{
			workspaceListener = new InternalWorkspaceListener();
			workspace.addWorkspaceListener( workspaceListener );
		}
	}

	public Object[] getElements( Object inputElement )
	{
		Workspace workspace = ( Workspace )inputElement;
		Project[] elements = new Project[workspace.getProjectCount()];
		for( int i1 = 0; i1 < workspace.getProjectCount(); i1++ )
		{
			Project project = workspace.getProjectAt( i1 );
			elements[i1] = project;

			propertyListeners.addListener( project );
		}

		// Clean up removed projects.
		// TODO This shouldn't be necessary, if it is handled by remove.
		projectListeners.removeListenersForRemovedElements( elements );

		return elements;
	}

	public Object getParent( Object element )
	{
		if( element instanceof Project )
			return ( ( Project )element ).getWorkspace();
		else
			return super.getParent( element );
	}

	public void dispose()
	{
		if( workspace != null )
		{
			workspace.removeWorkspaceListener( workspaceListener );
			workspace = null;
		}

		super.dispose();
	}

	private class InternalWorkspaceListener implements WorkspaceListener
	{
		public void projectAdded( Project project )
		{
			updateView( workspace );
			// projectListeners.addListener(project);
		}

		public void projectChanged( Project project )
		{
			updateView( workspace );
		}

		public void projectRemoved( Project project )
		{
			// TODO This shouldn't be necessary if the model item clears all
			// listeners at remove.
			// projectListeners.removeListener(project);
			updateView( workspace );
		}

		public void workspaceSwitched( Workspace arg0 )
		{
			// TODO Auto-generated method stub

		}

		public void workspaceSwitching( Workspace arg0 )
		{
			// TODO Auto-generated method stub

		}

		public void projectClosed( Project arg0 )
		{
			// TODO Auto-generated method stub
			
		}

		public void projectOpened( Project arg0 )
		{
			// TODO Auto-generated method stub
			
		}
	}
}
