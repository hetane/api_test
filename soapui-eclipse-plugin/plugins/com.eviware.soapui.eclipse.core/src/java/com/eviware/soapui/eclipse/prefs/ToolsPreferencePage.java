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
import com.eviware.soapui.actions.ToolsPrefs;
import com.eviware.soapui.eclipse.forms.FormBuilder;

/**
 * TODO Eclipse: Share settings with the Swing application.
 * 
 * @author Lars H
 */
public class ToolsPreferencePage extends AbstractPreferencePage<ToolsPrefs>
{
	private String[][] tools;
	
   public ToolsPreferencePage()
   {
      super( new ToolsPrefs( SoapUIPreferencesAction.INTEGRATED_TOOLS) );
      tools = getPrefs().getEclipseTools();
   }
   
   protected void buildForm(FormBuilder form)
   {
      for(String[] tool : tools)
      {
         form.appendDirectory(tool[0], null);
      }
   }
}
