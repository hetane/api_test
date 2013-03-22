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

import org.eclipse.jface.action.Action;

import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.support.action.SoapUIAction;
import com.eviware.soapui.eclipse.project.ProjectProxy;

/**
 * 
 * @author Lars
 */
public class SwtSoapuiAction<T extends ModelItem> extends Action
{
	private SoapUIAction<T> action;
	private T target;
	private Object param;

	public SwtSoapuiAction( SoapUIAction<T> action, T target )
	{
		this( action, target, null );
	}

	public SwtSoapuiAction( SoapUIAction<T> action, T target, Object param )
	{
		this.action = action;
		this.target = target;
		this.param = param;
		setText( action.getName() );
		setToolTipText( action.getDescription() );
	}

	public SoapUIAction<T> getAction()
	{
		return action;
	}

	@Override
	public void run()
	{
		if( target instanceof ProjectProxy )
		{
			action.perform( ( T )( ( ProjectProxy )target ).getProject(), param );
		}
		else
		{
			action.perform( target, param );
		}
	}
}
