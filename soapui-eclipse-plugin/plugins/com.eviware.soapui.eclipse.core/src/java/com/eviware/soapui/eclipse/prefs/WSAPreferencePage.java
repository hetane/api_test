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

import com.eviware.soapui.actions.AnnotatedSettingsPrefs;
import com.eviware.soapui.actions.SoapUIPreferencesAction;
import com.eviware.soapui.settings.WsaSettings;

/**
 * 
 * @author Dain Nilsson
 */
public class WSAPreferencePage extends AnnotatedPreferencePage
{
   public WSAPreferencePage()
   {
      super(new AnnotatedSettingsPrefs( WsaSettings.class, SoapUIPreferencesAction.WSA_SETTINGS ) );
   }
}
