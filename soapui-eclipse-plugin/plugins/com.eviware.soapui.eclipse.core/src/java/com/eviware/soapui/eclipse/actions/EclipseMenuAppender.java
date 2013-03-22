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

package com.eviware.soapui.eclipse.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.support.action.SoapUIActionRegistry.SoapUIActionGroupAction;

/**
 * 
 * @author Lars H
 */
public class EclipseMenuAppender implements MenuListener
{
   private final ISelectionProvider selectionProvider;
   private final Menu viewMenu;

   public static EclipseMenuAppender createIfReady(Viewer viewer)
   {
      Control control = viewer.getControl();
      Menu menu = control.getMenu();
      if(menu != null)
      {
         return new EclipseMenuAppender(viewer, menu);
      }
      else
      {
         return null;
      }
   }
   
   private EclipseMenuAppender(ISelectionProvider viewer, Menu menu)
   {
      this.selectionProvider = viewer;
      this.viewMenu = menu;
      menu.addMenuListener(this);
   }
   
   public void menuHidden(MenuEvent e)
   {
   }

   public void menuShown(MenuEvent e)
   {
      ModelItem modelItem = null;
      ISelection selection = selectionProvider.getSelection();
      if(selection instanceof IStructuredSelection)
      {
         Object selectedObject = ((IStructuredSelection)selection).getFirstElement();
         if(selectedObject instanceof ModelItem)
            modelItem = (ModelItem) selectedObject;
      }
      if(modelItem == null)
         return;

      List actionsAndContributionItems = SwtActionBuilder.buildActions(modelItem);
      addActions(viewMenu, modelItem, actionsAndContributionItems);
   }

   private void addActions(Menu menu, ModelItem modelItem, List actionsAndContributionItems)
   {
      for(Object element : actionsAndContributionItems)
      {
         if(element instanceof Action)
         {
            Action action = (Action) element;
            if(action.isEnabled())
            {
               addActionMenu(menu, action);
            }
         }
         else if(element instanceof IContributionItem)
         {
            IContributionItem item = (IContributionItem) element;
            if(item instanceof Separator)
            {
               addSeparator(menu);
            }
         }
         else if(element instanceof SoapUIActionGroupAction)
         {
            addSubMenu(modelItem, menu, (SoapUIActionGroupAction) element);
         }
      }
   }

   private void addSubMenu(ModelItem modelItem, Menu menu, SoapUIActionGroupAction group)
   {
      MenuItem mi = new MenuItem(menu,SWT.CASCADE);

      Menu subMenu = new Menu (menu);
      mi.setMenu(subMenu);
      mi.setText(group.getName());

      ArrayList subActions = new ArrayList();
      SwtActionBuilder.addActions( modelItem, subActions, group.getActionGroup() );
      for(Object element : subActions)
      {
         if(element instanceof IAction)
         {
            addActionMenu(subMenu, (IAction) element);
         }
         else if(element instanceof IContributionItem)
         {
            IContributionItem item = (IContributionItem) element;
            if(item instanceof Separator)
            {
               addSeparator(subMenu);
            }
         }
         else if(element instanceof SoapUIActionGroupAction)
         {
            addSubMenu(modelItem, subMenu, (SoapUIActionGroupAction) element);
         }
      }
   }

   private void addActionMenu(Menu menu, IAction action)
   {
      MenuItem mi = new MenuItem(menu, SWT.NONE);
      mi.setText(action.getText());
      mi.addSelectionListener(new MenuAction(action));
      mi.setEnabled(action.isEnabled());
   }

   private void addSeparator(Menu menu)
   {
      MenuItem mi = new MenuItem(menu, SWT.SEPARATOR);
   }
}
