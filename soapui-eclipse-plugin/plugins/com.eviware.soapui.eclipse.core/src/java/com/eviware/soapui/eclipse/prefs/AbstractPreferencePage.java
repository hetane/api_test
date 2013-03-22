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

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.actions.Prefs;
import com.eviware.soapui.eclipse.SoapUIActivator;
import com.eviware.soapui.eclipse.forms.FormBuilder;
import com.eviware.soapui.model.propertyexpansion.PropertyExpansionUtils;
import com.eviware.soapui.model.settings.Settings;

/**
 * 
 * @author Lars H
 */
public abstract class AbstractPreferencePage<T extends Prefs> extends PreferencePage implements IWorkbenchPreferencePage
{
   protected T prefs;
   
   protected FormBuilder form;

   public AbstractPreferencePage(T prefs)
   {
      this.prefs = prefs;
      setTitle( prefs.getTitle() );
   }

   public void init(IWorkbench workbench)
   {
   }
   
   protected T getPrefs()
   {
      return prefs;
   }
   
   protected Control createContents(Composite parent)
   {
      noDefaultAndApplyButton();
      
      Settings settings = SoapUI.getSettings();
      
      form = new FormBuilder(parent, getNumColumns(), prefs.getValues(settings)); 
      buildForm(form);
      return form.getPage();
   }
   
   // TODO Eclipse: Get rid of this. Use GridData.horizontalSpan instead.
   protected int getNumColumns() { return 3; }

   protected abstract void buildForm(FormBuilder form);
   
   public boolean performOk()
   {
      Settings settings = SoapUI.getSettings();
      
      prefs.storeValues(form.getFormValues(), settings);

      try
      {
         SoapUI.saveSettings();
         PropertyExpansionUtils.saveGlobalProperties();
      }
      catch(Exception e)
      {
         SoapUIActivator.logError("Could not save settings", e);
      }
      
      return true;
   }
}
