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

package com.eviware.soapui.eclipse.editors;

import org.eclipse.jface.text.rules.*;

public class TagRule extends MultiLineRule
{

	public TagRule( IToken token )
	{
		super( "<", ">", token );
	}

	protected boolean sequenceDetected( ICharacterScanner scanner, char[] sequence, boolean eofAllowed )
	{
		int c = scanner.read();
		if( sequence[0] == '<' )
		{
			if( c == '?' )
			{
				// processing instruction - abort
				scanner.unread();
				return false;
			}
			if( c == '!' )
			{
				scanner.unread();
				// comment - abort
				return false;
			}
		}
		else if( sequence[0] == '>' )
		{
			scanner.unread();
		}
		return super.sequenceDetected( scanner, sequence, eofAllowed );
	}
}
