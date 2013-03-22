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

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.eclipse.SoapUIActivator;
import com.eviware.soapui.eclipse.util.AwtFrame;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.PanelBuilder;
import com.eviware.soapui.model.tree.SoapUITreeNode;
import com.eviware.soapui.model.util.PanelBuilderRegistry;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.ui.Navigator;
import com.eviware.soapui.ui.NavigatorListener;
import com.eviware.soapui.ui.desktop.DesktopPanel;
import com.eviware.soapui.ui.support.DesktopListenerAdapter;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;

public class NavigatorView extends ViewPart
{
	private JPanel overviewPanel;
	private Navigator navigator;
	private Frame frame;

	public NavigatorView()
	{
	}

	@Override
	public void createPartControl( Composite parent )
	{
	   System.out.println("NavigatorView");
	   final CardLayout layout = new CardLayout();
	   final JPanel cards = new JPanel(layout);
	   final JLabel loadingLabel = new JLabel("Loading...");
	   cards.add(loadingLabel, "loading");
	   layout.show(cards, "loading");
	   AwtFrame awtFrame = new AwtFrame(parent, cards, null);
	   frame = awtFrame.frame;
	   
	   new Thread(new Runnable()
	   {
	      public void run()
	      {
	         System.out.println("NavigatorView: buildUI");
	         JComponent component = buildUI();
	         System.out.println("NavigatorView: show navigator");
	         cards.add(component, "navigator");
	         layout.show(cards, "navigator");
	      }
	   }).start();
	}

	@Override
	public void setFocus()
	{
      if(navigator == null)
         return;
      
		Display display = Display.getCurrent();
		if( display == null )
			navigator.getMainTree().requestFocus();
		// strange hack for java 1.6
		else
			display.asyncExec( new Runnable() {
   			public void run()
   			{
   				navigator.getMainTree().requestFocus();
   			}
         } );
	}

	private JComponent buildMainPanel()
	{
		JSplitPane splitPane = UISupport.createVerticalSplit();

		splitPane.setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );

		splitPane.setTopComponent( navigator );
		splitPane.setBottomComponent( buildOverviewPanel() );
		splitPane.setDividerLocation( 0.7 );
		splitPane.setResizeWeight( 0.7 );
		return splitPane;
	}

	private Component buildOverviewPanel()
	{
		overviewPanel = new JPanel( new BorderLayout() );
		return overviewPanel;
	}

	private JComponent buildUI()
	{
		try
		{
			Workspace workspace = SoapUIActivator.getWorkspace();
         
			navigator = new Navigator( workspace );
			navigator.addNavigatorListener( new InternalNavigatorListener() );

			SoapUI.setNavigator( navigator );
			
			SoapUI.getDesktop().addDesktopListener( new DesktopListenerAdapter() {
	         public void desktopPanelSelected( DesktopPanel desktopPanel ) {
	             ModelItem modelItem = desktopPanel.getModelItem();
	             if( modelItem != null ) {
	                 navigator.selectModelItem( modelItem );
	             }
	         }
			} );
      }
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return buildMainPanel();
	}
	
	private void setOverviewPanel( Component panel )
	{
		if( overviewPanel.getComponentCount() == 0 && panel == null )
		{
			return;
		}

		overviewPanel.removeAll();
		if( panel != null )
		{
			overviewPanel.add( panel, BorderLayout.CENTER );
		}
		overviewPanel.revalidate();
		overviewPanel.repaint();
	}

	public void dispose()
	{
		super.dispose();
		
      if(frame != null) {
   		EventQueue.invokeLater(new Runnable () {
   			public void run () {
   				frame.dispose ();
   			} } );
      }
	}
	
	public class InternalNavigatorListener implements NavigatorListener
	{
		public void nodeSelected( SoapUITreeNode treeNode )
		{
			if( treeNode != null )
			{
				PanelBuilder builder = PanelBuilderRegistry.getPanelBuilder( treeNode.getModelItem() );
				if( builder != null && builder.hasOverviewPanel() )
				{
					setOverviewPanel( builder.buildOverviewPanel( treeNode.getModelItem() ) );
					return;
				}
			}

			setOverviewPanel( null );
		}
	}
}
