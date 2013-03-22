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

import javax.swing.Action;
import javax.swing.ImageIcon;

import org.eclipse.swt.widgets.Display;

import com.eviware.soapui.support.action.swing.ActionList;
import com.eviware.soapui.support.types.StringToStringMap;
import com.eviware.x.form.XForm;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.XFormField;
import com.eviware.x.impl.swing.SwingXFormDialogBuilder;
import com.eviware.x.impl.swt.support.AwtEnvironment;

public class Swt_AwtXFormDialogBuilder extends SwingXFormDialogBuilder
{

	public Swt_AwtXFormDialogBuilder( String name )
	{
		super( name );
	}

	@Override
	public XFormDialog buildDialog( ActionList actions, String description, ImageIcon icon )
	{
		return new DialogBridge( super.buildDialog( actions, description, icon ) );
	}

	private class DialogBridge implements XFormDialog
	{
		private final XFormDialog dialog;
		private StringToStringMap result;
		private boolean booleanResult;

		public DialogBridge( XFormDialog dialog )
		{
			this.dialog = dialog;
		}

		public void addAction( Action action )
		{
			dialog.addAction( action );
		}

		public boolean getBooleanValue( String name )
		{
			return dialog.getBooleanValue( name );
		}

		public XFormField getFormField( String name )
		{
			return dialog.getFormField( name );
		}

		public XForm[] getForms()
		{
			return dialog.getForms();
		}

		public int getIntValue( String name, int defaultValue )
		{
			return dialog.getIntValue( name, defaultValue );
		}

		public int getReturnValue()
		{
			return dialog.getReturnValue();
		}

		public String getValue( String field )
		{
			return dialog.getValue( field );
		}

		public int getValueIndex( String name )
		{
			return dialog.getValueIndex( name );
		}

		public StringToStringMap getValues()
		{
			return dialog.getValues();
		}

		public void release()
		{
			dialog.release();
		}

		public void setBooleanValue( String name, boolean b )
		{
			dialog.setBooleanValue( name, b );
		}

		public void setFormFieldProperty( String name, Object value )
		{
			dialog.setFormFieldProperty( name, value );
		}

		public void setIntValue( String name, int value )
		{
			dialog.setIntValue( name, value );
		}

		public void setOptions( String field, Object[] options )
		{
			dialog.setOptions( field, options );
		}

		public void setSize( int i, int j )
		{
			dialog.setSize( i, j );
		}

		public void setValue( String field, String value )
		{
			dialog.setValue( field, value );
		}

		public void setValues( StringToStringMap values )
		{
			dialog.setValues( values );
		}

		public void setVisible( boolean visible )
		{
			dialog.setVisible( visible );
		}

		public void setWidth( int i )
		{
			dialog.setWidth( i );
		}

		public boolean show()
		{
			if( Display.getCurrent() != null )
			{
				AwtEnvironment.getInstance( Display.getCurrent() ).invokeAndBlockSwt( new Runnable()
				{
					public void run()
					{
						booleanResult = dialog.show();
					}
				} );
			}
			else
			{
				booleanResult = dialog.show();
			}
			return booleanResult;
		}

		public StringToStringMap show( final StringToStringMap values )
		{
			if( Display.getCurrent() != null )
			{
				AwtEnvironment.getInstance( Display.getCurrent() ).invokeAndBlockSwt( new Runnable()
				{
					public void run()
					{
						result = dialog.show( values );
					}
				} );
			}
			else
			{
				result = dialog.show( values );
			}
			return result;
		}

		public boolean validate()
		{
			return dialog.validate();
		}

	}

}
