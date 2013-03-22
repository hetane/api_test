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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.eviware.soapui.support.UIUtils;
import com.eviware.x.impl.swt.SwtDialogs;
import com.eviware.x.impl.swt.support.EmbeddedSwingComposite;

public class SwtUtils implements UIUtils
{
	public static final SwtUtils INSTANCE = new SwtUtils();

	public void invokeLater( Runnable runnable )
	{
		Display.getDefault().asyncExec( runnable );
	}

	public void invokeAndWait( Runnable runnable ) throws Exception
	{
		Display.getDefault().syncExec( runnable );
	}

	public void runInUIThreadIfSWT( Runnable runnable )
	{
		SwtDialogs.runInUIThread( runnable );
	}

	/**
	 * Bridge to show AWT/Swing components in SWT.
	 * 
	 * @param parent
	 * @param component
	 * @return
	 */
	public static Frame createAwtFrame( Composite parent, JComponent component )
	{
		return createAwtFrame( parent, component, null );
	}

	public static Frame createAwtFrame( Composite parent, final JComponent component, Object layoutData )
	{
		// Workaround from https://bugs.eclipse.org/bugs/show_bug.cgi?id=208968
		if( "sun.awt.motif.MToolkit".equals( Toolkit.getDefaultToolkit().getClass().getName() ) )
		{
			SWT_AWT.embeddedFrameClass = "sun.awt.motif.MEmbeddedFrame";
		}

		/*
		 * Composite swtAwtComponent = new Composite( parent, SWT.EMBEDDED ); if(
		 * layoutData != null ) swtAwtComponent.setLayoutData( layoutData );
		 */
		// final Frame frame = SWT_AWT.new_Frame( swtAwtComponent );

		EmbeddedSwingComposite esc = new EmbeddedSwingComposite( parent, SWT.EMBEDDED )
		{
			@Override
			protected JComponent createSwingComponent()
			{
				return component;
			}
		};
		if( layoutData != null )
			esc.setLayoutData( layoutData );
		esc.populate();

		/*
		 * // Use an opaque JPanel to paint the whole area (for Tabbed panel)
		 * JPanel innerPanel = new JPanel( new BorderLayout() );
		 * innerPanel.setOpaque( true ); innerPanel.add( component,
		 * BorderLayout.CENTER );
		 * 
		 * // Use a heavyweight component to get mouse events. See the SWT_AWT //
		 * documentation. Panel panel = new Panel( new BorderLayout() );
		 * panel.add( innerPanel, BorderLayout.CENTER );
		 * 
		 * frame.add( panel );
		 * 
		 * swtAwtComponent.addDisposeListener( new DisposeListener() { public void
		 * widgetDisposed( DisposeEvent e ) { frame.dispose(); } } );
		 */

		return esc.getFrame();
	}
}
