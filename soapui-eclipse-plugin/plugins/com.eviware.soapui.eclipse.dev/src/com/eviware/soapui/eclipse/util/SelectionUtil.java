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

package com.eviware.soapui.eclipse.util;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * 
 * @author Lars H
 */
public class SelectionUtil
{
	/**
	 * Find Java type from a selected Java type or Java file.
	 * 
	 * @param selection
	 * @return A type or null.
	 * @throws JavaModelException
	 */
	public static IType getSelectedType( ISelection selection ) throws JavaModelException
	{
		IType type = getSelectedElement( selection, IType.class );
		if( type != null )
		{
			return type;
		}

		ICompilationUnit javaFile = getSelectedElement( selection, ICompilationUnit.class );
		if( javaFile == null )
			return null;

		IType[] types = javaFile.getTypes();
		if( types != null && types.length > 0 )
		{
			return types[0];
		}

		return null;
	}

	public static IJavaProject getSelectedProject( ISelection selection )
	{
		return getSelectedElement( selection, IJavaProject.class );
	}

	/**
	 * Get the selected element of type T, if an instance of T is selected.
	 * 
	 * @param <T>
	 * @param selection
	 * @param ofClass
	 *           T.class
	 * @return An instance of T or null.
	 */
	@SuppressWarnings( "unchecked" )
	public static <T> T getSelectedElement( ISelection selection, Class<T> ofClass )
	{
		if( selection instanceof IStructuredSelection )
		{
			IStructuredSelection sel = ( IStructuredSelection )selection;
			Object element = sel.getFirstElement();
			if( ofClass.isInstance( element ) )
			{
				return ( T )element;
			}
		}
		return null;
	}
}
