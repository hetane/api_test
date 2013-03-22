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

import java.awt.Dimension;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author Lars H
 */
public class AbstractDialog extends TitleAreaDialog
{
   private String title;
   private String message;
   private Dimension size;
   
   public AbstractDialog(String title)
   {
      this(SwtDialogs.getShell(), title);
   }
   
   public AbstractDialog(Shell shell, String title)
   {
      super(shell);
      this.title = title;
      setTitle( title );
   }

   public void setMessage(String newMessage)
   {
      this.message = newMessage;
   }

   public String getMessage()
   {
      return message;
   }
   
   public boolean isHelpAvailable()
   {
      return false;
   }
   
   public void setSize(Dimension size)
   {
      this.size = size;
   }
      
   @Override
   public void create()
   {
      super.create();
      setTitle(title);
      if(message != null)
      {
         // Make a change
         setMessage("", IMessageProvider.NONE);
         setMessage(message, IMessageProvider.NONE);
      }
   }

   @Override
   protected void configureShell(Shell newShell)
   {
      super.configureShell(newShell);
      newShell.setText(title);
   }

   @Override
   protected Point getInitialSize()
   {
      if(size != null)
         return new Point(size.width, size.height);
      else
         return super.getInitialSize();
   }
}
