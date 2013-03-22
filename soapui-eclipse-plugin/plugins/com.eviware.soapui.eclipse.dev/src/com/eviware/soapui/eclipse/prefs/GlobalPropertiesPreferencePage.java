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

package com.eviware.soapui.eclipse.prefs;

import org.eclipse.swt.layout.GridData;

import com.eviware.soapui.actions.GlobalPropertiesPrefs;
import com.eviware.soapui.eclipse.forms.FormBuilder;
import com.eviware.soapui.impl.wsdl.panels.teststeps.support.PropertyHolderTable;
import com.eviware.soapui.model.propertyexpansion.PropertyExpansionUtils;

/**
 * 
 * @author Dain Nilsson
 */
public class GlobalPropertiesPreferencePage extends AbstractPreferencePage<GlobalPropertiesPrefs>
{
	public GlobalPropertiesPreferencePage()
	{
		super( new GlobalPropertiesPrefs() );
	}

	protected void buildForm( FormBuilder globalPropertiesForm )
	{
		GridData layout = new GridData( GridData.FILL_BOTH );
		globalPropertiesForm.appendAwt( new PropertyHolderTable( PropertyExpansionUtils.getGlobalProperties() ), layout );
		globalPropertiesForm.appendCheckBox( GlobalPropertiesPrefs.ENABLE_OVERRIDE,
				"Enables overriding of any property-reference with global properties", false );

	}
}
