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

import java.io.IOException;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.TextConsolePage;
import org.eclipse.ui.console.actions.CloseConsoleAction;
import org.eclipse.ui.part.IPageBookViewPage;

/**
 * 
 * @author Lars H
 */
public class RunnerConsole extends IOConsole implements RunnerContext
{
	private RunnerStatus status;

	private IOConsoleOutputStream standardOut;
	private IOConsoleOutputStream errorOut;
	private Color errorColor;

	public RunnerConsole( String title )
	{
		super( title, null );
		standardOut = newOutputStream();
		errorOut = newOutputStream();
	}

	public IPageBookViewPage createPage( IConsoleView view )
	{
		if( errorColor == null )
		{
			Display display = view.getViewSite().getShell().getDisplay();
			errorColor = new Color( display, 255, 0, 0 );
			errorOut.setColor( errorColor );
		}

		return new TextConsolePage( this, view )
		{
			protected void configureToolBar( IToolBarManager mgr )
			{
				super.configureToolBar( mgr );
				mgr.appendToGroup( IConsoleConstants.LAUNCH_GROUP, new CloseConsoleAction( RunnerConsole.this ) );
			}
		};
	}

	public void setStatus( RunnerStatus status )
	{
		this.status = status;
	}

	public void disposeContext()
	{
		if( status == RunnerStatus.FINISHED || status == RunnerStatus.ERROR )
		{
			try
			{
				standardOut.close();
				errorOut.close();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void dispose()
	{
		if( errorColor != null )
		{
			errorColor.dispose();
			errorColor = null;
		}
		super.dispose();
	}

	public RunnerStatus getStatus()
	{
		return status;
	}

	public String getTitle()
	{
		return getName();
	}

	public void log( String msg )
	{
		try
		{
			standardOut.write( msg );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	public void logError( String msg )
	{
		try
		{
			errorOut.write( msg );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
}
