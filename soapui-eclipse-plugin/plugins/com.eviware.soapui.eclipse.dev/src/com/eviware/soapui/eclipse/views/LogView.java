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

package com.eviware.soapui.eclipse.views;

import java.awt.EventQueue;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.eclipse.SoapUIActivator;
import com.eviware.soapui.eclipse.util.SwtUtils;
import com.eviware.soapui.support.log.Log4JMonitor;

public class LogView extends ViewPart
{
	private java.awt.Frame frame;
	private Log4JMonitor logMonitor;

	public LogView()
	{
	}

	public void createPartControl( Composite parent )
	{
		SoapUIActivator.getWorkspace();
		logMonitor = SoapUI.getLogMonitor();
		frame = SwtUtils.createAwtFrame( parent, logMonitor.getComponent() );
	}

	public void dispose()
	{
		super.dispose();

		EventQueue.invokeLater( new Runnable()
		{
			public void run()
			{
				frame.dispose();
			}
		} );
	}

	public void setFocus()
	{
		logMonitor.getComponent().requestFocus();
	}
}
