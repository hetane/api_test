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

package com.eviware.soapui.impl.wsdl.actions.iface.tools.support;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

import com.eviware.soapui.support.UISupport;

public class SwtToolHost implements ToolHost
{
	public void run(ToolRunner runner) throws Exception
	{
		RunnerConsole console = null;
		
		try
		{
			console = new RunnerConsole( runner.getName() );
			IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
			consoleManager.addConsoles(new IConsole[] { console } );
			consoleManager.showConsoleView(console);
			console.log("Running " + runner.getName() + " for [" + runner.getModelItem().getName() + "]\r\n");

			runner.setContext( console );
			Thread thread = new Thread( runner );
			thread.start();
		}
		catch (Exception ex)
		{
			UISupport.showErrorMessage( ex );
			throw ex;
		}
	}
}
