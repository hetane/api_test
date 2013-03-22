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

package com.eviware.x.impl.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * @author Lars H
 */
public class SwtCheckBoxFormField extends AbstractSwtXFormField<Button> implements SelectionListener 
{
   private Button checkBox;
   
	public SwtCheckBoxFormField(Composite panel, String description)
	{
      checkBox = new Button(panel, SWT.CHECK);
      if( description != null )
         checkBox.setText( description );
      checkBox.addSelectionListener(this);
	}

	public void setValue(String value)
	{
      checkBox.setSelection( value != null ? Boolean.parseBoolean( value ) : false );
	}

	public String getValue()
	{
		return Boolean.toString( checkBox.getSelection() );
	}

   @Override
   public Button getComponent()
   {
      return checkBox;
   }

   public void widgetDefaultSelected(SelectionEvent e)
   {
   }

   public void widgetSelected(SelectionEvent e)
   {
      String enabled = Boolean.toString( checkBox.getSelection() );
      fireValueChanged( enabled, null );
   }
}
