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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SwtLabelFormField extends AbstractSwtXFormField<Label> 
{
   private Label label;
	private Label content;
   
	public SwtLabelFormField(Composite panel, String text )
	{
      label = new Label( (Composite)panel, SWT.LEFT );
      content = new Label( (Composite)panel, SWT.LEFT );
      
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.horizontalSpan = 2;
      content.setLayoutData(gridData);
	}
	
   @Override
   public Label getComponent()
   {
      return content;
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
	}
}
