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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;

import com.eviware.soapui.support.action.swing.ActionList;
import com.eviware.soapui.support.action.swing.DefaultActionList;

public class SwtTabbledFormDialogTest extends TestCase
{
   public static void main(String[] args)
   {
      Display display = new Display();
      SwtTabbedFormDialog dialog = new SwtTabbedFormDialog("SwtTabbedFormDialog");
      ActionList actionList = new DefaultActionList("DefaultActionList");
      SwtXFormDialogBuilder dialogBuilder = new SwtXFormDialogBuilder("SwtXFormDialogBuilder");
      dialog.setActions(dialogBuilder.buildOkCancelActions());
      dialog.addAction(new AbstractAction("AbstractAction")
      {
         public void actionPerformed(ActionEvent e)
         {
            System.out.println("AbstractAction");
         }
      });
      dialog.show();
      display.dispose();
   }
}
