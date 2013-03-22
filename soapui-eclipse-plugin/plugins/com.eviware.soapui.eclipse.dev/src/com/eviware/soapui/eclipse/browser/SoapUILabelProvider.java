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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.eviware.soapui.eclipse.Images;
import com.eviware.soapui.eclipse.project.SoapuiAdapterFactory;
import com.eviware.soapui.impl.rest.RestRequest;
import com.eviware.soapui.impl.wsdl.teststeps.PropertyTransfersTestStep;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlDelayTestStep;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlGotoTestStep;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlGroovyScriptTestStep;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlPropertiesTestStep;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.iface.Interface;
import com.eviware.soapui.model.iface.Operation;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.mock.MockOperation;
import com.eviware.soapui.model.mock.MockRequest;
import com.eviware.soapui.model.mock.MockResponse;
import com.eviware.soapui.model.mock.MockService;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.testsuite.LoadTest;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestSuite;
import com.eviware.soapui.security.SecurityTest;

/**
 * This class handles the presentation of SoapUI elements in Eclipse views.
 * 
 * @author Lars H
 */
public class SoapUILabelProvider extends LabelProvider implements ITableLabelProvider
{
	private static final String prefix = "/com/eviware/soapui/resources/images/";

	public String getColumnText( Object element, int index )
	{
		return getText( element );
	}

	public String getText( Object element )
	{
		if( element instanceof ModelItem )
			return ( ( ModelItem )element ).getName();
		else
			return super.getText( element );
	}

	public Image getColumnImage( Object obj, int index )
	{
		return getImage( obj );
	}

	public Image getImage( Object obj )
	{
		if( obj instanceof ModelItem )
		{
			Image image = Images.getImage( ( ( ModelItem )obj ).getIcon() );
			if( image != null )
				return image;
		}

		String imageFile = prefix + getImageName( obj );

		if( imageFile != null )
			return Images.getImage( imageFile );

		return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_ELEMENT );
	}

	public static Image getImageForModelItem( ModelItem obj )
	{
		Image image = Images.getImage( obj.getIcon() );
		if( image != null )
			return image;

		String imageFile = prefix + getImageName( obj );
		if( imageFile != null )
			return Images.getImage( imageFile );

		return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_ELEMENT );
	}

	private static String getImageName( Object obj )
	{
		if( obj instanceof Project || ( obj instanceof IFile && SoapuiAdapterFactory.isSoapuiProject( ( IFile )obj ) ) )
			return "project.gif";
		if( obj instanceof Interface )
			return "interface.gif";
		if( obj instanceof Operation )
			return "operation.gif";
		if( obj instanceof RestRequest )
			return "rest_request.gif";
		if( obj instanceof Request )
			return "request.gif";
		if( obj instanceof TestSuite )
			return "testSuite.gif";
		if( obj instanceof TestCase )
			return "testCase.gif";
		if( obj instanceof TestStepsElement )
			return "teststeps.gif";
		if( obj instanceof MockService )
			return "mockService.gif";
		if( obj instanceof MockOperation )
			return "mockOperation.gif";
		if( obj instanceof MockRequest )
			return "mockRequest.gif";
		if( obj instanceof MockResponse )
			return "mockResponse.gif";
		if( obj instanceof PropertyTransfersTestStep )
			return "value_transfer.gif";
		if( obj instanceof WsdlDelayTestStep )
			return "wait.gif";
		if( obj instanceof WsdlGotoTestStep )
			return "goto.gif";
		if( obj instanceof WsdlGroovyScriptTestStep )
			return "groovy_script.gif";
		if( obj instanceof WsdlPropertiesTestStep )
			return "properties_step.gif";
		if( obj instanceof WsdlTestRequestStep )
			return "request.gif";
		if( obj instanceof LoadTestsElement )
			return "loadtests.gif";
		if( obj instanceof LoadTest )
			return "loadTest.gif";
		if( obj instanceof SecurityTestsElement )
			return "securityTest.png";
		if( obj instanceof SecurityTest )
			return "securityTest.png";
		return null;
	}
}
