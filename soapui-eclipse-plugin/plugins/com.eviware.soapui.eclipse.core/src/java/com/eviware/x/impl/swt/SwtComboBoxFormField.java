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

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.eviware.x.form.XFormOptionsField;

/**
 * 
 * @author Lars H
 */
public class SwtComboBoxFormField extends AbstractSwtXFormField<Combo>
implements SelectionListener, XFormOptionsField 
{
   private Combo comboBox;
   
   private ArrayList<Object> items = new ArrayList<Object>();
   
	public SwtComboBoxFormField(Composite panel, Object[] values)
	{
		if( values.length > 0 && values[0] == null )
		{
			comboBox = new Combo(panel,  SWT.DROP_DOWN );
			values[0] = "";
		}
		else
		{
			comboBox = new Combo(panel,  SWT.DROP_DOWN | SWT.READ_ONLY);
		}
      
		setOptions(values);
      
      Point size = comboBox.getSize();
      if( size.x < 200 )
      {
      	comboBox.setSize( 200, size.y );
      }
      
      comboBox.select(0);
      
		getComponent().addSelectionListener( this );
	}

   public void setOptions(Object[] values)
   {
      items.clear();
      for(int i = 0; i < values.length; i++)
      {
         items.add(values[i] != null ? values[i].toString() : "null");
      }
      comboBox.setItems(items.toArray(new String[items.size()]));
   }
   
   public String[] getOptions()
   {
      return comboBox.getItems();
   }
   
   public void addItem(Object value)
   {
      items.add(value);
      comboBox.add(value.toString());
   }
   
	public void setValue(String value)
	{
      int index = items.indexOf(value);
      if(index >= 0)
         comboBox.select(index);
      else
         comboBox.clearSelection();
	}

	public String getValue()
	{
		int index = comboBox.getSelectionIndex();
      if(index >= 0)
         return items.get(index).toString();
      else
         return comboBox.getText();
	}

   @Override
   public Combo getComponent()
   {
      return comboBox;
   }

   public void widgetDefaultSelected(SelectionEvent e)
   {
   }

   public void widgetSelected(SelectionEvent e)
   {
      fireValueChanged( getValue(), null );
   }

	public void setEditable(boolean editable)
	{
	}

   public String[] getSelectedOptions()
   {
      return new String[] { getValue() };
   }

   public void setSelectedOptions(Object[] arg0)
   {
   }

	public int[] getSelectedIndexes()
	{
		return null;
	}
}
