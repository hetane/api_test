package com.eviware.soapui.impl.wsdl.submit.filters;

import static org.junit.Assert.assertEquals;

import org.apache.commons.httpclient.URIException;
import org.junit.Before;
import org.junit.Test;

import com.eviware.soapui.config.HttpRequestConfig;
import com.eviware.soapui.impl.support.http.HttpRequest;
import com.eviware.soapui.impl.support.http.HttpRequestInterface;
import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.impl.wsdl.submit.transports.http.BaseHttpRequestTransport;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedGetMethod;

public class HttpRequestFilterTestCase
{
	HttpRequestFilter filter = new HttpRequestFilter();
	HttpRequestInterface request;
	WsdlSubmitContext context;
	ExtendedGetMethod getMethod;

	@Before
	public void setup()
	{
		request = new HttpRequest( HttpRequestConfig.Factory.newInstance(), false );
		context = new WsdlSubmitContext( request );
		getMethod = new ExtendedGetMethod();
		context.setProperty( BaseHttpRequestTransport.HTTP_METHOD, getMethod );
	}

	@Test
	public void testSimpleEndpoint() throws URIException
	{
		request.setEndpoint( "http://www.soapui.org" );
		filter.filterRequest( context, request );
		assertEquals( "http://www.soapui.org", getMethod.getURI().toString() );
	}

	@Test
	public void testSimpleEndpointWithPath() throws URIException
	{
		request.setEndpoint( "http://www.soapui.org/testing/a/path" );
		filter.filterRequest( context, request );
		assertEquals( "http://www.soapui.org/testing/a/path", getMethod.getURI().toString() );
	}
}
