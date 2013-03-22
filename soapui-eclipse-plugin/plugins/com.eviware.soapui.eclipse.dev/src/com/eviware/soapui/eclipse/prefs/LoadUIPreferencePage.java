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

package com.eviware.soapui.eclipse.prefs;

import java.io.File;

import com.eviware.soapui.actions.LoadUIPrefs;
import com.eviware.soapui.actions.SoapUIPreferencesAction;
import com.eviware.soapui.eclipse.forms.FormBuilder;
import com.eviware.soapui.support.components.DirectoryFormComponent;

public class LoadUIPreferencePage extends AbstractPreferencePage<LoadUIPrefs>
{

	public LoadUIPreferencePage()
	{
		super( new LoadUIPrefs( SoapUIPreferencesAction.LOADUI_SETTINGS ) );
	}

	@Override
	protected void buildForm( FormBuilder loadPrefsForm )
	{
		DirectoryFormComponent directoryFormComponent = new DirectoryFormComponent( "Folder containing loadUI.bat(.sh) " );
		directoryFormComponent.setInitialFolder( System.getProperty( "soapui.home" ) + File.separator + ".."
				+ File.separator + ".." );
		//		loadPrefsForm.append( LoadUIPrefs.LOADUI_PATH, directoryFormComponent );
		loadPrefsForm.appendDirectory( "Folder containing loadUI.bat(.sh)", "Folder containing loadUI.bat(.sh)" );
		loadPrefsForm.appendTextField( LoadUIPrefs.LOADUI_CAJO_PORT, "Client port for loadUI integration" );
		loadPrefsForm.appendTextField( LoadUIPrefs.SOAPUI_CAJO_PORT,
				"Server port of soapUI integration (change requires restart of soapUI)" );
	}

}
