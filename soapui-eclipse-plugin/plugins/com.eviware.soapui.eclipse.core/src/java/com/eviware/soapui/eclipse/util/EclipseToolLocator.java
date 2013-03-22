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

package com.eviware.soapui.eclipse.util;

import java.io.File;
import java.net.URL;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.core.AntCorePreferences;
import org.eclipse.ant.core.IAntClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;

import com.eviware.soapui.settings.ToolLocator;
import com.eviware.soapui.support.UISupport;

/**
 * 
 * @author Lars H
 */
public class EclipseToolLocator implements ToolLocator
{
   private static final String JAVA_ENVIRONMENT = "J2SE-1.5";

   public String getAntDir(boolean mandatory)
   {
      AntCorePreferences antPrefs = AntCorePlugin.getPlugin().getPreferences();
      String antHome = antPrefs.getAntHome();
      return antHome;
   }
   
   public String getJavacLocation(boolean mandatory)
   {
      IVMInstall vmInstall = getVMInstall();
      if(vmInstall == null)
      {
         if(mandatory)
         {
            UISupport.showErrorMessage( "Could not find an installed JDK for " + JAVA_ENVIRONMENT );
         }
         return null;
      }
      File dir = vmInstall.getInstallLocation();
      File bin = new File(dir, "bin");
//      File javac = new File(bin, "javac");
      return bin.getAbsolutePath();
   }
   
   private IVMInstall getVMInstall()
   {
      IExecutionEnvironment executionEnvironment =
         JavaRuntime.getExecutionEnvironmentsManager().getEnvironment(JAVA_ENVIRONMENT);
      for(IVMInstall vmInstall : executionEnvironment.getCompatibleVMs())
      {
         if(vmInstall.getName().indexOf("JDK") >= 0)
            return vmInstall;
      }

      return null;
   }
   
   // Perhaps we can use this in the future:
   private String getAntJar()
   {
      AntCorePreferences antPrefs = AntCorePlugin.getPlugin().getPreferences();
      for(IAntClasspathEntry entry : antPrefs.getAntHomeClasspathEntries())
      {
         String label = entry.getLabel();
         URL url = entry.getEntryURL();
         if(label.endsWith("ant.jar"))
         {
//          return entry.getEntryURL();
            return entry.getLabel();
         }
      }
      throw new RuntimeException("Could not find ant.jar");
   }
}
