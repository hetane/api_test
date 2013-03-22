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

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.SwingPluginSoapUICore;
import com.eviware.soapui.SwingSoapUICore;
import com.eviware.soapui.actions.SoapUIPreferencesAction;
import com.eviware.soapui.impl.wsdl.actions.project.CloseProjectAction;
import com.eviware.soapui.impl.wsdl.actions.project.ReloadProjectAction;
import com.eviware.soapui.impl.wsdl.actions.project.RemoveProjectAction;
import com.eviware.soapui.impl.wsdl.actions.project.RenameProjectAction;
import com.eviware.soapui.impl.wsdl.actions.project.SaveProjectAsAction;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.model.workspace.WorkspaceFactory;
import com.eviware.soapui.netbeans.module.NetBeansSoapUIProject.SoapUIProjectInformation;
import com.eviware.soapui.settings.UISettings;
import com.eviware.soapui.support.SoapUIException;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.SoapUIActionRegistry;
import com.eviware.soapui.support.log.TabbedLog4JMonitor;
import com.eviware.soapui.integration.impl.CajoServer;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {
   private static boolean closing;

   public static final String DEFAULT_WORKSPACE_FILE = "netbeans-soapui-workspace.xml";
   private static Workspace workspace;
   private static SwingSoapUICore soapUICore;

   public static String getSoapUIRoot() {
      return System.getProperty("netbeans.user", System.getProperty("user.home"));
   }
   
  public void restored() {

      try {


         if (soapUICore == null) {
//             System.setProperty( "soapui.jxbrowser.disable", "false");
//                ClassLoader cl = Thread.currentThread().getContextClassLoader();
//                try
//                {
//                    Thread.currentThread().setContextClassLoader(LogFactory.class.getClassLoader());
            initSoapUICore();
//                    HttpClientSupport.getHttpClient();
//                }
//                finally
//                {
//                    Thread.currentThread().setContextClassLoader(cl);
//                }

//            SwingActionDelegate.switchClassloader = true;
         }

         workspace = getWorkspace();
         while (workspace.getProjectCount() > 0) {
            workspace.removeProject(workspace.getProjectAt(0));
         }
         soapUICore.afterStartup(workspace);
                 CajoServer.getInstance().start();

         // stupid initializer (ok to do this here?)
         new SoapUIPreferencesAction();
      } catch (Exception e) {
         Exceptions.printStackTrace(e);
      }

   }

   public static Workspace getWorkspace() {
        final String wsfile = getSoapUIRoot() + File.separatorChar + DEFAULT_WORKSPACE_FILE;
      try {
         if (workspace == null) {
            if (soapUICore == null) {
               initSoapUICore();
            }

            SoapUI.getSettings().setBoolean(UISettings.CLOSE_PROJECTS, true);
               
            workspace = WorkspaceFactory.getInstance().openWorkspace(wsfile, null);
            while (workspace.getProjectCount() > 0) {
               workspace.removeProject(workspace.getProjectAt(0));
            }
         }
      } catch (Exception e) {
          if( workspace == null && new File( wsfile ).exists() )
          {
              SoapUI.log.error( "Failed to load default workspace: " + e.toString() + ", trying to recreate" );
              new File( wsfile ).delete();
                try {
                    workspace = WorkspaceFactory.getInstance().openWorkspace(wsfile, null );
                } catch (SoapUIException ex) {
                    Exceptions.printStackTrace(ex);
                }

          }
         
      }
      return workspace;
   }

   public static boolean isClosing()
   {
      return closing;
   }
   
   public void close() {
      
      closing = true;
      
      if( soapUICore != null )
      {
         try {
           
            if( workspace != null )
            {
               workspace.save(false);
               SoapUI.saveSettings();
            }
         } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
         }
      }
   }

   public static List<com.eviware.soapui.model.project.Project> getOpenSoapUIProjects() {
      List<com.eviware.soapui.model.project.Project> result = new ArrayList<com.eviware.soapui.model.project.Project>();
      Project[] projects = OpenProjects.getDefault().getOpenProjects();
      for (int i = 0; i < projects.length; i++) {
         Project project = projects[i];

         ProjectInformation pi = ProjectUtils.getInformation(project);
         if (!(pi instanceof SoapUIProjectInformation)) {
            continue;
         }
         SoapUIProjectInformation info = (NetBeansSoapUIProject.SoapUIProjectInformation) pi;
         com.eviware.soapui.model.project.Project soapUIProject = info.getSoapuiProject();
         if (soapUIProject != null) {
            result.add(soapUIProject);
         } else {
            SoapUI.log.error("Could not find soapUI project [" + info.getName() + "] in workspace");
         }
      }

      return result;
   }

   private static void initSoapUICore() {
      
      String root = getSoapUIRoot();
      System.setProperty("soapui.logroot", root + File.separatorChar);
      
      String old = System.setProperty("log4j.defaultInitOverride", "true");
      soapUICore = new SwingPluginSoapUICore( root ) {

         @Override
         protected SoapUIActionRegistry initActionRegistry() {
            
            SoapUIActionRegistry actionRegistry = super.initActionRegistry();
            
            // remove project-related actions provided by NetBeans project managements
            actionRegistry.removeAction( ReloadProjectAction.SOAPUI_ACTION_ID );
            actionRegistry.removeAction(RemoveProjectAction.SOAPUI_ACTION_ID);
            actionRegistry.removeAction(RenameProjectAction.SOAPUI_ACTION_ID);
            actionRegistry.removeAction(CloseProjectAction.SOAPUI_ACTION_ID);
            actionRegistry.removeAction(SaveProjectAsAction.SOAPUI_ACTION_ID);
            
            return actionRegistry;
         }
      };

      if( old == null )
         System.getProperties().remove("log4j.defaultInitOverride" );
      else
         System.setProperty("log4j.defaultInitOverride", old );
      
      UIManager.put("TabbedPane.selected", new Color(240, 240, 240));
      SwingUtilities.invokeLater(new Runnable() {

         public void run() {
            UISupport.setMainFrame(WindowManager.getDefault().getMainWindow());
         }
      });

      SoapUI.initLogMonitor(true, "soapui", new TabbedLog4JMonitor());
      SoapUI.log.info("Initialized soapUI Core");
   }
}
