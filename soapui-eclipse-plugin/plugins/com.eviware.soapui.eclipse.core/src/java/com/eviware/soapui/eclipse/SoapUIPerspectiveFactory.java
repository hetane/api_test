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

package com.eviware.soapui.eclipse;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SoapUIPerspectiveFactory implements IPerspectiveFactory
{
	public void createInitialLayout( IPageLayout layout )
	{
		String editorArea = layout.getEditorArea();

		layout.addView("com.eviware.soapui.views.NavigatorView",
            IPageLayout.LEFT, 0.20f, editorArea);

		layout.addView("com.eviware.soapui.views.LogView",
            IPageLayout.BOTTOM, 0.80f, editorArea);
	}
}
