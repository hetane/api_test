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

package com.eviware.soapui.eclipse.swt_awt;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.eclipse.SoapUIActivator;
import com.eviware.soapui.eclipse.browser.SoapUILabelProvider;
import com.eviware.soapui.eclipse.editors.DesktopPanelEditorInput;
import com.eviware.soapui.eclipse.project.SoapuiAdapterFactory;
import com.eviware.soapui.eclipse.util.SwtUtils;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.PanelBuilder;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.util.PanelBuilderRegistry;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.ui.Navigator;
import com.eviware.soapui.ui.desktop.DesktopPanel;
import com.eviware.soapui.ui.desktop.SoapUIDesktop;

/**
 * Use Swing component in Eclipse.
 * 
 * @author Lars H
 */
public class SwtAwtRequestEditor extends EditorPart implements PropertyChangeListener
{
	private DesktopPanel desktopPanel;
	private java.awt.Frame frame;

	@Override
	public void createPartControl( Composite parent )
	{
		JComponent component = desktopPanel.getComponent();
		frame = SwtUtils.createAwtFrame( parent, component );
	}

	public DesktopPanel getDesktopPanel()
	{
		return desktopPanel;
	}

	@Override
	public void init( IEditorSite site, IEditorInput input ) throws PartInitException
	{
		DesktopPanelEditorInput input2;
		if( input instanceof FileEditorInput )
		{
			Project project = SoapuiAdapterFactory.findSoapuiProject( ( ( FileEditorInput )input ).getFile() );
			if( project == null )
				throw new PartInitException( "Cannot find soapUI project." );
			ModelItem modelItem = project.getModelItem();
			if( SoapUI.getNavigator() == null )
			{
				Workspace workspace = SoapUIActivator.getWorkspace();
				Navigator navigator = new Navigator( workspace );
				SoapUI.setNavigator( navigator );
			}
			SoapUIDesktop desktop = SoapUI.getDesktop();
			DesktopPanel desktopPanel = desktop.getDesktopPanel( modelItem );
			if( desktopPanel == null )
			{
				PanelBuilder<ModelItem> panelBuilder = PanelBuilderRegistry.getPanelBuilder( modelItem );
				desktopPanel = panelBuilder.buildDesktopPanel( modelItem );
			}
			input2 = new DesktopPanelEditorInput( desktopPanel );
		}
		else if( input instanceof DesktopPanelEditorInput )
		{
			input2 = ( DesktopPanelEditorInput )input;
		}
		else
			throw new PartInitException( "Unsupported IEditorInput." );

		this.desktopPanel = input2.getDesktopPanel();
		setSite( site );
		setInput( input );

		ModelItem modelItem = desktopPanel.getModelItem();
		if( modelItem != null )
		{
			setPartName( modelItem.getName() );

			// TODO: There should be a better way of accessing the LabelProvider...
			setTitleImage( SoapUILabelProvider.getImageForModelItem( modelItem ) );
			modelItem.addPropertyChangeListener( ModelItem.ICON_PROPERTY, this );
		}
		else
		{
			setPartName( desktopPanel.getTitle() );
		}
	}

	@Override
	public boolean isDirty()
	{
		return false;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public void setFocus()
	{
		desktopPanel.getComponent().requestFocus();
	}

	public void dispose()
	{
		super.dispose();

		desktopPanel.getModelItem().removePropertyChangeListener( ModelItem.ICON_PROPERTY, this );

		EventQueue.invokeLater( new Runnable()
		{
			public void run()
			{
				frame.dispose();
			}
		} );
	}

	@Override
	public void doSave( IProgressMonitor monitor )
	{
	}

	@Override
	public void doSaveAs()
	{
	}

	public void propertyChange( PropertyChangeEvent evt )
	{
		SwtUtils.INSTANCE.runInUIThreadIfSWT( new Runnable()
		{
			@Override
			public void run()
			{
				setTitleImage( SoapUILabelProvider.getImageForModelItem( desktopPanel.getModelItem() ) );
				firePropertyChange( IEditorPart.PROP_DIRTY );
			}
		} );
	}
}
