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

import com.eviware.soapui.impl.WorkspaceImpl;
import com.eviware.soapui.model.workspace.Workspace;


// When is this used??
public class WorkspacePropertiesSource implements IPropertySource {

	private Workspace workspace; 

	IPropertyDescriptor[] descriptors;

	public static final String FILE_KEY = "file"; 
	
	public WorkspacePropertiesSource(Workspace workspace) {
		this.workspace = workspace;
		PropertyDescriptor file = new PropertyDescriptor(FILE_KEY, "File");
		
		this.descriptors = new IPropertyDescriptor[] { file };

	}
	
	
	public Object getEditableValue() {
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (id.equals(FILE_KEY)){
			return ((WorkspaceImpl)workspace).getPath();
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		if (id.equals(FILE_KEY)){
			return ((WorkspaceImpl)workspace).getPath() != null;
		}
		return false;
	}

	public void resetPropertyValue(Object id) {
		if (id.equals(FILE_KEY)){
			//TODO figure out what the default value is
			((WorkspaceImpl)workspace).setPath(null);
		}

	}

	public void setPropertyValue(Object id, Object value) {
		if (id.equals(FILE_KEY)){
			((WorkspaceImpl)workspace).setPath((String)value);
		}

	}

}
