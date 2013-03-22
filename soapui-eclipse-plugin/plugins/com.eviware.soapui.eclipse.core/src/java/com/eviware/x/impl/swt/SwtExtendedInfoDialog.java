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

package com.eviware.x.impl.swt;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author lars
 */
public class SwtExtendedInfoDialog extends AbstractDialog
{
   private String content;
   private ArrayList<Integer> buttons = new ArrayList<Integer>();
   private ArrayList<String> labels = new ArrayList<String>();
   
   private Boolean extendedInfoResult = null;
   
   public SwtExtendedInfoDialog(Shell parentShell, String title)
   {
      super(parentShell, title);
   }
   
   public void setContent(final String content)
   {
      this.content = content;
   }
      
   public void addButton(int buttonId, String label)
   {
      buttons.add(buttonId);
      labels.add(label);
   }
   
   /**
    * 
    * @return true if the user pushed Yes or OK, false if the user pushed No, null if the user pushed Cancel.
    */
   public Boolean show()
   {
      open();
      return extendedInfoResult;
   }

   @Override
   protected Control createDialogArea(Composite parent)
   {
      Composite composite = (Composite) super.createDialogArea(parent);
      
      Browser browser = new Browser(composite, SWT.NONE);
      browser.setText(content);
      browser.setLayoutData( new GridData(GridData.FILL_BOTH) );
      
      return composite;
   }
   
   @Override
   protected void createButtonsForButtonBar(Composite parent)
   {
      for(int i = 0; i < buttons.size(); i++)
      {
         int buttonId = buttons.get(i);
         String label = labels.get(i);
         boolean isDefault = (buttonId == IDialogConstants.OK_ID || buttonId == IDialogConstants.YES_ID);
         createButton(parent, buttonId, label, isDefault);
      }
   }
   
   @Override
   protected void buttonPressed(int buttonId)
   {
      extendedInfoResult = buttonResult(buttonId);
      close();
   }
   
   private Boolean buttonResult(int buttonId)
   {
      switch(buttonId)
      {
      case IDialogConstants.OK_ID:
         return true;
      case IDialogConstants.YES_ID:
         return true;
      case IDialogConstants.NO_ID:
         return false;
      case IDialogConstants.CANCEL_ID:
         return null;
      default:
         throw new IllegalArgumentException("Button id " + buttonId + " not implemented");
      }
   }
}
