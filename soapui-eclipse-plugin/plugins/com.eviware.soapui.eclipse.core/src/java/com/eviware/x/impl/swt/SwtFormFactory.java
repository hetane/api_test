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

import org.eclipse.swt.widgets.Display;

import com.eviware.x.form.XFormDialogBuilder;
import com.eviware.x.form.XFormFactory;
import com.eviware.x.impl.swing.SwingXFormDialogBuilder;

/**
 * 
 * @author lars
 */
public class SwtFormFactory extends XFormFactory
{
   public XFormDialogBuilder createDialogBuilder2( String name )
   {
      // Avoid Invalid thread access
      if( Display.getCurrent() == null )
         return new SwingXFormDialogBuilder( name );
      else
         return new SwtXFormDialogBuilder(name);
   }
}
