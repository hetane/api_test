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

package com.eviware.soapui.eclipse.forms;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.eviware.soapui.support.types.StringToStringMap;

/**
 * 
 * @author Lars H
 */
public class FormBuilder
{
   private StringToStringMap values;
   private List<FormValue> rows = new ArrayList<FormValue>();
   
   private Composite page;
   
   public FormBuilder(Composite parent, StringToStringMap values)
   {
      this(parent, 3, values);
   }
   
   public FormBuilder(Composite parent, int numColumns, StringToStringMap values)
   {
      this.values = values;
      
      page = new Composite(parent, SWT.NULL);
      GridLayout layout = new GridLayout();
      layout.numColumns = numColumns;
      page.setLayout(layout);
      page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
   }
   
   public Composite getPage()
   {
      return page;
   }
   
   public StringToStringMap getFormValues()
   {
      for(FormValue dir : rows)
      {
         String key = dir.getName();
         String value = dir.getValue();
         values.put(key, value);
      }
      
      return values;
   }
   

   
   public void appendDirectory(String name, String tooltip)
   {
      String value = values.get(name);
      rows.add(new DirectoryValue(page, name, value, tooltip));
   }
   
   public void appendFile(String name, String extension, String fileType, String tooltip)
   {
      String value = values.get(name);
      rows.add(new FileValue(page, name, value, extension, fileType, tooltip));
   }
   
   public void appendCheckBox(String caption, String label, boolean selected)
   {
      String value = values.get(caption);
      rows.add(new CheckBoxValue(page, caption, label, Boolean.valueOf( value )));
   }

   public void appendComboBox(String caption, String[] list, String label)
   {
      String value = values.get(caption);
      rows.add(new ComboBoxValue(page, caption, list, label, value));
   }

   public void appendTextField(String label, String tooltip)
   {
      String value = values.get(label);
      rows.add(new TextValue(page, label, tooltip, value));
   }
	
	public void appendStringList(String label, String tooltip) {
		String value = values.get(label);
		rows.add(new StringListValue(page, label, tooltip, value));
	}
	
	public void appendAwt(JComponent component, GridData layout) {
		rows.add(new AwtValue(page, component, layout));
	}
	
	public void appendFont(String label) {
		String value = values.get(label);
		rows.add(new FontValue(page, label, value));
	}

}
