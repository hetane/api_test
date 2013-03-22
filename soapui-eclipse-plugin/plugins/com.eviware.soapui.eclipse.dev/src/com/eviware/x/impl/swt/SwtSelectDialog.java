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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * 
 * @author Lars H
 */
public class SwtSelectDialog extends Dialog
{
   private static final int COLUMNS = 40;
   private static final int ROWS = 10;

   private final String title;
   private final String message;
   private final Object[] objects;
   
   private int initialSelection = -1;
   
   private List list;

   private Object selection = null;

   public SwtSelectDialog(Shell parentShell, String message, String title, Object[] objects)
   {
      super(parentShell);
      this.title = title;
      this.message = message;
      this.objects = objects;
   }
   
   public void setInitialSelection(Object object)
   {
   	if( object == null )
   		this.initialSelection = -1;
   	else
   	{
	      for(int i = 0; i < objects.length; i++)
	      {
	         if(object.equals(objects[i]))
	         {
	            this.initialSelection = i;
	         }
	      }
   	}
   }
   
   public Object getSelection()
   {
      return selection;
   }

   @Override
   protected void configureShell(Shell newShell)
   {
      super.configureShell(newShell);
      newShell.setText(title);
   }

   @Override
   protected Control createDialogArea(Composite parent)
   {
      Composite dialogArea = (Composite) super.createDialogArea(parent);
      dialogArea.setLayout(new GridLayout());

      Label label = new Label(dialogArea, 0);
      label.setText(message);

      list = new List(dialogArea, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      list.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
            | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
            | GridData.VERTICAL_ALIGN_FILL));
      
      String[] strings = new String[objects.length];
      for(int i = 0; i < objects.length; i++)
      {
         strings[i] = objects[i].toString();
      }
      list.setItems(strings);
      
      if(initialSelection >= 0)
      {
         list.setSelection(initialSelection);
      }
      else if(strings.length >= 0)
      {
         list.setSelection(0);
      }

      return dialogArea;
   }

   /**
    * The user has pushed Ok.
    */
   @Override
   protected void okPressed()
   {
      int[] selections = list.getSelectionIndices();
      if(selections.length == 1)
      {
         int selectedIndex = selections[0];
         this.selection = objects[selectedIndex];
      }
      
      super.okPressed();
   }
}
