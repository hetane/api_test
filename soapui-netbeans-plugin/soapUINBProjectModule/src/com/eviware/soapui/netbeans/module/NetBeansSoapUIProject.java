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

import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.tree.SoapUITreeNode;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Sofia
 */
public final class NetBeansSoapUIProject implements org.netbeans.api.project.Project {

   private final FileObject projectDir;
   private final Project soapUIProject;
   LogicalViewProvider logicalView;
   SoapUITreeNode soapUIProjectNode;
   private final ProjectState state;

   public NetBeansSoapUIProject(FileObject projectDir, Project soapUIProject, SoapUITreeNode soapUIProjectNode, ProjectState state) {
      this.projectDir = projectDir;
      this.soapUIProject = soapUIProject;
      this.state = state;
      this.soapUIProjectNode = soapUIProjectNode;
      this.logicalView = new NetBeansSoapUILogicalViewProvider(soapUIProjectNode, this);
   }

   public Project getSoapUIProject() {
      return soapUIProject;
   }

   public FileObject getProjectDirectory() {
      return projectDir;
   }
   private Lookup lkp;

   public Lookup getLookup() {
      if (lkp == null) {
         lkp = Lookups.fixed(new Object[]{this, state, new ActionProviderImpl() //Provides standard actions like Build and Clean
                    , new SoapUIDeleteOperation(), loadProperties(), new SoapUIProjectInformation() //Project information implementation
                    , logicalView, new SoapUIProjectOpenedHook() });
      }
      return lkp;
   }

   
   
   private Properties loadProperties() {
      return new Properties(); // TODO: figure out how these properties relates to the project node's properties

   }

   private final class SoapUIProjectOpenedHook extends ProjectOpenedHook
   {
      @Override
      protected void projectOpened() {
      }

      @Override
      protected void projectClosed() {
         try {
            if( !Installer.isClosing())
               NetBeansPluginUISupport.getDefault().closeProject(soapUIProject);
         } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
         }
      }
   }
   
   private final class ActionProviderImpl implements ActionProvider {

      public ActionProviderImpl() {
      }

      public String[] getSupportedActions() {
         return new String[]{ActionProvider.COMMAND_DELETE};
      }

      public void invokeAction(String string, Lookup lookup) throws IllegalArgumentException {

         if (string.equalsIgnoreCase(ActionProvider.COMMAND_DELETE)) {
            DefaultProjectOperations.performDefaultDeleteOperation(NetBeansSoapUIProject.this);
         }
//            if (string.equalsIgnoreCase(ActionProvider.COMMAND_RENAME)) {
//                try {
//                    DefaultProjectOperations.performDefaultRenameOperation(NetBeansSoapUIProject.this, null);
//                    ((WsdlProject) NetBeansSoapUIProject.this.getSoapUIProject()).setName(getProjectDirectory().getName());
//                    
//                    NetBeansSoapUIProject.this.getSoapUIProject().save();
//                } catch (IOException ioe) {
//                    ErrorManager.getDefault().notify(ioe);
//                }
//            }
      }

      public boolean isActionEnabled(String command, Lookup context) {
         if ((command.equals(ActionProvider.COMMAND_DELETE))) {
            return true;
         } else {
            throw new IllegalArgumentException(command);
         }
      }
   }

   private final class SoapUIDeleteOperation implements DeleteOperationImplementation {

      public List<FileObject> getDataFiles() {
         List<FileObject> dataFiles = new ArrayList<FileObject>();
         dataFiles.add(NetBeansSoapUIProject.this.getProjectDirectory().getFileObject(NetBeansSoapUIProjectFactory.SOAP_UI_PROJECT_FILE_NAME));
         return dataFiles;
      }

      public List<FileObject> getMetadataFiles() {
         List<FileObject> dataFiles = new ArrayList<FileObject>();
         if (NetBeansSoapUIProject.this.getProjectDirectory().getFileObject("nbproject") != null) {
            dataFiles.add(NetBeansSoapUIProject.this.getProjectDirectory().getFileObject("nbproject"));
         }
         return dataFiles;
      }

      public void notifyDeleted() throws IOException {
         NetBeansSoapUIProject.this.state.notifyDeleted();
      }

      public void notifyDeleting() throws IOException {
         // TODO check why the project doesn't always gets removed on disk (something locking the soapui-project.xml file?)
         NetBeansSoapUIProject.this.getSoapUIProject().save();
         Installer.getWorkspace().removeProject(NetBeansSoapUIProject.this.getSoapUIProject());
         Installer.getWorkspace().save(true);
      }
   }

   /** Implementation of project system's ProjectInformation class */
   public final class SoapUIProjectInformation implements ProjectInformation {

      public Icon getIcon() {
         return new ImageIcon(Utilities.loadImage("com/eviware/soapui/netbeans/module/resources/soapui.gif")); //Todo, check where this icon is used?

      }

      public String getName() {
         return getSoapUIProject().getName();
      }

      public String getDisplayName() {
         return getName();
      }

      public void addPropertyChangeListener(PropertyChangeListener pcl) {
      }

      public void removePropertyChangeListener(PropertyChangeListener pcl) {
      }

      public Project getSoapuiProject() {
         return getSoapUIProject();
      }

      public org.netbeans.api.project.Project getProject() {
         return NetBeansSoapUIProject.this;
      }
   }
}
