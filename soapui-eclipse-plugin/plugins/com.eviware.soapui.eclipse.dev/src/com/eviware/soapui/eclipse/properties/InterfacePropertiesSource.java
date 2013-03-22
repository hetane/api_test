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

import com.eviware.soapui.impl.wsdl.WsdlInterface;

/**
 * 
 * @author Sofia Jonsson
 * 
 *         This class holds eclipse properties for an Interface. It implements
 *         IPropertySource so that the properties view can pick it up.
 * 
 */
public class InterfacePropertiesSource implements IPropertySource
{

	WsdlInterface iFace;
	IPropertyDescriptor[] descriptors;

	public static final String DEFINITION_URL_KEY = "definition";
	public static final String BINDING_KEY = "binding";
	public static final String PORTTYPE_KEY = "portType";
	public static final String SOAPVERSION_KEY = "soapVersion";
	public static final String CACHED_KEY = "cached";
	public static final String STYLE_KEY = "style";

	public InterfacePropertiesSource( WsdlInterface iFace )
	{
		this.iFace = iFace;
		PropertyDescriptor defDef = new PropertyDescriptor( DEFINITION_URL_KEY, "Definition" );
		PropertyDescriptor bindingDef = new PropertyDescriptor( BINDING_KEY, "Binding" );
		PropertyDescriptor portTypeDef = new PropertyDescriptor( PORTTYPE_KEY, "PortType" );
		PropertyDescriptor soapVersionDef = new PropertyDescriptor( SOAPVERSION_KEY, "SOAP Version" );
		PropertyDescriptor cachedDef = new PropertyDescriptor( CACHED_KEY, "Cached" );
		PropertyDescriptor styleDef = new PropertyDescriptor( STYLE_KEY, "Style" );
		this.descriptors = new IPropertyDescriptor[] { defDef, bindingDef, portTypeDef, soapVersionDef, cachedDef,
				styleDef };
	}

	public Object getEditableValue()
	{
		// TODO figure out how this effects which properties are editable?
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		return descriptors;
	}

	public Object getPropertyValue( Object id )
	{
		if( id.equals( DEFINITION_URL_KEY ) )
		{
			return iFace.getDefinition();
		}
		else if( id.equals( BINDING_KEY ) )
		{
			return iFace.getBindingName();
		}
		else if( id.equals( PORTTYPE_KEY ) )
		{
			return ( iFace ).getName();
		}
		else if( id.equals( SOAPVERSION_KEY ) )
		{
			return ( iFace ).getSoapVersion();
		}
		else if( id.equals( CACHED_KEY ) )
		{
			// TODO figure out how to get this value
			return false;
		}
		else if( id.equals( STYLE_KEY ) )
		{
			return ( iFace ).getStyle();
		}
		return null;
	}

	public boolean isPropertySet( Object id )
	{
		if( id.equals( DEFINITION_URL_KEY ) )
		{
			return iFace.getDefinition() != null;
		}
		else if( id.equals( BINDING_KEY ) )
		{
			return iFace.getBindingName() != null;
		}
		else if( id.equals( PORTTYPE_KEY ) )
		{
			return ( iFace ).getName() != null;
		}
		else if( id.equals( SOAPVERSION_KEY ) )
		{
			return ( iFace ).getSoapVersion() != null;
		}
		else if( id.equals( CACHED_KEY ) )
		{
			// TODO where do I get this setting?
			//		
		}
		else if( id.equals( STYLE_KEY ) )
		{
			return ( iFace ).getStyle() != null;
		}
		return false;
	}

	public void resetPropertyValue( Object id )
	{
		// TODO Figure out what are the default settings?
	}

	public void setPropertyValue( Object id, Object value )
	{
		if( id.equals( DEFINITION_URL_KEY ) )
		{
			// Not editable?
		}
		else if( id.equals( BINDING_KEY ) )
		{
			// Not editable?
		}
		else if( id.equals( PORTTYPE_KEY ) )
		{
			// Not editable?
		}
		else if( id.equals( SOAPVERSION_KEY ) )
		{
			// TODO figure out how to set this properly
			// ((WsdlInterface)iFace).setSoapVersion(new SoapVersion());
		}
		else if( id.equals( CACHED_KEY ) )
		{
			// Not editable?
		}
		else if( id.equals( STYLE_KEY ) )
		{
			// Not editable?
		}
	}

}
