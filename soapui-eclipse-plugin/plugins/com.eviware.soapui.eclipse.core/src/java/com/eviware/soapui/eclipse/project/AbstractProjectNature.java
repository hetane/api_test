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

package com.eviware.soapui.eclipse.project;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import com.eviware.soapui.eclipse.SoapUIActivator;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.support.SoapUIException;
import com.eviware.soapui.support.UISupport;

/**
 * 
 * @author Lars H
 */
public class AbstractProjectNature implements IProjectNature
{
   /** The id of the JBossWS project nature as defined in plugin.xml */
   public static final String JBOSSWS_NATURE_ID = Project.JBOSSWS_NATURE_ID;
   
   /** The id of the SoapUI project nature as defined in plugin.xml */
   public static final String SOAPUI_NATURE_ID = Project.SOAPUI_NATURE_ID;

   private IProject project;
   
   private final static Logger log = Logger.getLogger(AbstractProjectNature.class);
   
   public AbstractProjectNature()
   {
   }
   
   public void configure() throws CoreException
   {
   }

   public void deconfigure() throws CoreException
   {
   }

   public IProject getProject()
   {
      return project;
   }

   public void setProject(IProject project)
   {
      this.project = project;
   }

   /**
    * Get the SoapUI project that belongs to this Eclipse project.
    * @return
    */
   public Project getSoapUIProject() throws IllegalStateException
   {
      if( project == null )
         throw new IllegalStateException( "Eclipse project is null" );

      return getSoapUIProject(project);
   }
   
   /**
    * Get the SoapUI project that belongs to an Eclipse project.
    * @return
    */
   public static Project getSoapUIProject(IProject project)
   {
      // FIXME Eclipse: Find SoapUI project in a robust way. The Eclipse project may change name.
      Workspace workspace = SoapUIActivator.getWorkspace();
      if(workspace == null)
         return null;
      String projectName = project.getName();
      Project soapuiProject = workspace.getProjectByName(projectName);
      if( soapuiProject == null )
      {
//         log.IllegalStateException( "No SoapUI project with name '" + projectName + "' found " );
         
         File soapuiProjectFile = new File( project.getLocation().toOSString(), 
         		AbstractAddProjectNatureAction.SOAPUI_PROJECT_FILE );
         
         try
			{
				if( soapuiProjectFile.exists() )
				{
					log.info( "Reimporting project [" + projectName + "] to workspace" );
					workspace.importProject( soapuiProjectFile.getAbsolutePath() );
				}
				else
				{
					log.info( "Recreating project [" + projectName + "] in workspace" );
					workspace.createProject( projectName, soapuiProjectFile );
				}
			}
			catch (SoapUIException e)
			{
				e.printStackTrace();
				UISupport.showErrorMessage( "Failed to reimport/recreate project" );
			}
         
      }
      return soapuiProject;
   }
   
   protected String getSetting(String key)
   {
      Project soapuiProject = getSoapUIProject();
      Settings settings = soapuiProject.getSettings();
      return settings.getString(key, null);
   }
}
