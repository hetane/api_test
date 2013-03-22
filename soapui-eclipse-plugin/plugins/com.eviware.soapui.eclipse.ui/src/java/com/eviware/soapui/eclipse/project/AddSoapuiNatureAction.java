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

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import com.eviware.soapui.support.types.StringToStringMap;

/**
 * 
 * @author Lars H
 */
public class AddSoapuiNatureAction extends AbstractAddProjectNatureAction
{
   // TODO Lägga till "JBossWS Libraries" i bygg-pathen innehållande de jar filer som behövs
   // (måste kollas upp..), detta måste skapas först som en User Library.
         
   // TODO Make a command to remove the nature again.
   
   public AddSoapuiNatureAction()
   {
      super(SoapuiProjectNature.SOAPUI_NATURE_ID);
   }

   @Override
   protected StringToStringMap getProjectSettings(IJavaProject javaProject)
   throws JavaModelException, IllegalStateException
   {
      // No settings for SoapUI projects (yet)
      return new StringToStringMap();
   }
}
