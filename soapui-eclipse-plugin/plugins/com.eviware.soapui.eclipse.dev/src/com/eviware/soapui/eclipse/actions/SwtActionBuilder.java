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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.support.action.SoapUIAction;
import com.eviware.soapui.support.action.SoapUIActionGroup;
import com.eviware.soapui.support.action.SoapUIActionMapping;
import com.eviware.soapui.support.action.SoapUIActionRegistry.SeperatorAction;
import com.eviware.soapui.support.action.SoapUIActionRegistry.SoapUIActionGroupAction;

/**
 * 
 * @author lars
 */
public class SwtActionBuilder
{
	/**
	 * Creates a list of IAction and IContributionItems for the specified
	 * modelItem
	 */

	public static <T extends ModelItem> List buildActions( T modelItem )
	{
		Class<?> clazz = modelItem.getClass();
		List actionsAndContributionItems = buildActions( clazz.getSimpleName() + "Actions", modelItem );

		if( actionsAndContributionItems.isEmpty() )
		{
			clazz = clazz.getSuperclass();

			while( actionsAndContributionItems.isEmpty() && clazz != null && ModelItem.class.isAssignableFrom( clazz ) )
			{
				actionsAndContributionItems = buildActions( clazz.getSimpleName() + "Actions", modelItem );
				clazz = clazz.getSuperclass();
			}
		}

		return actionsAndContributionItems;
	}

	@SuppressWarnings( "hiding" )
	public static <T extends ModelItem> List buildActions( String actionGroup, T modelItem )
	{
		ArrayList actionsAndContributionItems = new ArrayList();

		SoapUIActionGroup<T> group = SoapUI.getActionRegistry().getActionGroup( actionGroup );
		if( group != null )
		{
			addActions( modelItem, actionsAndContributionItems, group );
		}

		return actionsAndContributionItems;
	}

	/**
	 * Adds the specified ActionMappings to the specified ActionList for the
	 * specified modelItem
	 */

	@SuppressWarnings( { "hiding", "unchecked" } )
	public static <T extends ModelItem> void addActions( T modelItem, List actionsAndContributionItems,
			SoapUIActionGroup<T> actionGroup )
	{
		boolean prevWasSeparator = false;
		List<SoapUIActionMapping<T>> mappings = actionGroup.getActionMappings( modelItem );
		for( SoapUIActionMapping<? extends ModelItem> mapping : mappings )
		{
			boolean isSeparator = false;
			SoapUIActionMapping<T> actionMapping = ( SoapUIActionMapping<T> )mapping;
			SoapUIAction<T> action = ( SoapUIAction<T> )mapping.getAction();

			if( !action.applies( modelItem ) )
			{
				System.out.println( action + " does not apply to " + modelItem );
			}
			else if( action instanceof SeperatorAction )
			{
				if( !prevWasSeparator )
				{
					IContributionItem separator = new Separator();
					actionsAndContributionItems.add( separator );
				}
				prevWasSeparator = true;
			}
			else if( action instanceof SoapUIActionGroupAction )
			{
				// Create a submenu.
				actionsAndContributionItems.add( action );
				prevWasSeparator = false;
			}
			else if( action != null )
			{
				SwtSoapuiAction actionDelegate = new SwtSoapuiAction( action, modelItem, mapping.getParam() );
				actionDelegate.setText( mapping.getName() );
				actionsAndContributionItems.add( actionDelegate );
				prevWasSeparator = false;
			}
		}
	}
}
