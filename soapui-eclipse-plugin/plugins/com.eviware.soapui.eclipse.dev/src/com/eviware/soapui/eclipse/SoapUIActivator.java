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

package com.eviware.soapui.eclipse;

import java.awt.Color;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.UIManager;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.syntax.jedit.InputHandler;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.SoapUICore;
import com.eviware.soapui.SwingSoapUICore;
import com.eviware.soapui.StandaloneSoapUICore.SoapUITheme;
import com.eviware.soapui.eclipse.util.EclipseToolLocator;
import com.eviware.soapui.eclipse.util.SwtUtils;
import com.eviware.soapui.impl.WorkspaceImpl;
import com.eviware.soapui.impl.actions.NewWorkspaceAction;
import com.eviware.soapui.impl.actions.RenameWorkspaceAction;
import com.eviware.soapui.impl.actions.SwitchWorkspaceAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.support.SwingToolHost;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.support.SwtToolHost;
import com.eviware.soapui.integration.impl.CajoServer;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.model.workspace.WorkspaceFactory;
import com.eviware.soapui.monitor.TestMonitor;
import com.eviware.soapui.settings.ToolsSupport;
import com.eviware.soapui.settings.UISettings;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.SoapUIActionRegistry;
import com.eviware.soapui.support.log.LogDisablingTestMonitorListener;
import com.eviware.soapui.support.log.TabbedLog4JMonitor;
import com.eviware.soapui.ui.desktop.DesktopRegistry;
import com.eviware.soapui.ui.desktop.standalone.StandaloneDesktopFactory;
import com.eviware.x.form.XFormFactory;
import com.eviware.x.impl.swt.SwtDialogs;
import com.eviware.x.impl.swt.SwtFileDialogs;
import com.eviware.x.impl.swt.SwtFormFactory;
import com.eviware.x.impl.swt.support.AwtEnvironment;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Lars H
 */
public class SoapUIActivator extends AbstractUIPlugin
{
	private static final String EXTRAS_EXTENSION_POINT = "com.eviware.soapui.eclipse.extras";

	public static String PLUGIN_ID = "com.eviware.soapui.eclipse.core";

	// The shared instance.
	private static volatile SoapUIActivator plugin;

	private static volatile WorkspaceImpl workspace;
	private static volatile boolean disableSoapUI = false;
	private static volatile SwingSoapUICore soapUICore;
	private static volatile EclipseSoapUIDesktop desktop;

	/**
	 * The constructor.
	 */

	public SoapUIActivator() throws Exception
	{
		plugin = this;
	}

	public static void initSoapUI() throws Exception
	{
		if( soapUICore != null )
			return;

		// Without this, SWT hangs if SwingSoapUICore tries to open a JOptionPane
		initSwtUI();

		soapUICore = new SwingSoapUICore( System.getProperty( "user.home" ), SoapUICore.DEFAULT_SETTINGS_FILE )
		{
			@Override
			protected String importSettingsOnStartup( String fileName ) throws Exception
			{
				// dont show the popup..
				return fileName;
			}
		};

		AwtEnvironment.getInstance( Display.getCurrent() );

		try
		{
			// The META key on Mac and Linux don't work in Eclipse.
			// Use CTRL instead, even though it's not correct.
			InputHandler.useCtrlKeyInsteadOfMenuKey( true );

			if( SoapUI.getSettings().getBoolean( UISettings.NATIVE_LAF ) )
			{
				UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
				UIManager.put( "TabbedPane.tabAreaInsets", new Insets( 3, 2, 0, 0 ) );
				UIManager.put( "TabbedPane.unselectedBackground", new Color( 220, 220, 220 ) );
				UIManager.put( "TabbedPane.selected", new Color( 240, 240, 240 ) );
			}
			else
			{
				SoapUITheme theme = new SoapUITheme();

				PlasticXPLookAndFeel.setCurrentTheme( theme );
				PlasticXPLookAndFeel.setTabStyle( "Metal" );

				UIManager.setLookAndFeel( new PlasticXPLookAndFeel() );
				UIManager.put( "TabbedPane.tabAreaInsets", new Insets( 3, 2, 0, 0 ) );
				UIManager.put( "TabbedPane.unselectedBackground", new Color( 220, 220, 220 ) );
				UIManager.put( "TabbedPane.selected", new Color( 240, 240, 240 ) );

				PlasticXPLookAndFeel.setPlasticTheme( theme );
			}
		}
		catch( Throwable e )
		{
			System.err.println( "Error initializing look and feel; " + e.getMessage() );
		}

		// soapUICore.prepareUI();
		SoapUI.setSoapUICore( soapUICore );

		initLogging();
		updateActions();

		SoapUI.initLogMonitor( true, "soapui", new TabbedLog4JMonitor() );

		soapUICore.afterStartup( workspace );

		ToolsSupport.setToolLocator( new EclipseToolLocator() );

		initSoapUIPro();
		
		CajoServer.getInstance().start();

	}

	static void updateActions()
	{
		SoapUIActionRegistry actionRegistry = SoapUI.getActionRegistry();
		String[] actionsToRemove = { NewWorkspaceAction.SOAPUI_ACTION_ID, SwitchWorkspaceAction.SOAPUI_ACTION_ID,
				RenameWorkspaceAction.SOAPUI_ACTION_ID };
		for( String actionId : actionsToRemove )
		{
			actionRegistry.removeAction( actionId );
		}
		actionRegistry.addConfig( SoapUIActivator.class
				.getResourceAsStream( "/com/eviware/soapui/eclipse/resources/conf/eclipse-soapui-actions.xml" ),
				SoapUI.class.getClassLoader() );
	}

	private static void initSwtUI()
	{
		UISupport.setUIUtils( new SwtUtils() );
		UISupport.setDialogs( new SwtDialogs() );
		UISupport.setFileDialogs( new SwtFileDialogs() );
		UISupport.setToolHost( new SwingToolHost() );

		// TODO Eclipse: SwingFormFactory is more complete than SwtFormFactory.
		XFormFactory.Factory.instance = new SwtFormFactory();

		UISupport.setToolHost( new SwtToolHost() );

		DesktopRegistry.getInstance().addDesktop( SoapUI.DEFAULT_DESKTOP, new StandaloneDesktopFactory() );
	}

	/**
	 * See if soapUI Pro is installed and initialize if it is.
	 */
	private static void initSoapUIPro() throws Exception
	{
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint( EXTRAS_EXTENSION_POINT );
		for( IExtension extension : extensionPoint.getExtensions() )
		{
			for( IConfigurationElement configElement : extension.getConfigurationElements() )
			{
				try
				{
					ISoapUIExtra extra = ( ISoapUIExtra )configElement.createExecutableExtension( "class" );
					extra.init();
				}
				catch( Exception e )
				{
					System.err.println( "Error initializing " + configElement.getAttribute( "class" ) );
					e.printStackTrace();
					logError( "Error initializing soapUI", e );

					// Disable soapUI if Pro *is* installed but no license is
					// present.
					throw e;
				}
			}
		}
	}

	public static Workspace getWorkspace()
	{
		if( workspace == null && !disableSoapUI )
		{
			try
			{
				initSoapUI();
			}
			catch( Exception e )
			{
				disableSoapUI = true;
				return null;
			}

			try
			{
				IRunnableWithProgress runnable = new IRunnableWithProgress()
				{
					public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
					{
						try
						{
							IWorkspace eclipseWorkspace = ResourcesPlugin.getWorkspace();
							IWorkspaceRoot workspaceRoot = eclipseWorkspace.getRoot();
							String home = workspaceRoot.getLocation().toOSString();
							String workspaceFile = home + File.separatorChar + SoapUI.DEFAULT_WORKSPACE_FILE;
							logInfo( "Reading workspace: " + workspaceFile );

							monitor.beginTask( "Initializing Web Services Plugin", IProgressMonitor.UNKNOWN );
							monitor.subTask( "Reading workspace: " + workspaceFile );

							WorkspaceFactory factory = WorkspaceFactory.getInstance();
							workspace = ( WorkspaceImpl )factory.openWorkspace( workspaceFile, null );
						}
						catch( Exception e )
						{
							e.printStackTrace();
							throw new InterruptedException( e.getMessage() );
						}
					}
				};

				ProgressMonitorDialog pmd = new ProgressMonitorDialog( new Shell() );
				pmd.run( false, true, runnable );
			}
			catch( Exception e )
			{
				e.printStackTrace();
				logError( "Read workspace failed", e );
				throw new RuntimeException( e );
			}

			desktop = new EclipseSoapUIDesktop( workspace );
			SoapUI.setDesktop( desktop );
			TestMonitor testMonitor = SoapUI.getTestMonitor();
			testMonitor.addTestMonitorListener( new LogDisablingTestMonitorListener() );
			testMonitor.init( workspace );
			SoapUI.setWorkspace( workspace );
		}

		return workspace;
	}

	public static void logError( String message, Throwable e )
	{
		if( plugin != null )
		{
			IStatus status = new Status( IStatus.ERROR, PLUGIN_ID, 1, message, e );
			plugin.getLog().log( status );
		}
	}

	public static void logInfo( String message )
	{
		if( plugin != null )
		{
			IStatus status = new Status( IStatus.INFO, PLUGIN_ID, 0, message, null );
			plugin.getLog().log( status );
		}
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start( BundleContext context ) throws Exception
	{
		super.start( context );

		System.setProperty( "soapui.jxbrowser.disable", "true" );

		// initSoapUI();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop( BundleContext context ) throws Exception
	{
		if( workspace != null )
		{
			logInfo( "Saving workspace: " + workspace.getName() + " in " + workspace.getPath() );
			workspace.save( false );
		}

		// workspace = null;

		try
		{
			Images.dispose();
		}
		catch( Exception e )
		{
		}

		plugin = null;
		super.stop( context );
	}

	@Override
	public ImageRegistry getImageRegistry()
	{
		return null;
	}

	/**
	 * Returns the shared instance. Use with caution, it seems this may return
	 * null.
	 * 
	 * @return the shared instance.
	 */
	public static SoapUIActivator getDefault()
	{
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *           the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor( String path )
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin( "soapui", path );
	}

	/**
	 * Initialize Log4J. Configure log levels in log4j.properties that is located
	 * in this class' source directory.
	 * 
	 * @throws IOException
	 */

	private static void initLogging()
	{
		// Configure log levels with a property file.
		try
		{
			Logger soapuiLogger = Logger.getLogger( "com.eviware.soapui" );
			soapuiLogger.setLevel( Level.DEBUG );

			String logFile = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/soapui.log";
			boolean append = false;
			boolean bufferedIO = false; // If true nothing gets logged.
			Appender appender = new FileAppender( new PatternLayout( "%d{ABSOLUTE} %p [%c] %m%n" ), logFile, append,
					bufferedIO, 2000000 );
			appender.setName( "file" );
			soapuiLogger.addAppender( appender );

			// For use when launching from Eclipse.
			ConsoleAppender console = new ConsoleAppender( new PatternLayout( "%p [%c{2}] %m%n" ) );
			console.setName( "console" );
			soapuiLogger.addAppender( console );

			// PropertyConfigurator.configure( plugin.getClass().getResource(
			// "log4j.properties" ) );
		}
		catch( Throwable t )
		{
			t.printStackTrace();
		}
	}
}
