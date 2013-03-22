/*
 *  soapUI, copyright (C) 2004-2011 smartbear.com
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the
 *  terms of version 2.1 of the GNU Lesser General Public License as published by
 *  the Free Software Foundation.
 *  For the avoidance of doubt, eviware elects not to use any
 *  later versions of the LGPL License.
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details at gnu.org.
 *
 *
 *  -------------------------------------------------------------------------------------
 *  If soapUI is distributed with products from Sun, note the following:
 *  -------------------------------------------------------------------------------------
 *
 *  For the avoidance of doubt, except that if any license choice other than
 *  GPL or LGPL is available it will apply instead,
 *  Sun elects to use only the General Public License version 2 (GPLv2) at
 *  this time for any software where a choice of GPL
 *  license versions is made available with the language indicating that
 *  GPLv2 or any later version may be used, or where a
 *  choice of which version of the GPL is applied is otherwise unspecified.
 */

package com.eviware.soapui.netbeans.module.actions;

import com.eviware.soapui.actions.SoapUIPreferencesAction;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class PreferencesAction extends CallableSystemAction
{
   private SoapUIPreferencesAction soapuiAction;
   
   public void performAction ()
   {
      soapuiAction.actionPerformed ( null );
   }
   
   public String getName ()
   {
      return NbBundle.getMessage (PreferencesAction.class, "CTL_PreferencesAction");
   }
   
   protected void initialize ()
   {
      super.initialize ();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue ("noIconInMenu", Boolean.TRUE);
      
      soapuiAction = new SoapUIPreferencesAction();
   }
   
   public HelpCtx getHelpCtx ()
   {
      return HelpCtx.DEFAULT_HELP;
   }
   
   protected boolean asynchronous ()
   {
      return false;
   }
   
}
