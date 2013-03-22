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

package com.eviware.soapui.eclipse.project;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.iface.Interface;
import com.eviware.soapui.model.mock.MockService;
import com.eviware.soapui.model.project.EndpointStrategy;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.project.ProjectListener;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.model.testsuite.TestProperty;
import com.eviware.soapui.model.testsuite.TestPropertyListener;
import com.eviware.soapui.model.testsuite.TestSuite;
import com.eviware.soapui.model.workspace.Workspace;

/**
 * 
 * @author Lars H
 */
public class ProjectProxy implements Project
{
	private Project project;

	public ProjectProxy( Project project )
	{
		this.project = project;
	}

	public Project getProject()
	{
		return this.project;
	}

	public void addProjectListener( ProjectListener listener )
	{
		project.addProjectListener( listener );
	}

	public Interface getInterfaceAt( int index )
	{
		return project.getInterfaceAt( index );
	}

	public Interface getInterfaceByName( String interfaceName )
	{
		return project.getInterfaceByName( interfaceName );
	}

	public int getInterfaceCount()
	{
		return project.getInterfaceCount();
	}

	public TestSuite getTestSuiteAt( int index )
	{
		return project.getTestSuiteAt( index );
	}

	public TestSuite getTestSuiteByName( String testSuiteName )
	{
		return project.getTestSuiteByName( testSuiteName );
	}

	public int getTestSuiteCount()
	{
		return project.getTestSuiteCount();
	}

	public Workspace getWorkspace()
	{
		return project.getWorkspace();
	}

	public void removeProjectListener( ProjectListener listener )
	{
		project.removeProjectListener( listener );
	}

	public String getDescription()
	{
		return project.getDescription();
	}

	public ImageIcon getIcon()
	{
		return project.getIcon();
	}

	public String getName()
	{
		return project.getName();
	}

	public Settings getSettings()
	{
		return project.getSettings();
	}

	public void addPropertyChangeListener( String propertyName, PropertyChangeListener listener )
	{
		project.addPropertyChangeListener( propertyName, listener );
	}

	public void addPropertyChangeListener( PropertyChangeListener listener )
	{
		project.addPropertyChangeListener( listener );
	}

	public void removePropertyChangeListener( PropertyChangeListener listener )
	{
		project.removePropertyChangeListener( listener );
	}

	public void removePropertyChangeListener( String propertyName, PropertyChangeListener listener )
	{
		project.removePropertyChangeListener( propertyName, listener );
	}

	public boolean save() throws IOException
	{
		return project.save();
	}

	public void release()
	{
		project.release();
	}

	public TestSuite addNewTestSuite( String name )
	{
		return ( ( WsdlProject )project ).addNewTestSuite( name );
	}

	public boolean hasNature( String natureId )
	{
		return project.hasNature( natureId );
	}

	public MockService addNewMockService( String name )
	{
		return project.addNewMockService( name );
	}

	public MockService getMockServiceAt( int index )
	{
		return project.getMockServiceAt( index );
	}

	public MockService getMockServiceByName( String mockServiceName )
	{
		return project.getMockServiceByName( mockServiceName );
	}

	public int getMockServiceCount()
	{
		return project.getMockServiceCount();
	}

	public List<MockService> getMockServiceList()
	{
		return project.getMockServiceList();
	}

	public List<TestSuite> getTestSuiteList()
	{
		return project.getTestSuiteList();
	}

	public EndpointStrategy getEndpointStrategy()
	{
		return project.getEndpointStrategy();
	}

	public List<Interface> getInterfaceList()
	{
		return project.getInterfaceList();
	}

	public String getPath()
	{
		return project.getPath();
	}

	public boolean isDisabled()
	{
		return project.isDisabled();
	}

	public boolean isOpen()
	{
		return project.isOpen();
	}

	public List<? extends ModelItem> getChildren()
	{
		return project.getChildren();
	}

	public String getId()
	{
		return project.getId();
	}

	public void addTestPropertyListener( TestPropertyListener arg0 )
	{
		project.addTestPropertyListener( arg0 );
	}

	public ModelItem getModelItem()
	{
		return project.getModelItem();
	}

	public Map<String, TestProperty> getProperties()
	{
		return project.getProperties();
	}

	public TestProperty getProperty( String arg0 )
	{
		return project.getProperty( arg0 );
	}

	public String[] getPropertyNames()
	{
		return project.getPropertyNames();
	}

	public String getPropertyValue( String arg0 )
	{
		return project.getPropertyValue( arg0 );
	}

	public boolean hasProperty( String arg0 )
	{
		return project.hasProperty( arg0 );
	}

	public void removeTestPropertyListener( TestPropertyListener arg0 )
	{
		project.removeTestPropertyListener( arg0 );
	}

	public void setPropertyValue( String arg0, String arg1 )
	{
		project.setPropertyValue( arg0, arg1 );
	}

	public ModelItem getParent()
	{
		return project.getParent();
	}

	public String getResourceRoot()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getShadowPassword()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void inspect()
	{
		// TODO Auto-generated method stub

	}

	public void setShadowPassword( String password )
	{
		project.setShadowPassword( password );
	}

	public String getPropertiesLabel()
	{
		return project.getPropertiesLabel();
	}

	public TestProperty getPropertyAt( int index )
	{
		return project.getPropertyAt( index );
	}

	public int getPropertyCount()
	{
		return project.getPropertyCount();
	}

	public List<TestProperty> getPropertyList()
	{
		return project.getPropertyList();
	}

	public int getIndexOfTestSuite( TestSuite testSuite )
	{
		return project.getIndexOfTestSuite( testSuite );
	}
}
