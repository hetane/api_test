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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;

import com.eviware.soapui.eclipse.SoapUIActivator;
import com.eviware.soapui.eclipse.util.SelectionUtil;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.settings.ProjectSettings;
import com.eviware.soapui.support.SoapUIException;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.types.StringToStringMap;

/**
 * 
 * @author Lars H
 */
public abstract class AbstractAddProjectNatureAction implements IObjectActionDelegate
{
   public static final String SOAPUI_PROJECT_FILE = "soapui-project.xml";

   private final static Logger log;
   static {
	   try {
		SoapUIActivator.initSoapUI();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   log = Logger.getLogger(AbstractAddProjectNatureAction.class);
   }
   private final String natureId;
   
   private IJavaProject selectedProject;
   
   protected AbstractAddProjectNatureAction(String natureId)
   {
      this.natureId = natureId;
   }
   
   public void setActivePart(IAction action, IWorkbenchPart targetPart)
   {
   }

   public void selectionChanged(IAction action, ISelection selection)
   {
      selectedProject = SelectionUtil.getSelectedProject(selection);
   }

   public void run(IAction action)
   {
      IProgressMonitor monitor = null;
      
      // Add the nature to the project.
      IProject project = selectedProject.getProject();
      
      // Cannot modify closed projects.
      if(!project.isOpen())
         return;
      
      try
      {
         // Get project properties from the user.
         StringToStringMap newSettings = getProjectSettings(selectedProject); 
         if(newSettings == null)  // The user pushed Cancel.
            return;
         
         // Add SoapUI nature to the Eclipse project.
         addProjectNature(project, monitor);
         
         // Create SoapUI project.
         Project soapuiProject = createSoapUIProject(project, monitor);
         
         storeProjectSettings(soapuiProject, newSettings);
         
         //UISupport.showInfoMessage("SoapUI nature added to project '"+soapuiProject.getName()+"'! To explore the SoapUI project, make sure to use either the Project Explorer view or the SoapUI view.");
         if(UISupport.confirm("To explore the SoapUI project, either the Project Explorer view or the SoapUI view need to be used. Switch to the Project Explorer view now?", "SoapUI nature added to project")) {
        	 final IViewSite site = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.navigator.ProjectExplorer").getViewSite();
        	 final IFile file = project.getFile(SOAPUI_PROJECT_FILE);
        	 Display.getDefault().asyncExec( new Runnable() {
				public void run() {
					site.getSelectionProvider().setSelection(new StructuredSelection(file));
				}
        	 } );
         }
      }
      catch(CoreException e)
      {
         e.printStackTrace();  // TODO Configure Log4J instead of this.
         log.error("Could not add project nature", e);
      }
      catch(SoapUIException e)
      {
         e.printStackTrace();  // TODO Configure Log4J instead of this.
         log.error("Could not create SoapUI project", e);
      }
      catch(IOException e)
      {
         e.printStackTrace();  // TODO Configure Log4J instead of this.
         log.error("Could not save SoapUI project", e);
      }
   }

   protected void storeProjectSettings(Project soapuiProject, StringToStringMap newSettings)
   {
      // Store project properties.
      Settings projectSettings = soapuiProject.getSettings();
      for(String key : newSettings.keySet())
      {
         projectSettings.setString(key, newSettings.get(key));
      }
      projectSettings.setString( ProjectSettings.PROJECT_NATURE, natureId );
   }

   private void addProjectNature(IProject project, IProgressMonitor monitor) throws CoreException
   {
      if(!project.hasNature(natureId))
      {
         IProjectDescription description = project.getDescription();
         ArrayList<String> natureList = new ArrayList<String>( Arrays.asList(description.getNatureIds()) );
         log.info("Adding nature to project: " + project.getName());
         natureList.add(natureId);
         description.setNatureIds( (String[]) natureList.toArray( new String[natureList.size()] ) );
         project.setDescription(description, monitor);
      }
      assert project.hasNature(natureId);
   }

   /**
    * Create a SoapUI project in this Java project.
    * @param project
    * @param monitor
    * @throws SoapUIException 
    * @throws IOException 
    * @throws CoreException 
    */
   private Project createSoapUIProject(IProject project, IProgressMonitor monitor)
   throws SoapUIException, IOException, CoreException
   {
      String projectName = getSoapUIProjectName(project);
      
      // Create a SoapUI project.
      Workspace soapuiWorkspace = SoapUIActivator.getWorkspace();

      WsdlProject soapuiProject = null;

      // Create a file in the Eclipse project.
      File soapuiProjectFile = new File( project.getLocation().toOSString(), SOAPUI_PROJECT_FILE );
      if( soapuiProjectFile.exists())
      {
      	String path = soapuiProjectFile.getAbsolutePath();
      	
      	 // check existing projects
         for( int c = 0; c < soapuiWorkspace.getProjectCount(); c++ )
         {
         	soapuiProject = (WsdlProject) soapuiWorkspace.getProjectAt( c );
         	if( path.equals( soapuiProject.getPath() ))
         	{
         		break;
         	}
         	else
         		soapuiProject = null;
         }
         
         if( soapuiProject == null )
         	soapuiProject = (WsdlProject) soapuiWorkspace.importProject( path );
         
      	((WsdlProject)soapuiProject).setName( projectName );
      	log.info("Imported SoapUI project '" + projectName + "' at " + soapuiProjectFile);
      }
      else
      {
	      soapuiProject = (WsdlProject) soapuiWorkspace.createProject(projectName, soapuiProjectFile);
	      log.info("Create a SoapUI project '" + projectName + "' in " + soapuiProjectFile);
	      soapuiProject.save();
      }
      
      // Refresh to make the newly created soapui-project.xml file visible.
      project.refreshLocal( IResource.DEPTH_ONE, monitor );
      
      return soapuiProject;
   }
   
   /**
    * Use the Java project name in the SoapUI project name, to distinguish them from each other.
    * @param project
    * @return
    */
   private String getSoapUIProjectName(IProject project)
   {
      return project.getName();
   }
   
   /**
    * Get settings for the new project from the user.
    * @param javaProject
    * @return
    * @throws JavaModelException
    * @throws IllegalStateException
    */
   protected abstract StringToStringMap getProjectSettings(IJavaProject javaProject)
   throws JavaModelException, IllegalStateException;
}
