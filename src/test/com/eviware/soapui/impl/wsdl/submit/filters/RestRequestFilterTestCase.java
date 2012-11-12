package com.eviware.soapui.impl.wsdl.submit.filters;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.httpclient.URIException;
import org.apache.xmlbeans.XmlException;
import org.junit.Before;
import org.junit.Test;

import com.eviware.soapui.impl.rest.RestMethod;
import com.eviware.soapui.impl.rest.RestRequest;
import com.eviware.soapui.impl.rest.RestRequestInterface.RequestMethod;
import com.eviware.soapui.impl.rest.RestResource;
import com.eviware.soapui.impl.rest.RestService;
import com.eviware.soapui.impl.rest.RestServiceFactory;
import com.eviware.soapui.impl.rest.support.RestParamProperty;
import com.eviware.soapui.impl.rest.support.RestParamsPropertyHolder.ParameterStyle;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.impl.wsdl.submit.transports.http.BaseHttpRequestTransport;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedGetMethod;
import com.eviware.soapui.support.SoapUIException;

public class RestRequestFilterTestCase
{
	private RestRequestFilter filter = new RestRequestFilter();
	private RestRequest request;
	private WsdlSubmitContext context;
	private ExtendedGetMethod getMethod;
	private RestResource resource;
	private RestMethod method;
	private RestService service;

	@Before
	public void setup() throws XmlException, IOException, SoapUIException
	{
		WsdlProject project = new WsdlProject();
		service = ( RestService )project.addNewInterface( "Test", RestServiceFactory.REST_TYPE );
		resource = service.addNewResource( "test", "/test/path" );
		method = resource.addNewMethod( "GET" );
		method.setMethod( RequestMethod.GET );
		request = method.addNewRequest( "Test Request" );
		context = new WsdlSubmitContext( request );
		getMethod = new ExtendedGetMethod();
		context.setProperty( BaseHttpRequestTransport.HTTP_METHOD, getMethod );
		request.setEndpoint( "http://www.soapui.org" );
	}

	@Test
	public void testNoParameters() throws URIException
	{
		filter.filterRequest( context, request );
		assertEquals( "http://www.soapui.org/test/path", getMethod.getURI().toString() );
	}

	@Test
	public void testQueryParameters() throws URIException
	{
		// add simple parameter with value
		method.getParams().addProperty( "test" ).setStyle( ParameterStyle.QUERY );
		request.getParams().setPropertyValue( "test", "testing" );

		filter.filterRequest( context, request );
		assertEquals( "http://www.soapui.org/test/path?test=testing", getMethod.getURI().toString() );

		// add parameter without value
		method.getParams().addProperty( "test2" ).setStyle( ParameterStyle.QUERY );

		filter.filterRequest( context, request );
		assertEquals( "http://www.soapui.org/test/path?test=testing", getMethod.getURI().toString() );

		// add required parameter without value
		RestParamProperty param = method.getParams().addProperty( "test3" );
		param.setStyle( ParameterStyle.QUERY );
		param.setRequired( true );

		filter.filterRequest( context, request );
		assertEquals( "http://www.soapui.org/test/path?test=testing&test3=", getMethod.getURI().toString() );

		// test default value
		param.setDefaultValue( "tjoho" );
		filter.filterRequest( context, request );
		assertEquals( "http://www.soapui.org/test/path?test=testing&test3=tjoho", getMethod.getURI().toString() );

		// test encoding
		request.getParams().setPropertyValue( "test", "test ing" );
		request.getParams().setPropertyValue( "test3", "test%20ing" );

		filter.filterRequest( context, request );
		assertEquals( "http://www.soapui.org/test/path?test=test%20ing&test3=test%2520ing", getMethod.getURI().toString() );

		// test disabling of encoding for specific parameter
		param.setDisableUrlEncoding( true );
		filter.filterRequest( context, request );
		assertEquals( "http://www.soapui.org/test/path?test=test%20ing&test3=test%20ing", getMethod.getURI().toString() );

		// test multi value delimiter
		request.getParams().setPropertyValue( "test", "" );
		request.getParams().setPropertyValue( "test3", "1|2|3" );

		param.setDisableUrlEncoding( false );
		request.setMultiValueDelimiter( "\\|" );
		filter.filterRequest( context, request );
		assertEquals( "http://www.soapui.org/test/path?test3=1&test3=2&test3=3", getMethod.getURI().toString() );
	}
}
