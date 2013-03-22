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

import junit.framework.TestCase;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.SwingSoapUICore;
import com.eviware.soapui.impl.actions.NewWorkspaceAction;
import com.eviware.soapui.impl.actions.RenameWorkspaceAction;
import com.eviware.soapui.impl.actions.SwitchWorkspaceAction;
import com.eviware.soapui.support.action.SoapUIActionRegistry;

public class SoapUIActivatorTestCase extends TestCase
{
   private SoapUIActionRegistry actionRegistry;

   protected void setUp() throws Exception
   {
      super.setUp();
      SoapUI.setSoapUICore( new SwingSoapUICore() );
      actionRegistry = SoapUI.getActionRegistry();
   }
   
   public void testRemoveActions() throws Exception
   {
      checkExists(NewWorkspaceAction.SOAPUI_ACTION_ID);
      checkExists(SwitchWorkspaceAction.SOAPUI_ACTION_ID);
      checkExists(RenameWorkspaceAction.SOAPUI_ACTION_ID);
      
      SoapUIActivator.updateActions();
      checkRemoved(NewWorkspaceAction.SOAPUI_ACTION_ID);
      checkRemoved(SwitchWorkspaceAction.SOAPUI_ACTION_ID);
      checkRemoved(RenameWorkspaceAction.SOAPUI_ACTION_ID);
   }
   
   private void checkExists(String actionId)
   {
      assertNotNull(actionId, actionRegistry.getAction(actionId));
   }
   
   private void checkRemoved(String actionId)
   {
      assertNull(actionId, actionRegistry.getAction(actionId));
   }
}
