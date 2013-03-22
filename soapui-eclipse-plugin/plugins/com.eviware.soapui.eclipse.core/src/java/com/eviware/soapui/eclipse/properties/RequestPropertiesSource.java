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

import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.model.iface.Request;

public class RequestPropertiesSource implements IPropertySource {

	private Request request;

	IPropertyDescriptor[] descriptors;

	public static final String NAME_KEY = "name";

	public static final String ENCODING_KEY = "encoding";

	public static final String ENDPOINT_KEY = "endpoint";

	public static final String MTOMENABLED_KEY = "mtomEnabled";

	public static final String USERNAME_KEY = "username";

	public static final String PWD_KEY = "password";

	public static final String DOMAIN_KEY = "domain";

	public static final String WSSPASSWORDTYPE_KEY = "wssPasswordType";


	public RequestPropertiesSource(Request request) {
		this.request = request;
		PropertyDescriptor name = new PropertyDescriptor(NAME_KEY, "Name");
		PropertyDescriptor encoding = new PropertyDescriptor(ENCODING_KEY,
				"Encoding");
		PropertyDescriptor endpoint = new PropertyDescriptor(ENDPOINT_KEY,
				"Endpoint");
		PropertyDescriptor mtomEnabled = new PropertyDescriptor(
				MTOMENABLED_KEY, "Enable MTOM/Inline");
		PropertyDescriptor username = new PropertyDescriptor(USERNAME_KEY,
				"Username");
		PropertyDescriptor password = new PropertyDescriptor(PWD_KEY,
				"Password");
		PropertyDescriptor domain = new PropertyDescriptor(DOMAIN_KEY, "Domain");
		PropertyDescriptor wssPasswordType = new PropertyDescriptor(
				WSSPASSWORDTYPE_KEY, "WSS-Password Type");
		this.descriptors = new IPropertyDescriptor[] { name, encoding,
				endpoint, mtomEnabled, username, password, domain,
				wssPasswordType };

	}

	public Object getEditableValue() {
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (id.equals(NAME_KEY)) {
			return request.getName();
		} else if (id.equals(ENCODING_KEY)) {
			return request.getEncoding();
		}
		else if (id.equals(ENDPOINT_KEY)) {
			return request.getEndpoint();
		}
		else if (id.equals(MTOMENABLED_KEY)) {
			return ((WsdlRequest)request).isMtomEnabled();
		}
		else if (id.equals(USERNAME_KEY)) {
			return ((WsdlRequest)request).getUsername();
		}
		else if (id.equals(PWD_KEY)) {
			return ((WsdlRequest)request).getPassword();
		}
		else if (id.equals(DOMAIN_KEY)) {
			return ((WsdlRequest)request).getDomain();
		}
		else if (id.equals(WSSPASSWORDTYPE_KEY)) {
			return ((WsdlRequest)request).getWssPasswordType();
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		if (id.equals(NAME_KEY)) {
			return request.getName() != null;
		} else if (id.equals(ENCODING_KEY)) {
			return request.getEncoding() != null;
		}
		else if (id.equals(ENDPOINT_KEY)) {
			return request.getEndpoint() != null;
		}
		else if (id.equals(MTOMENABLED_KEY)) {
			return true;
		}
		else if (id.equals(USERNAME_KEY)) {
			return ((WsdlRequest)request).getUsername() != null;
		}
		else if (id.equals(PWD_KEY)) {
			return ((WsdlRequest)request).getPassword() != null;
		}
		else if (id.equals(DOMAIN_KEY)) {
			return ((WsdlRequest)request).getDomain() != null;
		}
		else if (id.equals(WSSPASSWORDTYPE_KEY)) {
			return ((WsdlRequest)request).getWssPasswordType() != null;
		}
		return false;
	}

	public void resetPropertyValue(Object id) {
		//TODO check the default values for each property
		if (id.equals(NAME_KEY)) {
			//request.setName(null);
		} else if (id.equals(ENCODING_KEY)) {
			//return request.getEncoding() != null;
		}
		else if (id.equals(ENDPOINT_KEY)) {
			//return request.getEndpoint() != null;
		}
		else if (id.equals(MTOMENABLED_KEY)) {
			//return true;
		}
		else if (id.equals(USERNAME_KEY)) {
			//return ((WsdlRequest)request).getUsername() != null;
		}
		else if (id.equals(PWD_KEY)) {
			//return ((WsdlRequest)request).getPassword() != null;
		}
		else if (id.equals(DOMAIN_KEY)) {
			//return ((WsdlRequest)request).getDomain() != null;
		}
		else if (id.equals(WSSPASSWORDTYPE_KEY)) {
			//return ((WsdlRequest)request).getWssPasswordType() != null;
		}

	}

	public void setPropertyValue(Object id, Object value) {
		if (id.equals(NAME_KEY)) {
			// TODO figure out if the name should be allowed to be set this way
			//request.setName((String)value);
		} else if (id.equals(ENCODING_KEY)) {
			request.setEncoding((String)value);
		}
		else if (id.equals(ENDPOINT_KEY)) {
			request.setEndpoint((String)value);
		}
		else if (id.equals(MTOMENABLED_KEY)) {
			((WsdlRequest)request).setMtomEnabled(Boolean.parseBoolean((String)value));
		}
		else if (id.equals(USERNAME_KEY)) {
			((WsdlRequest)request).setUsername((String)value);
		}
		else if (id.equals(PWD_KEY)) {
			((WsdlRequest)request).setPassword((String)value);
		}
		else if (id.equals(DOMAIN_KEY)) {
			((WsdlRequest)request).setDomain((String)value);
		}
		else if (id.equals(WSSPASSWORDTYPE_KEY)) {
			((WsdlRequest)request).setWssPasswordType((String)value);
		}

	}

}
