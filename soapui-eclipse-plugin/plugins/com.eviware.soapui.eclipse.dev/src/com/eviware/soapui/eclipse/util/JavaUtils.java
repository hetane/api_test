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

import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

/**
 * 
 * @author Lars H
 */
public class JavaUtils
{
	/**
	 * Get all source directories that are configured for a project.
	 * 
	 * @param javaProject
	 * @return
	 * @throws JavaModelException
	 */
	public static String[] getSourceDirectories( IJavaProject javaProject ) throws JavaModelException
	{
		ArrayList<String> srcDirs = new ArrayList<String>();
		String projectName = javaProject.getProject().getName();

		for( IPackageFragmentRoot pfRoot : javaProject.getPackageFragmentRoots() )
		{
			if( !pfRoot.isArchive() )
			{
				String dirName = pfRoot.getPath().toString();
				int ix = dirName.indexOf( projectName + "/" );

				if( ix >= 0 )
				{
					dirName = dirName.substring( ix + projectName.length() + 1 );
				}

				srcDirs.add( dirName );
			}
		}
		return srcDirs.toArray( new String[srcDirs.size()] );
	}

	public static IClasspathEntry findClasspathEntry( IType type ) throws JavaModelException
	{
		IPackageFragmentRoot pfRoot = ( IPackageFragmentRoot )type.getPackageFragment().getParent();
		String srcDir = pfRoot.getElementName();

		for( IClasspathEntry cpEntry : type.getJavaProject().getResolvedClasspath( true ) )
		{
			if( cpEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE )
			{
				IPath cpEntryPath = cpEntry.getPath();
				if( srcDir.equals( lastDir( cpEntryPath ) ) )
				{
					return cpEntry;
				}
			}
		}

		return null;
	}

	/**
	 * Get the classpath to the selected class (the compiled class) relative to
	 * the project. TODO Eclipse wstools: The user must build the project before
	 * this command.
	 * 
	 * @return The name of the output directory for the selected class.
	 * @throws JavaModelException
	 * @throws RuntimeException
	 */
	public static String getClassPath( IType type ) throws JavaModelException, RuntimeException
	{
		IClasspathEntry cpEntry = JavaUtils.findClasspathEntry( type );
		if( cpEntry != null )
		{
			IPath cpEntryOutputPath = cpEntry.getOutputLocation();
			if( cpEntryOutputPath != null )
			{
				return lastDir( cpEntryOutputPath );
			}
		}

		IPath path = type.getJavaProject().getOutputLocation();
		return lastDir( path );
	}

	/**
	 * Get the name of all interfaces that a class implements.
	 * 
	 * @param type
	 * @param monitor
	 * @return
	 * @throws JavaModelException
	 */
	public static String[] getInterfaceNames( IType type, IProgressMonitor monitor ) throws JavaModelException
	{
		ArrayList<String> interfaceNames = new ArrayList<String>();
		ITypeHierarchy superTypes = type.newSupertypeHierarchy( monitor );
		IType[] superInterfaces = superTypes.getAllInterfaces();

		for( int i = 0; i < superInterfaces.length; i++ )
		{
			String interfaceName = superInterfaces[i].getFullyQualifiedName();
			if( !"java.rmi.Remote".equals( interfaceName ) )
			{
				interfaceNames.add( interfaceName );
			}
		}
		return interfaceNames.toArray( new String[interfaceNames.size()] );
	}

	public static String lastDir( IPath path )
	{
		String s = path.toPortableString();
		int separator = s.indexOf( '/', 1 );
		return separator >= 0 ? s.substring( separator + 1 ) : s;
	}

	public static String getPackageName( String fullName, String className )
	{
		if( fullName.length() > ( "." + className ).length() )
		{
			return fullName.substring( 0, fullName.length() - ( "." + className ).length() );
		}
		else
		{
			return "";
		}
	}
}
