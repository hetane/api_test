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

package com.eviware.soapui.eclipse.actions;

import com.eviware.soapui.impl.actions.ImportWsdlProjectAction;
import com.eviware.soapui.impl.actions.NewWsdlProjectAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.axis1.Axis1XWSDL2JavaAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.axis2.Axis2WSDL2CodeAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.jaxb.JaxbXjcAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.tcpmon.TcpMonAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.wscompile.WSCompileAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.wsimport.WSImportAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.xfire.XFireAction;
import com.eviware.soapui.impl.wsdl.actions.iface.tools.xmlbeans.XmlBeans2Action;
import com.eviware.soapui.impl.wsdl.actions.project.AddInterfaceActionFromFile;
import com.eviware.soapui.impl.wsdl.actions.project.AddInterfaceActionFromURL;
import com.eviware.soapui.impl.wsdl.actions.project.RemoveProjectAction;
import com.eviware.soapui.impl.wsdl.actions.project.RenameProjectAction;
import com.eviware.soapui.impl.wsdl.actions.project.SaveProjectAction;
import com.eviware.soapui.impl.wsdl.actions.project.SaveProjectAsAction;
import com.eviware.soapui.support.action.swing.SwingActionDelegate;

/**
 * 
 * @author Lars H
 */
public class EclipseActions
{
	public static boolean actionIsImplemented(Object swingAction)
	{
		if (swingAction instanceof SwingActionDelegate)
		{
			SwingActionDelegate delegate = (SwingActionDelegate) swingAction;
			swingAction = delegate.getAction();
		}

		return
		// WorkspaceImpl actions:
		swingAction instanceof NewWsdlProjectAction
				|| swingAction instanceof ImportWsdlProjectAction
				||

				// Project actions:
				// Moved to plugin.xml:
				swingAction instanceof AddInterfaceActionFromURL
				|| swingAction instanceof AddInterfaceActionFromFile
				||

				// swingAction instanceof WSToolsJava2WsdlAction ||
				// swingAction instanceof AddNewTestSuiteAction ||
				swingAction instanceof RenameProjectAction
				|| swingAction instanceof RemoveProjectAction
				||
				// Moved to plugin.xml:
				swingAction instanceof SaveProjectAction
				|| swingAction instanceof SaveProjectAsAction
				||

				// Interface actions:
				// TODO Eclipse: Implement WSI actions:
				// swingAction instanceof WSToolsRegenerateJava2WsdlAction ||
				// swingAction instanceof WSIAnalyzeAction ||
				swingAction instanceof TcpMonAction
				||
				// TODO Eclipse: Port InterfaceEndpointsAction to SWT:
				// swingAction instanceof InterfaceEndpointsAction ||

				// Moved to plugin.xml:
				// swingAction instanceof UpdateInterfaceAction ||
				// swingAction instanceof ExportDefinitionAction ||
				// swingAction instanceof RemoveInterfaceAction ||

				// Moved to plugin.xml:
				// // Operation actions:
				// swingAction instanceof NewRequestAction ||
				// swingAction instanceof RelabelOperationAction ||
				//   
				// // Request actions:
				// swingAction instanceof CloneRequestAction ||
				// swingAction instanceof RenameRequestAction ||
				// swingAction instanceof DeleteRequestAction ||
				//      
				// // Common actions:
				// swingAction instanceof ShowOnlineHelpAction ||

				// Generate actions:
				// swingAction instanceof new WSToolsWsdl2JavaAction ||
				swingAction instanceof WSCompileAction || swingAction instanceof WSImportAction
				|| swingAction instanceof Axis1XWSDL2JavaAction || swingAction instanceof Axis2WSDL2CodeAction
				|| swingAction instanceof XFireAction || swingAction instanceof JaxbXjcAction
				|| swingAction instanceof XmlBeans2Action;
	}
}
