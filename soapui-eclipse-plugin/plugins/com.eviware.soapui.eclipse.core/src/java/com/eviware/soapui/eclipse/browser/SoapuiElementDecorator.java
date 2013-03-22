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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.eviware.soapui.eclipse.Images;
import com.eviware.soapui.eclipse.project.AbstractProjectNature;
import com.eviware.soapui.eclipse.project.SoapuiAdapterFactory;

/**
 * 
 * @author Lars H
 */
public class SoapuiElementDecorator implements ILabelDecorator
{
   private final static Logger log = Logger.getLogger( SoapuiElementDecorator.class );

   public Image decorateImage(Image image, Object element)
   {
      if(element instanceof IFile)
      {
         IFile file = (IFile) element;
         if(SoapuiAdapterFactory.isSoapuiProject(file))
            return Images.getImage("/com/eviware/soapui/resources/images/project.gif");
      }
      
      return image;
   }

   public String decorateText(String text, Object element)
   {
      if(element instanceof IFile)
      {
         IFile file = (IFile) element;
         if(SoapuiAdapterFactory.isSoapuiProject(file))
         {
            try
            {
               IProject project = file.getProject();
               if(project.hasNature(AbstractProjectNature.JBOSSWS_NATURE_ID))
                  return "JBossWS Web Services";
               else if(project.hasNature(AbstractProjectNature.SOAPUI_NATURE_ID))
                  return "SoapUI Web Services";
               else 
               	return text;
            }
            catch(CoreException e)
            {
               log.warn("Could not check nature", e);
            }
         }
      }

      return text;
   }

   public void addListener(ILabelProviderListener listener)
   {
   }

   public void removeListener(ILabelProviderListener listener)
   {
   }

   public void dispose()
   {
   }

   public boolean isLabelProperty(Object element, String property)
   {
      return false;
   }
}
