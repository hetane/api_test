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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlImporter;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.support.UISupport;

public class AddWsdlToSoapUIProjectAction implements IObjectActionDelegate
{
   private ISelection currentSelection;

   /**
    * Constructor for Action1.
    */
   public AddWsdlToSoapUIProjectAction()
   {
      super();
   }

   /**
    * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
    */
   public void setActivePart(IAction action, IWorkbenchPart targetPart)
   {
   }

   /**
    * @see IActionDelegate#run(IAction)
    */
   public void run(IAction action)
   {
      if (!(currentSelection instanceof IStructuredSelection))
      {
         UISupport.showErrorMessage("Invalid selection type");
         return;
      }

      IStructuredSelection sel = (IStructuredSelection) currentSelection;

      Object firstElement = sel.getFirstElement();
      if (!(firstElement instanceof IFile))
      {
         UISupport.showErrorMessage("Selection must be a file");
         return;
      }

      Shell shell = new Shell();
      final IFile file = (IFile) firstElement;
  
      final Project project1 = AbstractProjectNature.getSoapUIProject(file.getProject());
      if(project1 == null)
         return;
      
      final WsdlProject project = (WsdlProject) project1;
      
      ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
      try
      {
         pmd.run(false, false, new IRunnableWithProgress()
         {
            public void run(IProgressMonitor monitor)
            {
               monitor.beginTask("Importing WSDL from [" + file.toString() + "]",
                     IProgressMonitor.UNKNOWN);
               try
               {
                  WsdlInterface[] iface = WsdlImporter.importWsdl(project, file.getLocationURI().toString());
                  project.save();
               }
               catch (Exception e)
               {
                  UISupport.showErrorMessage(e);
               }
            }
         });
      }
      catch (Exception e)
      {
         UISupport.showErrorMessage(e);
      }
   }

   /**
    * @see IActionDelegate#selectionChanged(IAction, ISelection)
    */
   public void selectionChanged(IAction action, ISelection selection)
   {
      currentSelection = selection;
   }

}
