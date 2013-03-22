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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.eviware.soapui.eclipse.editors.DesktopPanelEditorInput;
import com.eviware.soapui.eclipse.editors.ModelItemEditor;
import com.eviware.soapui.eclipse.project.ModelItemProxy;
import com.eviware.soapui.eclipse.swt_awt.SwtAwtRequestEditor;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.PanelBuilder;
import com.eviware.soapui.model.util.PanelBuilderRegistry;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.ui.desktop.AbstractSoapUIDesktop;
import com.eviware.soapui.ui.desktop.DesktopPanel;
import com.eviware.soapui.ui.desktop.SoapUIDesktop;
import com.eviware.x.impl.swt.SwtDialogs;

public class EclipseSoapUIDesktop extends AbstractSoapUIDesktop
{
	private Map<DesktopPanel, IEditorPart> editorMap = new HashMap<DesktopPanel, IEditorPart>();
	private Map<ModelItem, DesktopPanel> modelItemMap = new HashMap<ModelItem, DesktopPanel>();
	private boolean addedListener = false;

	public EclipseSoapUIDesktop( Workspace workspace )
	{
		super( workspace );
	}

   public boolean closeAll()
   {
      SwtDialogs.threadLog.debug("EclipseSoapUIDesktop.closeAll -> runInUIThreadAsync");
      SwtDialogs.runInUIThreadAsync( new Runnable()
      {
         public void run()
         {
            SwtDialogs.threadLog.debug("runInUIThreadAsync -> EclipseSoapUIDesktop.closeAll");
            List<DesktopPanel> tmp = new ArrayList<DesktopPanel>(editorMap.keySet());
            for(DesktopPanel desktopPanel : tmp)
            {
               internalCloseDesktopPanel(desktopPanel);
            }
            editorMap.clear();
         }
      } );
      
      return true;
   }

	public boolean closeDesktopPanel( final DesktopPanel desktopPanel )
	{
		if( !editorMap.containsKey( desktopPanel ))
			return false;
		
      SwtDialogs.threadLog.debug("EclipseSoapUIDesktop.closeDesktopPanel -> runInUIThreadAsync");
		SwtDialogs.runInUIThreadAsync( new Runnable()
		{
			public void run()
			{
            SwtDialogs.threadLog.debug("runInUIThreadAsync -> EclipseSoapUIDesktop.closeDesktopPanel: "
                  + desktopPanel.getClass().getName());
				internalCloseDesktopPanel(desktopPanel);
			}
		} );
		return true;
	}

   private void internalCloseDesktopPanel(final DesktopPanel desktopPanel)
   {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      
      // page is null when eclipse are closing down..
      if( page != null )
         page.closeEditor( editorMap.get( desktopPanel ), false );
      
      editorMap.remove( desktopPanel );
      if( desktopPanel.getModelItem() != null )
         modelItemMap.remove( desktopPanel.getModelItem() );
      
      desktopPanel.onClose( false );
      fireDesktopPanelClosed( desktopPanel );
   }

	public boolean closeDesktopPanel( ModelItem modelItem )
	{
		if( modelItem instanceof ModelItemProxy )
		{
			modelItem = ((ModelItemProxy)modelItem).getModelItem();
		}
		
		if( !modelItemMap.containsKey( modelItem ))
			return false;
		
      SwtDialogs.threadLog.debug("EclipseSoapUIDesktop.closeDesktopPanel -> runInUIThreadAsync");
		SwtDialogs.runInUIThreadAsync( new DesktopPanelCloser( modelItem ) );
		return true;
	}

	public DesktopPanel getDesktopPanel( ModelItem modelItem )
	{
		if( modelItem instanceof ModelItemProxy )
		{
			modelItem = ((ModelItemProxy)modelItem).getModelItem();
		}
		
		return modelItemMap.get( modelItem );
	}

	public DesktopPanel[] getDesktopPanels()
	{
		return editorMap.keySet().toArray( new DesktopPanel[editorMap.size()] );
	}

	public boolean hasDesktopPanel( ModelItem modelItem )
	{
		if( modelItem instanceof ModelItemProxy )
		{
			modelItem = ((ModelItemProxy)modelItem).getModelItem();
		}
		
		return modelItemMap.containsKey( modelItem );
	}

	public DesktopPanel showDesktopPanel( ModelItem modelItem )
	{
		if( modelItem instanceof ModelItemProxy )
		{
			modelItem = ((ModelItemProxy)modelItem).getModelItem();
		}
		
		if( modelItemMap.containsKey( modelItem ))
		{
			SwtDialogs.runInUIThread( new ModelItemDesktopPanelActivator( modelItem ) );
		}
		else
		{
			SwtDialogs.runInUIThread( new ModelItemDesktopPanelOpener( modelItem ) );
		}

		return modelItemMap.get( modelItem );
	}

	public DesktopPanel showDesktopPanel( final DesktopPanel desktopPanel )
	{
		if( editorMap.containsKey( desktopPanel ))
		{
         SwtDialogs.threadLog.debug("EclipseSoapUIDesktopPanel.showDesktopPanel -> runInUIThread");
			SwtDialogs.runInUIThread( new Runnable()
			{
				public void run()
				{
               SwtDialogs.threadLog.debug("EclipseSoapUIDesktopPanel.showDesktopPanel: run");
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage();
					page.activate( editorMap.get( desktopPanel ) );
					fireDesktopPanelSelected( desktopPanel );
					
					if( !addedListener )
					{
						page.addPartListener( new PartListener() );
						addedListener = true;
					}
				}
			} );
		}
		else
		{
         SwtDialogs.threadLog.debug("EclipseSoapUIDesktopPanel.showDesktopPanel -> runInUIThread");
			SwtDialogs.runInUIThread( new Runnable()
			{
				public void run()
				{
               SwtDialogs.threadLog.debug("EclipseSoapUIDesktopPanel.showDesktopPanel: run");
					try
					{
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
									.getActivePage();
	
						DesktopPanelEditorInput editorInput = new DesktopPanelEditorInput( desktopPanel );
						IEditorPart editor = page.openEditor( editorInput, ModelItemEditor.EDITOR_ID, true );
	
						editorMap.put( desktopPanel, editor );
						fireDesktopPanelCreated( desktopPanel );
					}
					catch( PartInitException e )
					{
						e.printStackTrace();
					}
				}
			} );
		}

		return desktopPanel;
	}
	
	private final class DesktopPanelCloser implements Runnable
	{
		private final ModelItem modelItem;

		public DesktopPanelCloser( ModelItem modelItem )
		{
			this.modelItem = modelItem;
		}

		public void run()
		{
         SwtDialogs.threadLog.debug("DesktopPanelCloser.run does nothing");
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage();
		
			DesktopPanel desktopPanel = modelItemMap.get( modelItem );
			page.closeEditor( editorMap.get( desktopPanel ), false );
		
			editorMap.remove( desktopPanel );
			modelItemMap.remove( modelItem );
			
			desktopPanel.onClose( false );
			fireDesktopPanelClosed( desktopPanel );
		}
	}

	private final class ModelItemDesktopPanelOpener implements Runnable
	{
		private final ModelItem modelItem;

		public ModelItemDesktopPanelOpener( ModelItem modelItem )
		{
			this.modelItem = modelItem;
		}

		public void run()
		{
			try
			{
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage();
				PanelBuilder<ModelItem> panelBuilder = PanelBuilderRegistry.getPanelBuilder( modelItem );
				if( panelBuilder == null || !panelBuilder.hasDesktopPanel() )
					return;
				
				DesktopPanel desktopPanel = panelBuilder.buildDesktopPanel( modelItem );
		      if(desktopPanel == null)
		      {
		         SoapUIActivator.logError( "Cannot open " + modelItem.getClass().getName(), null );
		         return;
		      }
		
				DesktopPanelEditorInput editorInput = new DesktopPanelEditorInput( desktopPanel );
				IEditorPart editor = page.openEditor( editorInput, ModelItemEditor.EDITOR_ID, true );
				
				modelItemMap.put( modelItem, desktopPanel );
				editorMap.put( desktopPanel, editor );
				
				fireDesktopPanelCreated( desktopPanel );
				
				if( !addedListener )
				{
					page.addPartListener( new PartListener() );
					addedListener = true;
				}
			}
			catch( PartInitException e )
			{
				e.printStackTrace();
			}
		}
	}

	private final class ModelItemDesktopPanelActivator implements Runnable
	{
		private final ModelItem modelItem;

		public ModelItemDesktopPanelActivator( ModelItem modelItem )
		{
			this.modelItem = modelItem;
		}

		public void run()
		{
         SwtDialogs.threadLog.debug("ModelItemDesktopPanelActivator: run");
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage();
			
			DesktopPanel desktopPanel = modelItemMap.get( modelItem );
			page.activate( editorMap.get( desktopPanel ) );
			
			fireDesktopPanelSelected( desktopPanel );
		}
	}

	private final class PartListener implements IPartListener
	{
		public void partActivated( IWorkbenchPart part )
		{
			if( part instanceof SwtAwtRequestEditor )
			{
				DesktopPanel desktopPanel = ((SwtAwtRequestEditor)part).getDesktopPanel();
				fireDesktopPanelSelected( desktopPanel );
			}
		}

		public void partBroughtToTop( IWorkbenchPart part )
		{
		}

		public void partClosed( IWorkbenchPart part )
		{
			if( part instanceof SwtAwtRequestEditor )
			{
				DesktopPanel desktopPanel = ((SwtAwtRequestEditor)part).getDesktopPanel();
				editorMap.remove( desktopPanel );
				if( desktopPanel.getModelItem() != null )
					modelItemMap.remove( desktopPanel.getModelItem() );
				
				desktopPanel.onClose( false );
				fireDesktopPanelClosed( desktopPanel );
			}
		}

		public void partDeactivated( IWorkbenchPart part )
		{
		}

		public void partOpened( IWorkbenchPart part )
		{
		}
	}

   public JComponent getDesktopComponent()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public void transferTo(SoapUIDesktop newDesktop)
   {
      // TODO Auto-generated method stub
      
   }

   public void minimize( DesktopPanel desktopPanel )
   {
   }

   public void maximize(DesktopPanel arg0)
   {
   }
}
