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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * 
 * @author Lars H
 */
public class TextValue implements FormValue
{
   private Label label;
   private Text textField;
   
   public TextValue(Composite parent, String title, String tooltip, String value)
   {
      label = new Label(parent, SWT.LEFT);
      label.setText(title);
      label.setToolTipText(tooltip);  // TODO Eclipse: Tooltip does not work.
      label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
    
      textField = new Text(parent, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
      if(value != null)
         textField.setText(value);
      //textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      GridData layout = new GridData(GridData.FILL_HORIZONTAL);
      layout.horizontalSpan = 2;
      textField.setLayoutData(layout);
      //textField.setLayoutData(new GridData(
       //     GridData.FILL_HORIZONTAL, GridData.VERTICAL_ALIGN_BEGINNING, true, false, 2, 1));
   }

   /* (non-Javadoc)
    * @see com.eviware.soapui.eclipse.preferences.NameValue#getName()
    */
   public String getName()
   {
      return label.getText();
   }
   
   /* (non-Javadoc)
    * @see com.eviware.soapui.eclipse.preferences.NameValue#getValue()
    */
   public String getValue()
   {
      return textField.getText();
   }
}
