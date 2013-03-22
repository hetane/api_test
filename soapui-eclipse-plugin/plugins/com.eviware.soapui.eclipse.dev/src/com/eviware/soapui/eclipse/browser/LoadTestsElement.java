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

import com.eviware.soapui.model.testsuite.TestCase;

/**
 * 
 * @author Lars H
 */
public class LoadTestsElement
{
	private final TestCase parent;

	public LoadTestsElement( TestCase parent )
	{
		if( parent == null )
			throw new IllegalArgumentException( "parent is null" );
		this.parent = parent;
	}

	public String toString()
	{
		return "Load Tests";
	}

	public TestCase getTestCase()
	{
		return parent;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		return PRIME * parent.hashCode();
	}

	@Override
	public boolean equals( Object obj )
	{
		return obj instanceof LoadTestsElement && parent.equals( ( ( LoadTestsElement )obj ).parent );
	}
}
