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

package com.eviware.soapui.eclipse.prefs;

import java.net.URL;
import java.util.Properties;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.eclipse.Images;
import com.eviware.soapui.support.UISupport;

/**
 * 
 * @author Lars H
 */
public class MainPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
   public MainPreferencePage()
   {
   }

   public void init(IWorkbench workbench)
   {
   }

   protected Control createContents(Composite parent)
   {
      noDefaultAndApplyButton();
      
      URL splashURI = UISupport.findSplash( SoapUI.SOAPUI_SPLASH );

      Properties props = new Properties();
      try
      {
         props.load( SoapUI.class.getResourceAsStream( SoapUI.BUILDINFO_RESOURCE ) );
      }
      catch( Exception e1 )
      {
         SoapUI.logError( e1 );
      }
      
      Image splash = Images.getImage(splashURI.getFile());
      Composite base = new Composite(parent, SWT.NONE);
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      gridLayout.makeColumnsEqualWidth = true;
      base.setLayout(gridLayout);
      GridData layout = new GridData(GridData.FILL_BOTH);
      layout.grabExcessHorizontalSpace = true;
      base.setLayoutData(layout);
      Label label = new Label(base, SWT.NONE);
      label.setImage(splash);
      layout = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
      label.setLayoutData(layout);
      Browser browser = new Browser(base, SWT.NONE);
      browser.setText("<html><body><p align=center>soapUI " +
              SoapUI.SOAPUI_VERSION + ", copyright (C) 2004-2009 eviware.com<br>" +
              "<a href=\"http://www.soapui.org\" target=\"_new\">http://www.soapui.org</a> | " +
              "<a href=\"http://www.eviware.com\" target=\"_new\">http://www.eviware.com</a><br>" +
              "Build " + props.getProperty( "build.number" ) + ", Build Date " +
              props.getProperty( "build.date" ) + "</p></body></html>");
      layout = new GridData(GridData.FILL_BOTH);
      layout.grabExcessHorizontalSpace = true;
      layout.grabExcessVerticalSpace = true;
      browser.setLayoutData(layout);
      return base;
   }
}
