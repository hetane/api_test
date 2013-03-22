/*
 *  soapUI, copyright (C) 2004-2009 eviware.com 
 *
 *  SoapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of the GNU Lesser General Public License as published by the Free Software Foundation; 
 *  either version 2.1 of the License, or (at your option) any later version.
 *
 *  SoapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */
 
 package com.eviware.x.impl.swt;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.eviware.soapui.settings.ProjectSettings;
import com.eviware.soapui.support.UISupport;
import com.eviware.x.form.XFormTextField;

public class SwtProjectFileFormField extends AbstractSwtXFormField<Text> implements XFormTextField
{
	private final static Logger log = Logger.getLogger(SwtProjectFileFormField.class);
	
	private Text component;
	private final boolean directoriesOnly;
	private String projectRoot;
	private Button button;

	public SwtProjectFileFormField( Composite panel, String tooltip, boolean directoriesOnly )
	{
		this.directoriesOnly = directoriesOnly;
      
		component = new Text( panel, SWT.BORDER );
		component.setToolTipText( tooltip );
      component.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      button = new Button( panel, SWT.NONE );
		button.setText("Browse...");
      button.addSelectionListener(new SelectFileAction());
	}
	
   public Text getComponent()
   {
      return component;
   }

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		button.setEnabled( enabled );
	}
   
	public void setValue(String value)
	{
		log.debug( "Setting value [" + value + "]" );
		if( value != null && projectRoot != null && value.startsWith( projectRoot ) )
		{
			value = value.substring( projectRoot.length()+1 );
			log.debug( "Changed value to [" + value + "]" );
		}
			
		component.setText( value != null ? value : "" );
	}

	public String getValue()
	{
		return component.getText().trim();
		/*
		if( projectRoot != null && text.length() > 0 )
		{
			String tempName = projectRoot + File.separatorChar + text;
			if( new File( tempName ).exists() )
			{
				text = tempName;
			}
		}
		
		return text;
*/	}
	
	private class SelectFileAction implements SelectionListener
	{
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }

      public void widgetSelected(SelectionEvent e)
      {
      	IProject project = null;
      	IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
      	String value = null;
      	
			// if project root is set check if it belongs to an eclipse project
      	if( projectRoot != null )
      	{
      		// we need to do this "backwards" since the project doesn't physically have to 
      		// in the workspace
      		int ix = projectRoot.lastIndexOf( File.separatorChar );
      		if( ix == -1 )
      		{
      			project = workspaceRoot.getProject( projectRoot );
      		}
      		else
      		{	
      			project = workspaceRoot.getProject( projectRoot.substring( ix+1 ) );
      			
      			// make sure if this is actually the same as the projectRoot
      			if( project != null && !project.getLocation().toOSString().equals( projectRoot ))
      			{
      				project = null;
      			}
      		}
      	}
      	
      	// use containerselectiondialog if possible
      	if( directoriesOnly )
      	{
      		if( project != null )
      		{
		      	ContainerSelectionDialog dialog = new ContainerSelectionDialog( component.getShell(), 
		      			project.getFolder( project.getName() + "/." ),
		      			true, "Select folder" );
		      	
		      	while( dialog.open() == Window.OK )
		      	{
		      		value = dialog.getResult()[0].toString().substring( 1 );
		      		if( value.startsWith( project.getName() ))
		      			break;
		      		else
		      			UISupport.showErrorMessage( "Must select folder in project [" + project.getName() + "]" );
		      	}
		      	
		      	value = projectRoot + value.substring( project.getName().length() ).replace( '/', File.separatorChar );
      		}
      		else
      		{
      			DirectoryDialog dialog = new DirectoryDialog( component.getShell() ); 
      			if( projectRoot != null )
      			{
      				dialog.setFilterPath( projectRoot );
      			}
      			
      			value = dialog.open();
      		}
      	}
      	else
      	{
      		FileDialog dialog = new FileDialog( component.getShell(), SWT.OPEN );
      		
      		if( projectRoot != null )
      		{
      			dialog.setFileName( projectRoot + File.separatorChar + "." );
      		}
      		
      		value = dialog.open();
      	}
      	
      	if( value != null )
         	setValue( value );
      }
	}

	public void setWidth(int columns)
	{
		// @todo implement
	}

	public void setProperty(String name, Object value)
	{
		super.setProperty(name, value);
		
		if( name.equals( ProjectSettings.PROJECT_ROOT ))
		{
			projectRoot = (String) value;
			log.debug( "Set projectRoot to [" + projectRoot + "]" );
		}
	}
	
	
}
