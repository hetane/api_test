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

import com.eviware.soapui.support.SoapUIException;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.model.tree.SoapUITreeModel;
import com.eviware.soapui.model.tree.SoapUITreeNode;
import com.eviware.soapui.ui.Navigator;
import com.eviware.soapui.monitor.TestMonitor;
import com.eviware.soapui.ui.desktop.DesktopPanel;
import com.eviware.soapui.ui.support.DesktopListenerAdapter;
import com.eviware.soapui.support.log.LogDisablingTestMonitorListener;

import javax.swing.SwingUtilities;
import org.openide.ErrorManager;

/**
 *
 * @author Ajit
 */
public class NetBeansPluginUISupport {

    private static NetBeansPluginUISupport DEFAULT = null;

    private Workspace defaultWorkspace;
    private Navigator navigator;
    private SoapUITreeModel treeModel;
    private Map<SoapUITreeNode, SoapUITreeNodeWrapper> allSoapUINodes;
    private NetBeansSoapUIDesktop desktop;

    public static NetBeansPluginUISupport getDefault() {
        if (DEFAULT == null) {
            DEFAULT = new NetBeansPluginUISupport();
        }
        return DEFAULT;
    }

    private NetBeansPluginUISupport() {
        initSoapUI();
    }

   void closeProject(Project soapUIProject) throws IOException {
      soapUIProject.save();
      
      if( defaultWorkspace.getIndexOfProject(soapUIProject) != -1 )
      {
         defaultWorkspace.removeProject(soapUIProject);
      }
      else
      {
         NetBeansPluginUISupport.getDefault().getDesktop().closeDependantPanels(soapUIProject);
      }
   }

    private void initSoapUI() {
        allSoapUINodes = new HashMap<SoapUITreeNode, SoapUITreeNodeWrapper>();
        defaultWorkspace = Installer.getWorkspace();
        treeModel = new SoapUITreeModel(defaultWorkspace);
        treeModel.addTreeModelListener(new NetBeansSoapUITreeModelListener());
        desktop = new NetBeansSoapUIDesktop(defaultWorkspace);
        desktop.addDesktopListener(new DesktopListenerAdapter() {

            public void desktopPanelSelected(DesktopPanel desktopPanel) {
                ModelItem modelItem = desktopPanel.getModelItem();
                if (modelItem != null) {
                    navigator.selectModelItem(modelItem);
                }
            }
        });

        // we need a navigator to avoid NPE
        navigator = new Navigator(defaultWorkspace);
        SoapUI.setNavigator(navigator);
        SoapUI.setDesktop(desktop);

        TestMonitor testMonitor = SoapUI.getTestMonitor();

        testMonitor.addTestMonitorListener(new LogDisablingTestMonitorListener());
        testMonitor.init(defaultWorkspace);

    }

    public NetBeansSoapUIDesktop getDesktop()
    {
       return desktop;
    }
    
    public void addSoapUINode(SoapUITreeNode treeNode, SoapUITreeNodeWrapper wrapper) {
        allSoapUINodes.put(treeNode, wrapper);
    }

    public SoapUITreeNodeWrapper getWrapperNode( SoapUITreeNode treeNode )
    {
       return allSoapUINodes.get(treeNode);
    }
    
    public SoapUITreeNodeWrapper removeTreeNode( SoapUITreeNode treeNode )
    {
       return allSoapUINodes.remove(treeNode);
    }
    
    public Project getSoapUIProjectByFile(String soapUIProjectFilePath) throws SoapUIException {
        return defaultWorkspace.importProject(soapUIProjectFilePath);
    }

    public Project getSoapUIProjectByName(String name) {
        return defaultWorkspace.getProjectByName(name);
    }

    public SoapUITreeNode getSoapUIProjectNode(Project soapUIProject) {
        return treeModel.getTreeNode(soapUIProject);
    }

    void saveWorkspace(boolean flag) {
        defaultWorkspace.save(flag);
    }

    class NetBeansSoapUITreeModelListener implements TreeModelListener {

        public void treeNodesChanged(TreeModelEvent e) {
            refreshNodeTree(e);
        }

        public void treeNodesInserted(TreeModelEvent e) {
            refreshNodeTree(e);
        }

        public void treeNodesRemoved(final TreeModelEvent e) {
            try {
                for (int i = 0; i < e.getChildren().length; i++) {
                    Object removedChild = e.getChildren()[i];
                    final SoapUITreeNodeWrapper wrapper = allSoapUINodes.get(removedChild);
                    if (wrapper != null) {
                        wrapper.destroy();
                    }
                }
                
                SwingUtilities.invokeLater(new Runnable() {

               public void run() {
                   refreshNodeTree(e);
               }
            });
               
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
                SoapUI.logError(ioe);
            }
        }

        public void treeStructureChanged(TreeModelEvent e) {
            refreshNodeTree(e);
        }

        private void refreshNodeTree(TreeModelEvent e) {

            SoapUITreeNodeWrapper wrapper = allSoapUINodes.get(e.getPath()[e.getPath().length - 1]);
            if (wrapper != null) {
                wrapper.refresh();
            }
        }
    }
}
