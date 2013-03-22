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

import java.util.List;

import com.eviware.soapui.actions.AnnotatedSettingsPrefs;
import com.eviware.soapui.eclipse.forms.FormBuilder;
import com.eviware.soapui.settings.Setting;

public abstract class AnnotatedPreferencePage extends AbstractPreferencePage<AnnotatedSettingsPrefs>
{
   public AnnotatedPreferencePage(AnnotatedSettingsPrefs prefs)
   {
      super(prefs);
   }
   
   protected void buildForm(FormBuilder form)
   {
      AnnotatedSettingsPrefs annotatedPrefs = (AnnotatedSettingsPrefs) getPrefs();
      List<Setting> settings = annotatedPrefs.getSettings();
      for( Setting annotation : settings )
      {
         switch( annotation.type() )
         {
            case BOOLEAN : 
            {
               form.appendCheckBox( annotation.name(), annotation.description(), false );
               break;
            }
            case FOLDER : 
            {
               form.appendDirectory( annotation.name(), annotation.description() );
               break;
            }
            case FILE :
            {
            	form.appendFile(annotation.name(), null, null, annotation.description());
            	break;
            }
            case ENUMERATION :
            {
               form.appendComboBox( annotation.name(), annotation.values(), annotation.description() );
               break;
            }
            case STRINGLIST:
            	form.appendStringList(annotation.name(), annotation.description());
            	break;
            default : 
            {
               form.appendTextField( annotation.name(), annotation.description() );
               break;
            }
         }
      }
   }
}
