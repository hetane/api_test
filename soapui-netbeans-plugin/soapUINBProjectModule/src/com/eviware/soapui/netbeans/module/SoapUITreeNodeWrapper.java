/*
 *  soapUI, copyright (C) 2004-2011 smartbear.com
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the
 *  terms of version 2.1 of the GNU Lesser General Public License as published by
 *  the Free Software Foundation.
 *  For the avoidance of doubt, eviware elects not to use any
 *  later versions of the LGPL License.
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details at gnu.org.
 *
 *
 *  -------------------------------------------------------------------------------------
 *  If soapUI is distributed with products from Sun, note the following:
 *  -------------------------------------------------------------------------------------
 *
 *  For the avoidance of doubt, except that if any license choice other than
 *  GPL or LGPL is available it will apply instead,
 *  Sun elects to use only the General Public License version 2 (GPLv2) at
 *  this time for any software where a choice of GPL
 *  license versions is made available with the language indicating that
 *  GPLv2 or any later version may be used, or where a
 *  choice of which version of the GPL is applied is otherwise unspecified.
 */
package com.eviware.soapui.netbeans.module;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.PanelBuilder;
import com.eviware.soapui.model.tree.SoapUITreeNode;
import com.eviware.soapui.model.tree.nodes.ProjectTreeNode;
import com.eviware.soapui.model.util.PanelBuilderRegistry;
import com.eviware.soapui.support.action.swing.ActionList;
import com.eviware.soapui.support.action.swing.ActionSupport;


import com.eviware.soapui.support.components.JPropertiesTable;
import com.eviware.soapui.support.components.JPropertiesTable.PropertyDescriptor;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Sofia
 */
public class SoapUITreeNodeWrapper extends AbstractNode {

   final SoapUITreeNode soapUITreeNode;
   private Action preferredAction = null;
   private boolean preferredActionInitialized = false;
   private String lastReturnedDisplayName;

   public SoapUITreeNodeWrapper(SoapUITreeNode soapUITreeNode) {
      super(new Children.Array());
      this.soapUITreeNode = soapUITreeNode;
//        NetBeansSoapUIProjectFactory.addSoapUINode(soapUITreeNode, this);
      NetBeansPluginUISupport.getDefault().addSoapUINode(soapUITreeNode, this);
      
      addChildren();
   }

   public SoapUITreeNodeWrapper(SoapUITreeNode soapUITreeNode, Lookup lookup) {
      super(new Children.Array(), lookup);
      this.soapUITreeNode = soapUITreeNode;
//        NetBeansSoapUIProjectFactory.addSoapUINode(soapUITreeNode, this);
      NetBeansPluginUISupport.getDefault().addSoapUINode(soapUITreeNode, this);
      addChildren();
   }

   /*
    * TODO Refactor into a separate class
    * TODO fix bugg with changing name properties. 
    * TODO remove popup editor (...) from properties which has options
    * TODO fox problem when renaming things which has open windows (also a problem with normal rename-actions)
    * */
   @Override
   protected Sheet createSheet() {
      Sheet sheet = Sheet.createDefault();
      Sheet.Set set = Sheet.createPropertiesSet();
      PanelBuilder panelBuilder = PanelBuilderRegistry.getPanelBuilder(this.soapUITreeNode.getModelItem());
      if ((panelBuilder != null) && (panelBuilder.hasOverviewPanel())) {
         Component panel = panelBuilder.buildOverviewPanel(this.soapUITreeNode.getModelItem());
         if (panel instanceof JPropertiesTable) {
            JPropertiesTable.PropertiesTableModel model = ((JPropertiesTable) panel).getTableModel();
            for (int i = 0; i < model.getRowCount(); i++) {
               PropertyDescriptor descr = model.getPropertyDescriptorAt(i);
               Property prop;

               // TODO remove the last part when renaming projects work properly
               if ((!descr.isEditable()) || ((descr.getName().equalsIgnoreCase("Name")) && (this.soapUITreeNode.getModelItem() instanceof WsdlProject))) {
                  prop = new ReadOnlyProperty(descr.getName(), descr.getCaption(), descr.getCaption(), model, i);
               } else {
                  prop = new ReadWriteProperty(descr.getName(), descr.getCaption(), descr.getCaption(), model, i, descr.getOptions());
               }
               prop.setName(descr.getCaption());
               set.put(prop);
            }
         }
      }

      sheet.put(set);
      return sheet;
   }

   public Action[] getActions(boolean context) {

      ActionList actionList = soapUITreeNode.getActions();
      ArrayList<Action> actions = extractActionsFromActionList(actionList);

      //actions.add(propertiesAction.createContextAwareInstance(this.getLookup()));
      if (soapUITreeNode instanceof ProjectTreeNode) {
         //actions.add(CommonProjectActions.renameProjectAction());
         actions.add(null);
         actions.add(CommonProjectActions.closeProjectAction());
         actions.add(CommonProjectActions.deleteProjectAction());

      }
      return actions.toArray(new Action[actions.size()]);
   }

   @Override
   public Action getPreferredAction() {
      //ugly way of making sure we have fetched the preferred action
      if (!preferredActionInitialized) {
         this.getActions(false);
         preferredActionInitialized = true;
      }

      return (preferredAction != null) ? preferredAction : super.getPreferredAction();
   }

   private ArrayList<Action> extractActionsFromActionList(ActionList actionList) {
      ArrayList<Action> actions = new ArrayList<Action>();

      for (int i = 0; i < actionList.getActionCount(); i++) {
         if (actionList.getActionAt(i) instanceof ActionSupport.ActionListAction) {
            // TODO: Find another way to fix nested action lists  
            ActionSupport.ActionListAction list = (ActionSupport.ActionListAction) actionList.getActionAt(i);
            ArrayList<Action> childActions = extractActionsFromActionList(list.getActionList());
            actions.add(new PopupAction(list.getActionList().getLabel(), childActions));
         } else {
            if (!(actionList.getActionAt(i) == ActionSupport.SEPARATOR_ACTION)) {

               Action action = actionList.getActionAt(i);
               // if shortcut is Enter then this is our preffered action                        
               if (KeyStroke.getKeyStroke("pressed ENTER").equals(action.getValue(Action.ACCELERATOR_KEY))) {

                  this.preferredAction = action;
               }
               // remove all shortcuts (for now)
               action.putValue(Action.ACCELERATOR_KEY, null);
               actions.add(action);
            } else {
               actions.add(null);
            }
         }
      }
      return actions;
   }

   public Image getIcon(int type) {
      return soapUITreeNode.getModelItem().getIcon().getImage();
   }

   public Image getOpenedIcon(int type) {
      return getIcon(type);
   }

   public String getDisplayName() {
      lastReturnedDisplayName = soapUITreeNode.getModelItem().getName();
      return lastReturnedDisplayName;
   }
   
   public void refresh() {
      
      String name = soapUITreeNode.getModelItem().getName();
      if( !name.equals(lastReturnedDisplayName))
         fireDisplayNameChange(lastReturnedDisplayName, name);
      
      Node [] nodeArray = this.getChildren().getNodes();
      List<Node> nodes = new ArrayList<Node>(Arrays.asList(nodeArray));
      
      Node[] childSoapUINBNodes = new Node[soapUITreeNode.getChildCount()];
      for (int i = 0; i < soapUITreeNode.getChildCount(); i++) {
         
         SoapUITreeNodeWrapper wrapperNode = NetBeansPluginUISupport.getDefault().getWrapperNode(soapUITreeNode.getChildNode(i));
         if( wrapperNode == null )
         {
            wrapperNode = new SoapUITreeNodeWrapper(soapUITreeNode.getChildNode(i));
         }
         else nodes.remove( wrapperNode );
            
         childSoapUINBNodes[i] = wrapperNode;
      }
      
      if( !nodes.isEmpty() || nodeArray.length != childSoapUINBNodes.length )
      {
         getChildren().remove( nodeArray );
         getChildren().add(childSoapUINBNodes);

         for( Node node : nodes )
         {
            try {
               node.destroy();
            } catch (IOException ex) {
               Exceptions.printStackTrace(ex);
            }
         }
      }
   }

   @Override
   public void destroy() throws IOException {
//      System.out.println( "Destroying SoapUITreeNodeWrapper for " + soapUITreeNode.toString() );
      NetBeansPluginUISupport.getDefault().removeTreeNode( this.soapUITreeNode );
      
      for( Node node : getChildren().getNodes())
      {
         node.destroy();
      }
      
      super.destroy();
   }

   private void addChildren() {
      Node[] childSoapUINBNodes = new Node[soapUITreeNode.getChildCount()];
      for (int i = 0; i < soapUITreeNode.getChildCount(); i++) {
         childSoapUINBNodes[i] = new SoapUITreeNodeWrapper(soapUITreeNode.getChildNode(i));
      }
      // Note: the API docs says not to use the add method...
      this.getChildren().add(childSoapUINBNodes);
   }

   private static class ReadWriteProperty extends PropertySupport.ReadWrite {

      private int rowNumber;
      private JPropertiesTable.PropertiesTableModel model;
      private Object[] options;

      ReadWriteProperty(String name,
              String displayName,
              String shortDescription,
              JPropertiesTable.PropertiesTableModel model,
              int rowNumber,
              Object[] options) {
         super(name, String.class, displayName, shortDescription);
         this.model = model;
         this.rowNumber = rowNumber;
         this.options = options;

      }

      @Override
      public Object getValue() throws IllegalAccessException, InvocationTargetException {

         return (model.getValueAt(rowNumber, 1) == null) ? "" : model.getValueAt(rowNumber, 1);

      }

      public Object getValue(String str) {
         if ((str.equals("inplaceEditor")) && (options != null)) {
            InplaceComboBoxPropertyEditor editor = new InplaceComboBoxPropertyEditor();
            editor.setAvailableValues(options);
            return editor;
         }
         return super.getValue(str);
      }

      @Override
      public void setValue(Object value) throws IllegalAccessException, InvocationTargetException {

         model.setValueAt(value, rowNumber, 1);

      }
   }

   private static class ReadOnlyProperty extends PropertySupport.ReadOnly {

      private int rowNumber;
      JPropertiesTable.PropertiesTableModel model;

      ReadOnlyProperty(String name,
              String displayName,
              String shortDescription,
              JPropertiesTable.PropertiesTableModel model,
              int rowNumber) {
         super(name, String.class, displayName, shortDescription);
         this.model = model;
         this.rowNumber = rowNumber;
      }

      @Override
      public Object getValue() throws IllegalAccessException, InvocationTargetException {
         return (model.getValueAt(rowNumber, 1) == null) ? "" : model.getValueAt(rowNumber, 1);
      }
   }

   private static class PopupAction extends AbstractAction implements Presenter.Popup {

      private ArrayList<Action> actions;
      private String label;

      PopupAction(String label, ArrayList<Action> actions) {
         this.actions = actions;
         this.label = label;

      }

      public void actionPerformed(ActionEvent e) {
         throw new UnsupportedOperationException("Not supported.");
      }

      public JMenuItem getPopupPresenter() {
         JMenu popup = new JMenu();
         popup.setText(label);
         if (actions != null) {
            for (Iterator<Action> it = actions.iterator(); it.hasNext();) {
               Action action = it.next();
               if (action != null) {
                  popup.add(action);
               } else {
                  popup.addSeparator();
               }
            }
         }
         return popup;

      }
   }
}

