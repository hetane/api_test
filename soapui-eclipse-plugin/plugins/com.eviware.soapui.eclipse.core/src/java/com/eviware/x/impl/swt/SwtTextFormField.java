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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.eviware.x.form.XFormTextField;

public class SwtTextFormField extends AbstractSwtXFormField<Text> implements XFormTextField
{
   private Text component;
   
	public SwtTextFormField(Composite panel)
	{
      component = new Text( (Composite)panel, SWT.BORDER );
      component.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      component.addModifyListener(new ModifyListener(){
		public void modifyText(ModifyEvent arg0) {
			fireValueChanged(getValue(), null);
		}
      });
	}
	
   @Override
   public Text getComponent()
   {
      return component;
   }

	public void setRequired(boolean required, String message)
	{
		super.setRequired(required, message);

// TODO Red border around required parameters
//		if( required )
//			getComponent().setBorder( BorderFactory.createLineBorder( Color.RED ));
//		else
//			getComponent().setBorder( BorderFactory.createLineBorder( Color.GRAY ));
	}

	public void setValue(String value)
	{
		getComponent().setText( value != null ? value : "" );
	}

	public String getValue()
	{
		return getComponent().getText();
	}

	public void setWidth(int columns)
	{
		// TODO Not implemented set width of SWT text field
	}
}
