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

package com.eviware.soapui.eclipse.util;

import com.eviware.soapui.support.UIUtils;
import com.eviware.x.impl.swt.SwtDialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javax.swing.JComponent;

import java.awt.Frame;

public class SwtUtils implements UIUtils
{
   public static final SwtUtils INSTANCE = new SwtUtils();
   
	public void invokeLater(Runnable runnable)
	{
	   SwtDialogs.getDisplay().asyncExec(runnable);	
	}

	public void invokeAndWait( Runnable runnable ) throws Exception
	{
	   SwtDialogs.getDisplay().syncExec( runnable );
	}
	
	public void runInUIThreadIfSWT( Runnable runnable )
	{
	   SwtDialogs.runInUIThread( runnable );
	}
	
	public void dispatchUIEvents()
	{
	   System.out.println("SwtUtils.dispatchUIEvents");
	   if(Display.getCurrent() != null)
	   SwtDialogs.getDisplay().readAndDispatch();
	}
   
   /**
    * Bridge to show AWT/Swing components in SWT.
    * @param parent
    * @param component
    * @return
    */
   public static Frame createAwtFrame( Composite parent, JComponent component )
   {
      return createAwtFrame(parent, component, null);
   }
   
   public static Frame createAwtFrame( Composite parent, JComponent component, Object layoutData )
   {
      AwtFrame awtFrame = new AwtFrame(parent, component, layoutData);
      return awtFrame.frame;
   }
}
