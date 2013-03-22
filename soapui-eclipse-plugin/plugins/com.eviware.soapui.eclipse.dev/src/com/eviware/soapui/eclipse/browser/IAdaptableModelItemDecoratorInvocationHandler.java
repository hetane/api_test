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

import java.lang.reflect.Method;

import com.eviware.soapui.eclipse.project.SoapuiAdapterFactory;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.iface.Interface;
import com.eviware.soapui.model.iface.Operation;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.project.Project;

public class IAdaptableModelItemDecoratorInvocationHandler extends ModelItemInvocationHandler
{

	private SoapuiAdapterFactory soapuiAdapterFactory = new SoapuiAdapterFactory();

	public IAdaptableModelItemDecoratorInvocationHandler( ModelItem target )
	{
		super( target );
	}

	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		Object returnObj;

		if( method.getName().equals( "getAdapter" ) )
		{
			returnObj = soapuiAdapterFactory.getAdapter( target, ( Class )args[0] );
		}
		else
		{
			returnObj = super.invoke( proxy, method, args );
		}

		if( method.getName().equals( "getProjectAt" ) )
		{
			returnObj = SoapuiAdapterFactory.getIAdaptableDecoratedProject( ( Project )returnObj );
		}
		else if( method.getName().equals( "getInterfaceAt" ) )
		{
			returnObj = SoapuiAdapterFactory.getIAdaptableDecoratedInterface( ( Interface )returnObj );
		}
		else if( method.getName().equals( "getOperationAt" ) )
		{
			returnObj = SoapuiAdapterFactory.getIAdaptableDecoratedOperation( ( Operation )returnObj );
		}
		else if( method.getName().equals( "getRequestAt" ) )
		{
			returnObj = SoapuiAdapterFactory.getIAdaptableDecoratedRequest( ( Request )returnObj );
		}

		return returnObj;
	}

}
