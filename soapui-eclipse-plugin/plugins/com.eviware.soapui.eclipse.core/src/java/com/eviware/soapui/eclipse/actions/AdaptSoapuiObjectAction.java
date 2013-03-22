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

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPart;

import com.eviware.soapui.model.ModelItem;

/**
 * 
 * @author lars
 */
public class AdaptSoapuiObjectAction<T extends ModelItem> extends AbstractObjectAction<T>
{
   private final Class actionClass;

   public AdaptSoapuiObjectAction(Class<T> elementClass, Class actionClass)
   {
      super(elementClass);
      this.actionClass = actionClass;
   }
   
   @Override
   public void run(IWorkbenchPart activePart, T selectedElement) throws Exception
   {
      List actionsAndContributionItems = SwtActionBuilder.buildActions( selectedElement );
      Action action = findAction( actionsAndContributionItems );
      if( action != null )
      	executeAction(selectedElement, action);
   }

	private Action findAction(List actionsAndContributionItems) throws Exception
	{
		for(Object element : actionsAndContributionItems)
      {
//         if( action instanceof ActionListAction )
//         {
//         	action = findAction( ((ActionListAction)action).getActionList() );
//         	if( action != null )
//         		return action;
//         }
//         else
			//if(element instanceof Action && element.getClass() == actionClass)
		   if(element instanceof Action && element.getClass() == actionClass || element instanceof SwtSoapuiAction && ((SwtSoapuiAction)element).getAction().getClass() == actionClass)
         {
            Action action = (Action) element;
            return action.isEnabled() ? action : null;
         }
      }
		
		return null;
	}
   
   // Give subclasses a chance to modify behavior.
   protected void executeAction(T selectedElement, Action action) throws Exception
   {
      action.run();
   }
}
