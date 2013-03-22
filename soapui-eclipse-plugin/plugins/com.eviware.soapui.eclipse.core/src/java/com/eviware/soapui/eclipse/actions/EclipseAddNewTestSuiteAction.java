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

import java.io.IOException;

import org.eclipse.jface.action.Action;

import com.eviware.soapui.impl.wsdl.actions.project.AddNewTestSuiteAction;
import com.eviware.soapui.model.project.Project;
import com.eviware.x.form.XForm;
import com.eviware.x.form.XFormDialogBuilder;
import com.eviware.x.form.XFormFactory;

/**
 * 
 * @author Lars H
 */
public class EclipseAddNewTestSuiteAction extends AdaptSoapuiObjectAction<Project>
{
   public EclipseAddNewTestSuiteAction()
   {
      super(Project.class, AddNewTestSuiteAction.class);
   }
   
   @Override
   protected void executeAction(Project selectedElement, Action action) throws IOException
   {
// FIXME Merge: Remove this?
      XFormDialogBuilder builder = XFormFactory.createDialogBuilder( "Basic Authentication" );
      XForm mainForm = builder.createForm( "Basic" );
      mainForm.addLabel( "Info", "" );
      mainForm.addTextField( "Username", "Username for authentication", XForm.FieldType.TEXT ); 
      mainForm.addTextField( "Password", "Password for authentication", XForm.FieldType.PASSWORD ); 
     
      action.run();
      selectedElement.save();
   }
}
