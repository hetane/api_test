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

/**
 * 
 * @author Lars H
 */
public class UIPreferencePage extends AbstractPreferencePage<UIPrefs>
{
   public UIPreferencePage()
   {
      super( new UIPrefs( SoapUIPreferencesAction.UI_SETTINGS ) );
   }
   
   protected void buildForm(FormBuilder editorForm)
   {
      editorForm.appendCheckBox( UIPrefs.CLOSE_PROJECTS, "(close all projects on startup)", false );
	    
      editorForm.appendCheckBox( UIPrefs.ORDER_PROJECTS, "(orders projects alphabetically in tree)", false );
		editorForm.appendCheckBox( UIPrefs.ORDER_REQUESTS, "(orders requests alphabetically in tree)", false );
//		editorForm.appendCheckBox( UIPrefs.ORDER_TESTSUITES, "(orders TestSuites alphabetically in tree)", false );
		editorForm.appendCheckBox( UIPrefs.SHOW_DESCRIPTIONS, "(show description content when available)", false );
        
		editorForm.appendCheckBox( UIPrefs.AUTOSAVE_ONEXIT, "(automatically save all projects on exit)", true );
		editorForm.appendCheckBox( UIPrefs.CREATE_BACKUP, "(backup project files before they are saved)", true );
		editorForm.appendDirectory( UIPrefs.BACKUP_FOLDER, "(folder to backup to, can be both relative or absolute)");
		editorForm.appendTextField( UIPrefs.AUTOSAVE_INTERVAL, "Sets the autosave interval in minutes, 0 = off" );
		
		editorForm.appendCheckBox( UIPrefs.ENABLE_GROOVY_LOG_DURING_LOADTEST, "(do not disable the groovy log when running LoadTests)", true );
   }
}
