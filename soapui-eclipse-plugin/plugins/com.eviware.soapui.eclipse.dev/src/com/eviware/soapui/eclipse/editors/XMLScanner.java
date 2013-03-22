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
import org.eclipse.jface.text.*;

public class XMLScanner extends RuleBasedScanner
{

	public XMLScanner( ColorManager manager )
	{
		IToken procInstr = new Token( new TextAttribute( manager.getColor( IXMLColorConstants.PROC_INSTR ) ) );

		IRule[] rules = new IRule[2];
		// Add rule for processing instructions
		rules[0] = new SingleLineRule( "<?", "?>", procInstr );
		// Add generic whitespace rule.
		rules[1] = new WhitespaceRule( new XMLWhitespaceDetector() );

		setRules( rules );
	}
}
