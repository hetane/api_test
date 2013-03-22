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

package com.eviware.soapui.eclipse.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.eviware.soapui.impl.wsdl.WsdlOperation;

/**
 * 
 * @author Sofia Jonsson
 * 
 *         This class holds eclipse properties for an Operation. It implements
 *         IPropertySource so that the properties view can pick it up.
 * 
 */
public class OperationPropertiesSource implements IPropertySource
{

	WsdlOperation operation;
	IPropertyDescriptor[] descriptors;

	public static final String SOAPACTION_KEY = "action";
	public static final String OPERATION_KEY = "operation";
	public static final String STYLE_KEY = "style";
	public static final String INPUT_KEY = "inputName";
	public static final String OUTPUT_KEY = "outputName";
	public static final String SENDS_ATTACHMENTS_KEY = "sendsAttachments";
	public static final String RECEIVES_ATTACHMENTS_KEY = "receivesAttachments";

	public OperationPropertiesSource( WsdlOperation operation )
	{
		this.operation = operation;
		PropertyDescriptor soapActionDef = new PropertyDescriptor( SOAPACTION_KEY, "SOAPAction" );
		PropertyDescriptor operationDef = new PropertyDescriptor( OPERATION_KEY, "Operation" );
		PropertyDescriptor styleDef = new PropertyDescriptor( STYLE_KEY, "Style" );
		PropertyDescriptor inputDef = new PropertyDescriptor( INPUT_KEY, "Input" );
		PropertyDescriptor outputDef = new PropertyDescriptor( OUTPUT_KEY, "Output" );
		PropertyDescriptor sendsAttachmentsDef = new PropertyDescriptor( SENDS_ATTACHMENTS_KEY, "Sends Attachments" );
		PropertyDescriptor receivesAttachmentsDef = new PropertyDescriptor( RECEIVES_ATTACHMENTS_KEY,
				"Receives Attachments" );
		this.descriptors = new IPropertyDescriptor[] { soapActionDef, operationDef, styleDef, inputDef, outputDef,
				sendsAttachmentsDef, receivesAttachmentsDef };
	}

	public Object getEditableValue()
	{
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		return descriptors;
	}

	public Object getPropertyValue( Object id )
	{
		if( id.equals( SOAPACTION_KEY ) )
		{
			return operation.getAction();
		}
		else if( id.equals( OPERATION_KEY ) )
		{
			return operation.getName();
		}
		else if( id.equals( STYLE_KEY ) )
		{
			return operation.getStyle();
		}
		else if( id.equals( INPUT_KEY ) )
		{
			return operation.getInputName();
		}
		else if( id.equals( OUTPUT_KEY ) )
		{
			return operation.getOutputName();
		}
		else if( id.equals( SENDS_ATTACHMENTS_KEY ) )
		{
			return operation.getSendsAttachments();
		}
		else if( id.equals( RECEIVES_ATTACHMENTS_KEY ) )
		{
			return operation.getReceivesAttachments();
		}
		return null;
	}

	public boolean isPropertySet( Object id )
	{
		if( id.equals( SOAPACTION_KEY ) )
		{
			return operation.getAction() != null;
		}
		else if( id.equals( OPERATION_KEY ) )
		{
			return operation.getName() != null;
		}
		else if( id.equals( STYLE_KEY ) )
		{
			return operation.getStyle() != null;
		}
		else if( id.equals( INPUT_KEY ) )
		{
			return operation.getInputName() != null;
		}
		else if( id.equals( OUTPUT_KEY ) )
		{
			return operation.getOutputName() != null;
		}
		else if( id.equals( SENDS_ATTACHMENTS_KEY ) )
		{
			return true;
		}
		else if( id.equals( RECEIVES_ATTACHMENTS_KEY ) )
		{
			return true;
		}
		return false;
	}

	public void resetPropertyValue( Object id )
	{
		// TODO what are the default settings?
		if( id.equals( SOAPACTION_KEY ) )
		{
			//	
		}
		else if( id.equals( OPERATION_KEY ) )
		{
			// operation.setName();
		}
		else if( id.equals( STYLE_KEY ) )
		{
			// return operation.getStyle();
		}
		else if( id.equals( INPUT_KEY ) )
		{
			// return operation.getInputName();
		}
		else if( id.equals( OUTPUT_KEY ) )
		{
			// return operation.getOutputName();
		}
		else if( id.equals( SENDS_ATTACHMENTS_KEY ) )
		{
			// return operation.getSendsAttachments();
		}
		else if( id.equals( RECEIVES_ATTACHMENTS_KEY ) )
		{
			// return operation.getReceivesAttachments();
		}

	}

	public void setPropertyValue( Object id, Object value )
	{
		// TODO figure out how to set the values
		if( id.equals( SOAPACTION_KEY ) )
		{
			// operation.setAction(value);
		}
		else if( id.equals( OPERATION_KEY ) )
		{
			operation.setName( ( String )value );
		}
		else if( id.equals( STYLE_KEY ) )
		{
			// operation.setStyle();
		}
		else if( id.equals( INPUT_KEY ) )
		{
			operation.setInputName( ( String )value );
		}
		else if( id.equals( OUTPUT_KEY ) )
		{
			operation.setOutputName( ( String )value );
		}
		else if( id.equals( SENDS_ATTACHMENTS_KEY ) )
		{
			// operation.setSendsAttachments(Boolean.parseBoolean((String)value));
		}
		else if( id.equals( RECEIVES_ATTACHMENTS_KEY ) )
		{
			// return operation.getReceivesAttachments();
		}
	}

}
