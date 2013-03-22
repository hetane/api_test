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

import org.eclipse.swt.widgets.Control;

import com.eviware.x.form.AbstractXFormField;

/**
 * 
 * @author Lars H
 */
public abstract class AbstractSwtXFormField<T extends Control> extends AbstractXFormField<T>
{
	private boolean enabled;
   public AbstractSwtXFormField()
   {
   }
      
   public void setToolTip(String tooltip)
   {
      getComponent().setToolTipText( tooltip );
   }

   public boolean isEnabled()
   {
	   if(!getComponent().isDisposed()) enabled = getComponent().isEnabled();
	   return enabled;
   }

   public void setEnabled(boolean enabled)
   {
	   this.enabled = enabled;
	   if(!getComponent().isDisposed()) getComponent().setEnabled( this.enabled );  
   }

   public void setProperty(String name, Object value)
   {
      if( name.equals("dimension"))
      {
         Control component = getComponent();
         Dimension size = (Dimension) value;
         component.setSize( size.width, size.height );
      }
   }
}
