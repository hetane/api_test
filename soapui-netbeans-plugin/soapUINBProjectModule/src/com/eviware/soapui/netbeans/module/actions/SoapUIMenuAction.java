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
package com.eviware.soapui.netbeans.module.actions;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.actions.SoapUIPreferencesAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.axis1.Axis1XWSDL2JavaAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.axis2.Axis2WSDL2CodeAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.cxf.CXFAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.jaxb.JaxbXjcAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.jbossws.JBossWSConsumeAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.jbossws.WSToolsWsdl2JavaAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.oracle.OracleWsaGenProxyAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.tcpmon.TcpMonAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.wscompile.WSCompileAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.wsimport.WSImportAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.xfire.XFireAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.xmlbeans.XmlBeans2Action;
import com.eviware.soapui.impl.support.actions.ShowOnlineHelpAction;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.swing.SwingActionDelegate;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class SoapUIMenuAction extends CallableSystemAction
{
   public void performAction ()
   {
      // TODO implement action body
   }
   
   public String getName ()
   {
      return NbBundle.getMessage (SoapUIMenuAction.class, "CTL_SoapUIMenuAction");
   }
   
   @Override
   protected void initialize ()
   {
      super.initialize ();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue ("noIconInMenu", Boolean.TRUE);
   }
   
   public HelpCtx getHelpCtx ()
   {
      return HelpCtx.DEFAULT_HELP;
   }
   
   @Override
   protected boolean asynchronous ()
   {
      return false;
   }

   @Override
   public JMenuItem getMenuPresenter ()
   {
      JMenu menu = new JMenu( "soapUI" );
      
      SoapUIPreferencesAction.getInstance().putValue( Action.ACCELERATOR_KEY, null );
      menu.add ( SoapUIPreferencesAction.getInstance () );
      menu.addSeparator ();
      menu.add ( buildGenerateMenu() );
      menu.add( SwingActionDelegate.createDelegate( TcpMonAction.SOAPUI_ACTION_ID ));
      menu.addSeparator ();
      menu.add( new ShowOnlineHelpAction( NbBundle.getMessage (SoapUIMenuAction.class, "OnlineHelpMenuItemName"),
              "http://www.soapui.org/IDE-Plugins/netbean.html" ));
      menu.add( new ShowOnlineHelpAction( "soapui.org", "http://www.soapui.org"));
      menu.addSeparator ();
      menu.add( new ShowAboutAction());
      return menu;
   }
   
   private JMenu buildGenerateMenu() 
   {
      JMenu toolsMenu = new JMenu( NbBundle.getMessage (SoapUIMenuAction.class, "GenerateMenuName") );
  
      toolsMenu.add( SwingActionDelegate.createDelegate( WSToolsWsdl2JavaAction.SOAPUI_ACTION_ID ));
      toolsMenu.add( SwingActionDelegate.createDelegate( JBossWSConsumeAction.SOAPUI_ACTION_ID ));
      toolsMenu.addSeparator();
      toolsMenu.add( SwingActionDelegate.createDelegate( WSCompileAction.SOAPUI_ACTION_ID ));
      toolsMenu.add( SwingActionDelegate.createDelegate( WSImportAction.SOAPUI_ACTION_ID ));
      toolsMenu.addSeparator();
      toolsMenu.add( SwingActionDelegate.createDelegate( Axis1XWSDL2JavaAction.SOAPUI_ACTION_ID ));
      toolsMenu.add( SwingActionDelegate.createDelegate( Axis2WSDL2CodeAction.SOAPUI_ACTION_ID ));
      toolsMenu.add( SwingActionDelegate.createDelegate( CXFAction.SOAPUI_ACTION_ID ));
      toolsMenu.add( SwingActionDelegate.createDelegate( XFireAction.SOAPUI_ACTION_ID ));
      toolsMenu.add( SwingActionDelegate.createDelegate( OracleWsaGenProxyAction.SOAPUI_ACTION_ID ));
      toolsMenu.addSeparator();
      toolsMenu.add( SwingActionDelegate.createDelegate( XmlBeans2Action.SOAPUI_ACTION_ID ));
      toolsMenu.add( SwingActionDelegate.createDelegate( JaxbXjcAction.SOAPUI_ACTION_ID ));

      return toolsMenu;
   }
   
   private static class ShowAboutAction extends AbstractAction
	{
		public ShowAboutAction()
		{
			super( NbBundle.getMessage (SoapUIMenuAction.class, "AboutActionName"));
			putValue( SHORT_DESCRIPTION, NbBundle.getMessage (SoapUIMenuAction.class, "AboutActionDescription") );
		}

		public void actionPerformed( ActionEvent e )
		{
			URI splashURI = null;
			try
			{
				splashURI = UISupport.findSplash( SoapUI.SOAPUI_SPLASH ).toURI();
			}
			catch( URISyntaxException e1 )
			{
				SoapUI.logError( e1 );
			}
			
			Properties props = new Properties();
			try
			{
				props.load( SoapUI.class.getResourceAsStream( SoapUI.BUILDINFO_RESOURCE ) );
			}
			catch( Exception e1 )
			{
				SoapUI.logError( e1 );
			}
			
			
			UISupport.showExtendedInfo( NbBundle.getMessage (SoapUIMenuAction.class, "AboutDialogTitle"), null, 
						"<html><body><p align=center><img src=\"" + splashURI + "\"><br>soapUI " +
						SoapUI.SOAPUI_VERSION + " NetBeans Plug-in, copyright (C) 2004-2011 smartbear.com<br>" +
						"<a href=\"http://www.soapui.org\">http://www.soapui.org</a> | " +
						"<a href=\"http://www.eviware.com\">http://www.eviware.com</a><br>" +
									"Build " + props.getProperty( "build.number" ) + ", Build Date " +
									props.getProperty( "build.date" ) + "</p></body></html>",
									
						new Dimension( 470, 360 ));
		}
	}
}
