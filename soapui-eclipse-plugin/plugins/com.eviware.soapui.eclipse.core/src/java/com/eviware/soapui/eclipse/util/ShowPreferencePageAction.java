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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;

import com.eviware.soapui.eclipse.SoapUIActivator;

@SuppressWarnings("serial")
public class ShowPreferencePageAction extends AbstractAction
{
	private final Class<? extends IPreferencePage> preferencePageClass;

	public ShowPreferencePageAction( String label, Class<? extends IPreferencePage> preferencePageClass )
	{
		super( label );
		this.preferencePageClass = preferencePageClass;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			IPreferencePage preferencePage = preferencePageClass.newInstance();
			
			PreferenceManager mgr = new PreferenceManager();
			PreferenceNode one = new PreferenceNode( preferencePage.getTitle(), preferencePage );

			mgr.addToRoot(one);

			PreferenceDialog dialog = new PreferenceDialog(null, mgr);
         
         IPreferenceStore ps = null;
         SoapUIActivator activator = SoapUIActivator.getDefault();
         if(activator != null)
         {
            ps = activator.getPreferenceStore();
            dialog.setPreferenceStore(ps);
         }
			dialog.open();
			
			if (ps instanceof PreferenceStore)
				((PreferenceStore) ps).save();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
