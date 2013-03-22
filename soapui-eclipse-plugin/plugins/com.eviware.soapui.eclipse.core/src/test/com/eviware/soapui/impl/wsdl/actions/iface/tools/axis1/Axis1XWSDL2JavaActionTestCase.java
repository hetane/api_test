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

package com.eviware.soapui.impl.wsdl.actions.iface.tools.axis1;

import junit.framework.TestCase;

import com.eviware.soapui.config.WsdlInterfaceConfig;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.XFormFactory;

/**
 * 
 * @author lars
 */
public class Axis1XWSDL2JavaActionTestCase extends TestCase
{
   public void setUp() throws Exception
   {
      super.setUp();
      XFormFactory.Factory.instance = new SwtOnlyFormFactory();
   }
   
   public void testBuildDialog() throws Exception
   {
      WsdlProject project = new WsdlProject();
      WsdlInterfaceConfig interfaceConfig = WsdlInterfaceConfig.Factory.newInstance();
      WsdlInterface iface = new WsdlInterface( project, interfaceConfig );
      Axis1XWSDL2JavaAction action = new Axis1XWSDL2JavaAction();
      XFormDialog dialog = action.buildDialog(iface);
      assertNotNull("dialog", dialog);
   }
}
