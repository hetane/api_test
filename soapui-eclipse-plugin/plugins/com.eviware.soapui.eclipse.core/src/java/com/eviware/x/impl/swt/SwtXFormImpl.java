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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.model.iface.Interface;
import com.eviware.soapui.support.types.StringToStringMap;
import com.eviware.x.form.XForm;
import com.eviware.x.form.XFormField;
import com.eviware.x.form.XFormOptionsField;
import com.eviware.x.form.XFormTextField;

/**
 * 
 * @author Lars H
 */
public class SwtXFormImpl implements XForm
{
   private String name;
   private Composite panel;
   private GridLayout layout;
   private Map<String,XFormField> components = new HashMap<String,XFormField>();
	
	public SwtXFormImpl(Composite parent, String name)
	{
		this.name = name;
      panel = new Composite( parent, SWT.NONE );
      layout = new GridLayout();
      layout.numColumns = 3;
      panel.setLayout(layout);
      GridData gridData = new GridData(GridData.FILL_BOTH);
      panel.setLayoutData(gridData);
	}
   
   public void setName(String name)
   {
      this.name = name;
   }
   
   public String getName()
	{
		return name;
	}

   public Control getPanel()
   {
      return panel;
   }
   
   public void addSpace( int size )
   {
//      if( size > 0 )
//         layout.appendRow( new RowSpec( size + "px" ) );
   }

   public XFormField addCheckBox( String name, String description )
   {
      Label jlabel = new Label( panel, SWT.LEFT );
      jlabel.setText(name);

      SwtCheckBoxFormField checkBox = new SwtCheckBoxFormField( panel, description );
     
      GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		checkBox.getComponent().setLayoutData(gridData);
      
      return  addComponent( name, checkBox );
   }

   /**
    * Call this if a row only has 2 components to fill the 3 columns of the layout.
    * TODO Eclipse: Get rid of this. Use GridData instead.
    */
   public void addDummy()
   {
      new Label(panel, SWT.NONE);
   }

   public XFormField addComponent(String label, XFormField formComponent)
	{
   	components.put( label, formComponent );
      return formComponent;
	}

	public XFormOptionsField addComboBox( String name, Object [] values, String description )
   {
      Label jlabel = new Label( panel, SWT.LEFT );
      jlabel.setText(name);

      SwtComboBoxFormField comboBox = new SwtComboBoxFormField( panel, values );
      comboBox.setToolTip( description );
      addComponent( name, comboBox );
      
      addDummy();

      return comboBox;
   }

   public void setOptions(String name, Object[] values)
   {
      XFormOptionsField combo = (XFormOptionsField) getComponent( name );
      combo.setOptions( values );
   }
   
   public void addSeparator()
   {
		Label separator = new Label(panel, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 3;
		separator.setLayoutData(gridData);
   }
   
   public void addSeparator( String label )
   {
   	if( label == null )
   	{
			addSeparator();
   	}
   	else
   	{
   		Label text = new Label( panel, SWT.HORIZONTAL );
   		text.setText( label );
   		
   		Label separator = new Label(panel, SWT.SEPARATOR | SWT.HORIZONTAL);
			GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalSpan = 3;
			separator.setLayoutData(gridData);
   	}
   }

   public XFormTextField addTextField( String name, String description, FieldType type )
   {
      Label label = new Label( panel, SWT.LEFT );
      label.setText(name);

   	if( type == FieldType.FOLDER || type == FieldType.FILE )
   	{   		
         SwtFileFormField fileFormField = new SwtFileFormField(
               panel, description, type == FieldType.FOLDER );
         addComponent(name, fileFormField);
         return fileFormField;
   	}
   	else if( type == FieldType.PROJECT_FILE || type == FieldType.PROJECT_FOLDER )
   	{   		
         SwtProjectFileFormField fileFormField = new SwtProjectFileFormField( panel, 
         		description, type == FieldType.PROJECT_FOLDER );
         addComponent(name, fileFormField);
         return fileFormField;
   	}
      
      SwtTextFormField textField = new SwtTextFormField(panel);
      textField.setToolTip( description );
      addComponent( name, textField );
      
      addDummy();

		return textField;
   }

   public XFormField addNameSpaceTable(String name, Interface modelItem)
   {
      Label jlabel = new Label( panel, SWT.LEFT );
      jlabel.setText(name);

      SwtNamespaceTable namespaceTable = new SwtNamespaceTable( panel, (WsdlInterface) modelItem );
      
      addComponent( name, namespaceTable );
      addDummy();
      
      return namespaceTable;
   }

   public void setComponentValue( String label, String value )
   {
      XFormField component = getComponent( label );
		if( component != null ) component.setValue( value );
   }
   
   public String getComponentValue( String name )
   {
      XFormField component = getComponent( name );
      return component == null ? null : component.getValue();
   }

   public XFormField getComponent( String label )
   {
      return components.get( label );
   }

	public void setValues(StringToStringMap values)
	{
		for( Iterator<String> i = values.keySet().iterator(); i.hasNext(); )
		{
			String key = i.next();
			setComponentValue( key, values.get( key ));
		}
	}

	public StringToStringMap getValues()
	{
		StringToStringMap values = new StringToStringMap();
		
		for( Iterator<String> i = components.keySet().iterator(); i.hasNext(); )
		{
			String key = i.next();

         // Cannot do this after the dialog is disposed.
			values.put( key, getComponentValue( key ));
		}
		
		return values;
	}

   public void addLabel(String name, String label)
   {
   	SwtLabelFormField labelField = new SwtLabelFormField( panel, label );
   	labelField.setValue(name);
   	
      addComponent( name, labelField );
   }

   public XFormField[] getFormFields()
   {
      Collection<XFormField> fields = components.values();
      return fields.toArray( new XFormField[fields.size()] );
   }
   
   public XFormField getFormField( String name )
   {
      return components.get( name );
   }
   
   public void setFormFieldProperty(String name, Object value)
	{
		for( XFormField field : components.values() )
		{
			field.setProperty( name, value );
		}
	}
   
   public Object[] getOptions( String name )
   {
      XFormField combo = getComponent( name );
      if( combo instanceof XFormOptionsField  )
         return ((XFormOptionsField)combo).getOptions();
      
      return null;
   }
}
