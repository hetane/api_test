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

import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import com.eviware.soapui.eclipse.browser.IAdaptableModelItemDecoratorInvocationHandler;
import com.eviware.soapui.eclipse.browser.ModelItemInvocationHandler;
import com.eviware.soapui.eclipse.properties.InterfacePropertiesSource;
import com.eviware.soapui.eclipse.properties.OperationPropertiesSource;
import com.eviware.soapui.eclipse.properties.ProjectPropertiesSource;
import com.eviware.soapui.eclipse.properties.RequestPropertiesSource;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.iface.Interface;
import com.eviware.soapui.model.iface.Operation;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.workspace.Workspace;

/**
 * This class "adapts" a soapui-project.xml file to the corresponding SoapUI Project.
 * This is used by Eclipse like a dynamic type cast.
 * 
 * It also has options for adapting our various ModelItems to 
 * IPropertySource implementations. This is used to present properties in
 * the Eclipse properties view. 
 * 
 * @author Lars H
 */
public class SoapuiAdapterFactory implements IAdapterFactory
{
   private static final String SOAPUI_PROJECT_FILE = "soapui-project.xml";
   
   private static final Class[] CAN_ADAPT_TO = { Project.class, ProjectWithJBossWsNature.class, IPropertySource.class };
 
   private final static Logger log = Logger.getLogger(SoapuiAdapterFactory.class);

   public Class[] getAdapterList()
   {
      return CAN_ADAPT_TO;
   }
   
   public Object getAdapter(Object adaptableObject, Class adapterType)
   {
      try
      {
         if(adaptableObject instanceof IFile)
         {
            IFile file = (IFile) adaptableObject;
            if(Project.class.equals(adapterType))
            {
               return findSoapuiProject(file);
            }
            else if(ProjectWithJBossWsNature.class.equals(adapterType))
            {
               if(file.getProject().hasNature(AbstractProjectNature.JBOSSWS_NATURE_ID))
               {
                  Project project = findSoapuiProject(file);
                  if(project != null)
                     return new ProjectWithJBossWsNature(project);
               }
            }
            else if(ProjectWithSoapUINature.class.equals(adapterType))
            {
               if(file.getProject().hasNature(AbstractProjectNature.SOAPUI_NATURE_ID))
               {
                  Project project = findSoapuiProject(file);
                  if(project != null)
                     return new ProjectWithSoapUINature(project);
               }
            }
         }
         // take care of IPropertySource Adaptations on all ModelItems
         if (IPropertySource.class.equals(adapterType)) {
        	if (adaptableObject instanceof Project){
        		return new ProjectPropertiesSource((Project)adaptableObject);
        	}
        	else if (adaptableObject instanceof WsdlInterface){
        		return new InterfacePropertiesSource((WsdlInterface)adaptableObject);
        	}
        	else if (adaptableObject instanceof WsdlInterface){
        		return new OperationPropertiesSource((WsdlOperation)adaptableObject);
        	}
        	else if (adaptableObject instanceof Request){
        		return new RequestPropertiesSource((Request)adaptableObject);
        	}
        	 
         }
      }
      catch(CoreException e)
      {
         log.error("Could not adapt object", e);
      }
      
      return null;
   }

   /**
    * Check if a file is a SoapUI project.
    * @param file
    * @return
    */
   public static boolean isSoapuiProject(IFile file)
   {
      return file.getName().equals(SOAPUI_PROJECT_FILE);
   }

   public static Project findSoapuiProject(IFile file)
   {
      if(!isSoapuiProject(file))
         return null;
      
      IProject project = file.getProject();
      Project soapuiProject = AbstractProjectNature.getSoapUIProject(project);
      return soapuiProject;
   }

   // NOTE: This is not tested.
   public static IProject getEclipseProject(Project soapuiProject)
   {
      String projectName = soapuiProject.getName();
      IProject eclipseProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
      if(!eclipseProject.exists())
         return null;
         
      return eclipseProject;
   }
   
   // NOTE: This is not tested.
   public static IFile getProjectFile(Project soapuiProject)
   {
      IProject eclipseProject = getEclipseProject(soapuiProject);
      if(eclipseProject == null)
         return null;
      
      IFile projectFile = eclipseProject.getFile(SOAPUI_PROJECT_FILE);
      if(!projectFile.exists())
         return null;

      return projectFile;
   }
   
   /**
    * Returns a proxy to a ModelItem that also implements IAdaptable.
    */
   public static ModelItem getIAdaptableDecoratedModelItem(ModelItem modelItem)
   {
      return (ModelItem) Proxy.newProxyInstance( 
               Thread.currentThread().getContextClassLoader(), 
               new Class[]{ ModelItem.class, IAdaptable.class, ModelItemProxy.class }, 
               new ModelItemInvocationHandler( modelItem ) );
   }
   
   /**
    * Returns a proxy to a Workspace that also implements IAdaptable.
    */
   public static Workspace getIAdaptableDecoratedWorkspace(Workspace workspace)
   {
	   return (Workspace) Proxy.newProxyInstance( 
               Thread.currentThread().getContextClassLoader(), 
               new Class[]{ Workspace.class, IAdaptable.class, ModelItemProxy.class }, 
               new IAdaptableModelItemDecoratorInvocationHandler( workspace ) );
   }
   
   /**
    * Returns a proxy to a Project that also implements IAdaptable.
    */
   public static Project getIAdaptableDecoratedProject(Project project)
   {
	   return (Project) Proxy.newProxyInstance( 
               Thread.currentThread().getContextClassLoader(), 
               new Class[]{ Project.class, IAdaptable.class, ModelItemProxy.class }, 
               new IAdaptableModelItemDecoratorInvocationHandler( project ) );
   }
   
   /**
    * Returns a proxy to a Interface that also implements IAdaptable.
    */
   public static Interface getIAdaptableDecoratedInterface(Interface iface)
   {
	   return (Interface) Proxy.newProxyInstance( 
               Thread.currentThread().getContextClassLoader(), 
               new Class[]{ Interface.class, IAdaptable.class, ModelItemProxy.class }, 
               new IAdaptableModelItemDecoratorInvocationHandler( iface ) );
   }
   
   /**
    * Returns a proxy to a Operation that also implements IAdaptable.
    */
   public static Operation getIAdaptableDecoratedOperation(Operation op)
   {
	   return (Operation) Proxy.newProxyInstance( 
               Thread.currentThread().getContextClassLoader(), 
               new Class[]{ Operation.class, IAdaptable.class, ModelItemProxy.class }, 
               new IAdaptableModelItemDecoratorInvocationHandler( op ) );
   }
   
   /**
    * Returns a proxy to a Request that also implements IAdaptable.
    */
   public static Request getIAdaptableDecoratedRequest(Request op)
   {
	   return (Request) Proxy.newProxyInstance( 
               Thread.currentThread().getContextClassLoader(), 
               new Class[]{ Request.class, IAdaptable.class, ModelItemProxy.class }, 
               new IAdaptableModelItemDecoratorInvocationHandler( op ) );
   }
}
