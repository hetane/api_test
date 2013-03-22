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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.eviware.soapui.eclipse.project.ModelItemProxy;
import com.eviware.soapui.model.ModelItem;

public class ModelItemInvocationHandler implements InvocationHandler
{
	protected final ModelItem target;

	public ModelItemInvocationHandler( ModelItem target )
	{
		this.target = target;
	}

	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		if( method.getDeclaringClass().equals( ModelItemProxy.class ) && method.getName().equals( "getModelItem" ) )
			return target;

		return method.invoke( target, args );
	}

}
