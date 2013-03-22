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

package com.eviware.soapui.eclipse.ui.popup.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

import com.eviware.soapui.eclipse.SoapUIActivator;
import com.eviware.soapui.eclipse.browser.BrowserView;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.support.UISupport;

public class AddWsdlToSoapUIProjectAction implements IObjectActionDelegate {

	private ISelection currentSelection;

	/**
	 * Constructor for Action1.
	 */
	public AddWsdlToSoapUIProjectAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) 
	{
		if( ! ( currentSelection instanceof IStructuredSelection ))
		{
			UISupport.showErrorMessage( "Invalid selection type" );
			return;
		}

		IStructuredSelection sel = (IStructuredSelection) currentSelection;
		
		Object firstElement = sel.getFirstElement();
		if( ! ( firstElement instanceof IFile ))
		{
			UISupport.showErrorMessage( "Selection must be a file" );
			return;
		}
		
		Shell shell = new Shell();
		IFile file = (IFile) firstElement;
		
		IStructuredContentProvider provider = new IStructuredContentProvider()
		{
			public void dispose()
			{
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
			}

			public Object[] getElements(Object inputElement)
			{
				Workspace workspace = (Workspace) inputElement;
				List<Project> projects = new ArrayList<Project>();
				for( int c = 0; c < workspace.getProjectCount(); c++ )
					projects.add( workspace.getProjectAt( c ));
				
				return projects.toArray();
			}};
			
		ListDialog dialog = new ListDialog( shell );
		dialog.setContentProvider( provider );
		dialog.setLabelProvider( new LabelProvider()
		{
			public String getText(Object element)
			{
				return ((Project)element).getName();
			}} );
		
		dialog.setTitle( "Add WSDL to SoapUI Project" );
		dialog.setMessage( "Select the SoapUI project to which the WSDL will be added" );
		dialog.setInput( SoapUIActivator.getWorkspace() ); 
				
		if( dialog.open() == Dialog.OK )
		{
			ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell );
			try
			{
				pmd.run( false, false, new WsdlImporter(dialog.getResult(), file) );
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) 
	{
		currentSelection = selection;
	}
	
	public final static class WsdlImporter implements IRunnableWithProgress
	{
		private final Object[] projects;
		private final IFile file;

		private WsdlImporter(Object[] objects, IFile file)
		{
			this.projects = objects;
			this.file = file;
		}

		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
		{
			monitor.beginTask( "Importing WSDL from [" + file.toString() + "]", IProgressMonitor.UNKNOWN );
			
			for( int c = 0; c < projects.length; c++ )
			{
				WsdlProject project = (WsdlProject) projects[c];
				try
				{
					monitor.subTask( "Adding to project [" + project.getName() + "]" );
					WsdlInterface[] iface = com.eviware.soapui.impl.wsdl.support.wsdl.WsdlImporter.importWsdl(
					      project, file.getLocationURI().toString() );
					
					BrowserView view = (BrowserView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
						showView( "com.eviware.soapui.browser.soapui_projects" );
					
					view.selectModelItem( project );
					view.selectModelItem( iface[0] );
				}
				catch (Exception e)
				{
					UISupport.showErrorMessage( e );
				}
			}
		}
	}
}
