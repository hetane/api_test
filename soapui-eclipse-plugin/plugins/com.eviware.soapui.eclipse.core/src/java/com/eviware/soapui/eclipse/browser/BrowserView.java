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

package com.eviware.soapui.eclipse.browser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.eviware.soapui.eclipse.SoapUIActivator;
import com.eviware.soapui.eclipse.actions.SwtActionBuilder;
import com.eviware.soapui.eclipse.editors.ModelItemEditor;
import com.eviware.soapui.eclipse.editors.RequestEditorInput;
import com.eviware.soapui.eclipse.project.SoapuiAdapterFactory;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.support.action.SoapUIActionRegistry.SoapUIActionGroupAction;

/**
 * This view displays the SoapUI projects in a tree.
 * 
 * @author Lars H
 */
public class BrowserView extends ViewPart
{
   private TreeViewer viewer;
   private OpenAction openAction = new OpenAction();

	/**
	 * The constructor.
	 */
	public BrowserView()
   {
	}

   private Workspace getWorkspace()
   {
	   return SoapuiAdapterFactory.getIAdaptableDecoratedWorkspace(((SoapUIActivator.getWorkspace())));
   }

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent)
   {
      viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

      getSite().setSelectionProvider( viewer );
      viewer.setContentProvider(new WorkspaceContentProvider());
      viewer.setLabelProvider(new SoapUILabelProvider());
      viewer.setInput(getWorkspace());
      hookContextMenu();
      hookDoubleClickAction();
      contributeToActionBars();
	}

	public TreeViewer getViewer()
	{
		return viewer;
	}

	private void hookContextMenu()
   {
      MenuManager menuMgr = new MenuManager("#PopupMenu");
      menuMgr.setRemoveAllWhenShown(true);
      menuMgr.addMenuListener(new IMenuListener()
      {
         public void menuAboutToShow(IMenuManager manager)
         {
            BrowserView.this.fillContextMenu(manager);
         }
      });
      Menu menu = menuMgr.createContextMenu(viewer.getControl());
      viewer.getControl().setMenu(menu);
      getSite().registerContextMenu(menuMgr, viewer);
   }

	private void contributeToActionBars()
   {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
   {
      // These commands are only enabled when the selection is null.
      // There's no way to deselect, so this may be impossible.
      // Therefore, show them in the local pull down too.
      List actionsAndContributionItems = SwtActionBuilder.buildActions( getWorkspace() );
      for(Object element : actionsAndContributionItems)
      {
         if(element instanceof Action)
         {
            Action action = (Action) element;
            manager.add(action);
//         if(EclipseActions.actionIsImplemented(swingAction))
//         {
//            manager.add(eclipseAction);
//         }
         }
         else if(element instanceof IContributionItem)
         {
            IContributionItem item = (IContributionItem) element;
            manager.add(item);
         }
      }
	}

	private void fillContextMenu(IMenuManager manager)
   {
      ISelection selection = viewer.getSelection();
      Object obj = ((IStructuredSelection)selection).getFirstElement();

      // TODO Refactor the soapui actions to not use Swing. Then, change this.
      if(obj instanceof ModelItem)
      {
         ModelItem modelItem = (ModelItem) obj;
         List actionsAndContributionItems = SwtActionBuilder.buildActions(modelItem);
         buildActionMenus(modelItem, manager, actionsAndContributionItems);
      }
      
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
   
   private void buildActionMenus(ModelItem modelItem, IMenuManager manager, List actionsAndContributionItems)
   {
      for(Object element : actionsAndContributionItems)
      {
         if(element instanceof IAction)
         {
            IAction action = (IAction) element;
            manager.add(action);
         }
         else if(element instanceof IContributionItem)
         {
            IContributionItem item = (IContributionItem) element;
            manager.add(item);
         }
         else if(element instanceof SoapUIActionGroupAction)
         {
            SoapUIActionGroupAction group = (SoapUIActionGroupAction) element;
            ArrayList subActions = new ArrayList();
            SwtActionBuilder.addActions( modelItem, subActions, group.getActionGroup() );
            MenuManager subManager = new MenuManager(group.getName());
            manager.add(subManager);
            buildActionMenus(modelItem, subManager, subActions);
         }
      }
   }
	
	private void fillLocalToolBar(IToolBarManager manager)
   {
	}

   private void hookDoubleClickAction()
   {
      viewer.addDoubleClickListener(new IDoubleClickListener()
      {
         public void doubleClick(DoubleClickEvent event)
         {
            ISelection selection = viewer.getSelection();
            Object obj = ((IStructuredSelection) selection).getFirstElement();
            openAction.setSelection(obj);
            if(openAction.isEnabled())
               openAction.run();
         }
      });
   }
   
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
   {
      viewer.getControl().setFocus();
   }

   private class OpenAction extends Action
   {
      private WsdlRequest request;
      
      public OpenAction()
      {
         setText("Open");
      }
      
      public void setSelection(Object selection)
      {
         request = (selection instanceof WsdlRequest ? (WsdlRequest)selection : null);
         setEnabled(request != null);
      }
      
      public void run()
      {
         // Open SOAP request/response editor.
         IWorkbenchPage page = getViewSite().getPage();
         RequestEditorInput editorInput = new RequestEditorInput(request);
         try
         {
            page.openEditor(editorInput, ModelItemEditor.EDITOR_ID, true);
         }
         catch(PartInitException e)
         {
            SoapUIActivator.logError("Could not open SOAP request editor", e);
         }
      }
   }

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		// TODO Auto-generated method stub
		
	}

	public ISelection getSelection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		// TODO Auto-generated method stub
		
	}

	public void setSelection(ISelection selection)
	{
		// TODO Auto-generated method stub
		
	}

	public void selectModelItem(final ModelItem item)
	{
		viewer.getTree().getDisplay().asyncExec( new Runnable() {
			public void run()
			{
				viewer.expandToLevel( item, 1 );
				viewer.setSelection( new StructuredSelection( item ) );
			}} );
	}
}
