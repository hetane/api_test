/*
 *  SoapUI, copyright (C) 2004-2012 smartbear.com
 *
 *  SoapUI is free software; you can redistribute it and/or modify it under the
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  SoapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.tools;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eviware.soapui.DefaultSoapUICore;
import com.eviware.soapui.SoapUI;
import com.eviware.soapui.SoapUICore;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.mock.DispatchException;
import com.eviware.soapui.impl.wsdl.mock.WsdlMockRunner;
import com.eviware.soapui.impl.wsdl.mock.WsdlMockService;
import com.eviware.soapui.model.mock.MockRunner;
import com.eviware.soapui.model.mock.MockService;

/**
 * @author ole
 */
public class SoapUIMockServlet extends HttpServlet
{
	private MockRunner mockRunner;
	private MockService mockService;
	private WsdlProject project;
	private static Logger logger = Logger.getLogger( SoapUIMockServlet.class.getName() );

	@Override
	public void init() throws ServletException
	{
		super.init();
		try
		{
			logger.info( "Initializing SoapUI Core" );
			SoapUI.setSoapUICore(
					createSoapUICore( getInitParameter( "settingsFile" ), getInitParameter( "settingsPassword" ) ), true );

			logger.info( "Loading project" );
			project = new WsdlProject( getInitParameter( "projectFile" ), getInitParameter( "projectPassword" ) );

			logger.info( "Starting MockService" );

			logger.log( Level.SEVERE, "Starting mock service" );

			logger.log( Level.SEVERE, " Mock service count"+ project.getMockServiceCount() );

			logger.log( Level.SEVERE, " REST Mock service count"+ project.getRestMockServiceCount() );

			if(project.getMockServiceCount() > 0)
			{
				mockService = project.getMockServiceByName( getInitParameter( "mockService" ) );

				logger.log( Level.SEVERE, " Actual soap Mock service "+mockService.getName().toString() );
			}
			else if(project.getRestMockServiceCount() > 0)
			{
				mockService = project.getRestMockServiceByName( getInitParameter( "restMockService" ) );

				logger.log( Level.SEVERE, " Actual rest Mock service " + mockService.getName().toString() );
			}

			mockRunner = mockService.start();
		}
		catch( Exception ex )
		{
			logger.log( Level.SEVERE, null, ex );
		}
	}

	@Override
	protected void service( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
			IOException
	{
		try
		{
			mockRunner.dispatchRequest( request, response );
		}
		catch( DispatchException ex )
		{
			logger.log( Level.SEVERE, null, ex );
		}
	}

	/**
	 * Returns a short description of the servlet.
	 */
	public String getServletInfo()
	{
		return mockService.getName();
	}

	// </editor-fold>

	protected SoapUICore createSoapUICore( String settingsFile, String soapUISettingsPassword )
	{
		return new DefaultSoapUICore( null, settingsFile, soapUISettingsPassword );
	}
}
