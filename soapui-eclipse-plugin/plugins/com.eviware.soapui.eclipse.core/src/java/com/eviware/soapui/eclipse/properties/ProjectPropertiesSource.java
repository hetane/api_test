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

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.project.Project;

/**
 * 
 * @author Sofia Jonsson
 * 
 * This class holds eclipse properties for a Project. It implements IPropertySource
 * so that the properties view can pick it up.
 *
 */
public class ProjectPropertiesSource implements IPropertySource {

	Project project; 
	IPropertyDescriptor[] descriptors;
	public static final String FILE_KEY = "file";
	public static final String CACHE_DEF_KEY = "cacheDefinitions";
	
	public ProjectPropertiesSource(Project project){
		this.project = project;
		PropertyDescriptor file = new PropertyDescriptor(FILE_KEY,
		"File");
		PropertyDescriptor cacheDef = new PropertyDescriptor(CACHE_DEF_KEY,
		"Cache Definitions");
		this.descriptors = new IPropertyDescriptor[]{file, cacheDef};
	}

	public Object getEditableValue() {
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (id.equals(FILE_KEY)){
			if (project instanceof WsdlProject){
				return ((WsdlProject)project).getPath();
			}	
		}
		else if (id.equals(CACHE_DEF_KEY)){
			if (project instanceof WsdlProject){
				return ((WsdlProject)project).isCacheDefinitions();
			}
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		if (id.equals(FILE_KEY)){
			if (project instanceof WsdlProject){
				return ((WsdlProject)project).getPath() != null;
			}
		}
		else if (id.equals(CACHE_DEF_KEY)){
			if (project instanceof WsdlProject){
				// is this correct?
				return true;
			}
		}
		return false;
	}

	public void resetPropertyValue(Object id) {
		if (id.equals(FILE_KEY)){
		//	((WsdlProject)project).setPath();
		}
		else if (id.equals(CACHE_DEF_KEY)){
			// what is default?
			//((WsdlProject)project).setCacheDefinitions(cacheDefinitions)();
		}
		
	}

	public void setPropertyValue(Object id, Object value) {
//		if (id.equals(FILE_KEY)){
//			this.fileValue = (String)value;
//		}
//		else 
		if (id.equals(CACHE_DEF_KEY)){
			((WsdlProject)project).setCacheDefinitions(Boolean.parseBoolean((String)value));
		}
		
	}
	

}
