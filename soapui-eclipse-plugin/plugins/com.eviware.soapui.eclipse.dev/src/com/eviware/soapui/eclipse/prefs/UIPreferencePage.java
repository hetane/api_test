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

import com.eviware.soapui.actions.SoapUIPreferencesAction;
import com.eviware.soapui.actions.UIPrefs;
import com.eviware.soapui.eclipse.forms.FormBuilder;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.settings.UISettings;
import com.eviware.soapui.support.types.StringToStringMap;

/**
 * 
 * @author Lars H
 */
public class UIPreferencePage extends AbstractPreferencePage<UIPrefs>
{
	public UIPreferencePage()
	{
		super( new InternalUIPrefs( SoapUIPreferencesAction.UI_SETTINGS ) );
	}

	@Override
	protected void buildForm( FormBuilder editorForm )
	{
		editorForm.appendCheckBox( UIPrefs.CLOSE_PROJECTS, "(close all projects on startup)", false );

		editorForm.appendCheckBox( UIPrefs.ORDER_PROJECTS, "(orders projects alphabetically in tree)", false );
		editorForm.appendCheckBox( UIPrefs.ORDER_REQUESTS, "(orders requests alphabetically in tree)", false );
		// editorForm.appendCheckBox( UIPrefs.ORDER_TESTSUITES,
		// "(orders TestSuites alphabetically in tree)", false );
		editorForm.appendCheckBox( UIPrefs.SHOW_DESCRIPTIONS, "(show description content when available)", false );

		editorForm.appendCheckBox( UIPrefs.AUTOSAVE_ONEXIT, "(automatically save all projects on exit)", true );
		editorForm.appendCheckBox( UIPrefs.CREATE_BACKUP, "(backup project files before they are saved)", true );
		editorForm.appendDirectory( UIPrefs.BACKUP_FOLDER, "(folder to backup to, can be both relative or absolute)" );
		editorForm.appendTextField( UIPrefs.AUTOSAVE_INTERVAL, "Sets the autosave interval in minutes, 0 = off" );

		editorForm.appendCheckBox( UIPrefs.NATIVE_LAF, "(use the native Look and Feel, requires restart)", false );

		editorForm.appendCheckBox( UIPrefs.ENABLE_GROOVY_LOG, "(do not disable the groovy log when running LoadTests)",
				true );

		editorForm.appendCheckBox( UIPrefs.LINEBREAK, "(normalize line-breaks when saving project)", false );

		editorForm.appendTextField( UIPrefs.GC_INTERVAL,
				"(sets the Garbage Collector interval in seconds, 0 means garbage collection is only performed by JRE)" );

		editorForm.appendTextField( UIPrefs.RAW_RESPONSE_MESSAGE_SIZE, "(sets the size of raw response mesage to show)" );
		editorForm.appendTextField( UIPrefs.RAW_REQUEST_MESSAGE_SIZE, "(sets the size of raw request mesage to show)" );
	}

	private static class InternalUIPrefs extends UIPrefs
	{
		public InternalUIPrefs( String title )
		{
			super( title );
		}

		@Override
		public void storeValues( StringToStringMap values, Settings settings )
		{
			super.storeValues( values, settings );
			settings.setBoolean( UISettings.NATIVE_LAF, values.getBoolean( NATIVE_LAF ) );
		}

		@Override
		public StringToStringMap getValues( Settings settings )
		{
			StringToStringMap values = super.getValues( settings );
			values.put( NATIVE_LAF, settings.getBoolean( UISettings.NATIVE_LAF ) );
			return values;
		}

	}
}
