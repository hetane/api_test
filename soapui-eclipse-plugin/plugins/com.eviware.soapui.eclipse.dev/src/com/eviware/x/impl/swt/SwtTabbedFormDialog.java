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
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.eviware.soapui.support.action.swing.ActionList;
import com.eviware.soapui.support.action.swing.ActionSupport;
import com.eviware.soapui.support.action.swing.DefaultActionList;
import com.eviware.soapui.support.types.StringToStringMap;
import com.eviware.x.form.XForm;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.XFormField;

/**
 * 
 * @author Lars H
 */
public class SwtTabbedFormDialog extends AbstractDialog implements XFormDialog
{
   private ActionList actions;
   
   // On SWT (on Macintosh), parent components must be created before children.
   // The interface of this class is designed for Swing, where this was not a requirement.
   // Instead of changing that interface, this class first operates in a record mode
   // where all forms are mocked and method calls recorded. In this mode, forms == null.
   // Afterwards, the components are created and the recorded calls repeated on real components.
   private ArrayList<InvocationRecorder<XForm>> formHandlers = new ArrayList<InvocationRecorder<XForm>>();
	private ArrayList<XForm> forms = null;
   
	private TabFolder tabs;
	private int returnValue;
   
   private StringToStringMap storedResult;
	private String newTitle;

	private Image newTitleImage;

	public SwtTabbedFormDialog(String title)
	{
      super(title);
   }

   @SuppressWarnings("unchecked")
   public XForm createForm(String name)
   {
      // Mock a form.
      InvocationRecorder<XForm> formHandler = new InvocationRecorder<XForm>(XForm.class);
      this.formHandlers.add(formHandler);
      XForm form = formHandler.getMock();
      form.setName(name);
    
      return form;
   }
   
   public void setActions(ActionList actions)
   {
      this.actions = actions;
   }
   
   public void addAction(Action action)
   {
      if(actions == null)
      {
         actions = new DefaultActionList();
      }
      actions.addAction( action );      
   }
   
   @Override
   protected Control createDialogArea(Composite parent)
   {
		parent = (Composite) super.createDialogArea(parent);
		
		Composite container = new Composite(parent, SWT.NONE);
	   FillLayout fillLayout = new FillLayout();
	   fillLayout.marginWidth = 3;
	   fillLayout.marginHeight = 3;
		container.setLayout(fillLayout);
	   GridData gridData = new GridData(GridData.FILL_BOTH);
		container.setLayoutData(gridData);
		
      tabs = new TabFolder(container, SWT.NONE);

		this.forms = new ArrayList<XForm>();
      for(InvocationRecorder<XForm> formHandler : formHandlers)
      {
         SwtXFormImpl form = new SwtXFormImpl(tabs, "");
         formHandler.playback(form);
         
         TabItem tab = new TabItem(tabs, SWT.NONE);
         tab.setControl(form.getPanel());
         tab.setText(form.getName());
         
         this.forms.add(form);
      }
		
      return parent;
	}

   @Override
   protected void createButtonsForButtonBar(Composite parent)
   {
      // this.actions is not set yet. Create buttons later.
   	if( actions == null )
   		return;
   	
      for(int i = 0; i < actions.getActionCount(); i++)
      {
         final Action action = actions.getActionAt(i);
         
         if( action == ActionSupport.SEPARATOR_ACTION )
         {
         }
         else 
         {
	         int buttonId = i;
	         String label = (String) action.getValue(Action.NAME);
	         boolean defaultButton = "true".equals(action.getValue(Action.DEFAULT));
	         
	         Button button = createButton(parent, buttonId, label, defaultButton);
         }
      }
   }
   
   @Override
   protected void buttonPressed(int buttonId)
   {
      Action action = actions.getActionAt(buttonId);
      if( action != null )
         action.actionPerformed(null);
   }
   
	public void setValues(StringToStringMap values)
	{
		for( XForm form : getForms() )
			form.setValues( values );
	}

	public void setTitle(String newTitle)
	{
		this.newTitle = newTitle;
	}

	public void setTitleImage(Image newTitleImage)
	{
		this.newTitleImage = newTitleImage;
	}

	public StringToStringMap getValues()
	{
      if(storedResult != null)
      {
         return storedResult;
      }

      StringToStringMap result = new StringToStringMap();
      storeResult(result);
		return result;
	}
	
	public int open()
	{
		create();
		
		if( newTitle != null )
   		super.setTitle( newTitle );
   	   	
   	if( newTitleImage != null )
   		super.setTitleImage( newTitleImage );
		
		tabs.setSelection( 0 );
      storedResult = null;
      
      super.initializeBounds();
		return super.open();
	}

	public boolean close()
	{
      storedResult = new StringToStringMap();
      storeResult(storedResult);
      
      return super.close();
	}
   
   private void storeResult(StringToStringMap result)
   {
      for( XForm form : getForms() )
      {
         result.putAll( form.getValues() );
      }
   }

	public int getReturnValue()
	{
		return returnValue;
	}

	public void setVisible(boolean visible)
	{
		if( visible )
			open();
		else
			close();
	}

	public StringToStringMap show(StringToStringMap values)
	{
		setValues( values );
		setVisible( true );
		return getValues();
	}

	public void setReturnValue(int returnValue)
	{
		this.returnValue = returnValue;
	}

   public boolean validate()
   {
      return true;
   }

	public void setOptions(String field, Object[] options)
	{
      for( XForm form : getForms() )
         form.setOptions( field, options );
	}

	public void setFormFieldProperty(String name, Object value)
	{
		for( XForm form : getForms() )
		{
			form.setFormFieldProperty( name, value );
		}
	}
   
   public XForm[] getForms()
   {
      if(forms != null)
      {
         return forms.toArray(new XForm[forms.size()]);
      }
      else
      {
         ArrayList<XForm> tmp = new ArrayList<XForm>();
         for(InvocationRecorder<XForm> handler : formHandlers)
         {
            tmp.add( handler.getMock() );
         }
         return tmp.toArray(new XForm[tmp.size()]);
      }
   }

   public boolean getBooleanValue( String name )
   {
      try
      {
         return Boolean.parseBoolean( getValue( name ));
      }
      catch( NumberFormatException e )
      {
         return false;
      }
   }
   
   public int getIntValue( String name, int defaultValue )
   {
      try
      {
         return Integer.parseInt( getValue( name ));
      }
      catch( NumberFormatException e )
      {
         return defaultValue;
      }
   }

   public void setBooleanValue( String name, boolean b )
   {
      setValue( name, Boolean.toString( b ));
   }

   public void setIntValue( String name, int value )
   {
      setValue( name, Integer.toString( value ));
   }

   public XFormField getFormField(String name)
   {
      for( XForm form : getForms() )
      {
         XFormField formField = form.getFormField( name );
         if( formField != null )
            return formField;
      }
      
      return null;
   }

   public String getValue(String field)
   {
      return getValues().get(field);
   }

   public int getValueIndex(String name)
   {
      for(XForm form : getForms())
      {
         if( form.getComponent( name ) != null )
         {
            Object[] options = form.getOptions( name );
            if( options == null )
               return -1;
      
            return Arrays.asList( options ).indexOf( form.getComponentValue( name ) );
         }
      }
      
      return -1;
   }

   public void setValue(String field, String value)
   {
      for(XForm form : getForms())
      {
         if( form.getComponent( field ) != null )
            form.getComponent( field ).setValue( value );
      }
   }

   public boolean show()
   {
      show( new StringToStringMap() );

      return returnValue == XFormDialog.OK_OPTION;
   }

   public void setWidth(int w)
   {
      // Ignore.
   }

	public void release()
	{
		// TODO Auto-generated method stub
		
	}

	public void setSize( int i, int j )
	{
		// TODO Auto-generated method stub
		
	}
}
