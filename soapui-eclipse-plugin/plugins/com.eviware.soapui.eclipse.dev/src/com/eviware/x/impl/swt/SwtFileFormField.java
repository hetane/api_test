/*
 *  soapUI, copyright (C) 2004-2011 eviware.com 
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */
 
 package com.eviware.x.impl.swt;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.eviware.soapui.settings.ProjectSettings;
import com.eviware.soapui.support.UISupport;
import com.eviware.x.form.XFormTextField;

public class SwtFileFormField extends AbstractSwtXFormField<Text> implements XFormTextField
{
	private Text component;
	private String componentValue;
	private final boolean directoriesOnly;
	private Button button;
	private String projectRoot;

	public SwtFileFormField(Composite panel, String tooltip, boolean directoriesOnly) {
		this.directoriesOnly = directoriesOnly;

		component = new Text(panel, SWT.BORDER);
		component.setToolTipText(tooltip);
		component.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		component.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				fireValueChanged(getValue(), null);
			}
		});

		button = new Button(panel, SWT.NONE);
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
		if( value != null && projectRoot != null && value.startsWith( projectRoot ) )
		{
			value = value.substring( projectRoot.length()+1 );
		}
		
		componentValue = value != null ? value : "";
		if(!component.isDisposed())
			component.setText(componentValue);
	}

	public String getValue()
	{
		if(!component.isDisposed())
			componentValue = component.getText();
		return componentValue;
	}
	
	private class SelectFileAction implements SelectionListener
	{
      public void widgetDefaultSelected(SelectionEvent e)
      {
      }

      public void widgetSelected(SelectionEvent e)
      {
         File defaultDirectory = null;
         if( component.getText().length() > 0 )
            defaultDirectory = new File( component.getText() );

         if(directoriesOnly)
         {
            File directory = UISupport.getFileDialogs().openDirectory(
                  SwtFileFormField.this, "Select Folder", defaultDirectory);
            if( directory != null )
            {
               setValue( directory.getAbsolutePath());
            }
         }
         else
         {
            // TODO Eclipse: Set title and file type. Same in the Swing code? 
            //File directory = UISupport.getFileDialogs().openXML(
             //     SwtFileFormField.this, "Select File");
            File directory = UISupport.getFileDialogs().open(SwtFileFormField.this, "Select File", null, null, null);
            if( directory != null )
            {
            	setValue( directory.getAbsolutePath());
            }
            
         }
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
		}
	}
}
