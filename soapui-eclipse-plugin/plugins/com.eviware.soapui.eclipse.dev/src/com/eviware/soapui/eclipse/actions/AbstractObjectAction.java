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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.eviware.soapui.eclipse.SoapUIActivator;
import com.eviware.soapui.eclipse.util.SelectionUtil;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.support.UISupport;

/**
 * 
 * @author Lars H
 */
public abstract class AbstractObjectAction<T extends ModelItem> implements IObjectActionDelegate
{
	private final Class<T> elementClass;

	private IWorkbenchPart activePart;

	private T selectedElement;

	public AbstractObjectAction( Class<T> elementClass )
	{
		if( elementClass == null )
			throw new IllegalArgumentException( "elementClass is null" );
		this.elementClass = elementClass;
	}

	public void setActivePart( IAction action, IWorkbenchPart targetPart )
	{
		activePart = targetPart;
	}

	public void selectionChanged( IAction action, ISelection selection )
	{
		T element = SelectionUtil.getSelectedElement( selection, elementClass );
		setSelection( element );
		if( element == null )
			action.setEnabled( false );
	}

	public void setSelection( T element )
	{
		selectedElement = element;
	}

	public void run( IAction action )
	{
		try
		{
			run( activePart, selectedElement );
		}
		catch( Exception e )
		{
			SoapUIActivator.logError( getClass().getName() + " failed", e );
			UISupport.showErrorMessage( e );
			e.printStackTrace();
		}
	}

	public abstract void run( IWorkbenchPart activePart, T selectedElement ) throws Exception;
}
