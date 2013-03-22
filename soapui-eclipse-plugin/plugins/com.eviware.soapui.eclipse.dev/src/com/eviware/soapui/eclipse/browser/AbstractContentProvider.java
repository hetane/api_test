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

package com.eviware.soapui.eclipse.browser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

import com.eviware.soapui.eclipse.actions.EclipseMenuAppender;
import com.eviware.soapui.eclipse.project.SoapuiAdapterFactory;
import com.eviware.soapui.eclipse.util.SelectionUtil;
import com.eviware.soapui.eclipse.util.SwtUtils;
import com.eviware.soapui.impl.rest.RestMethod;
import com.eviware.soapui.impl.rest.RestRequest;
import com.eviware.soapui.impl.rest.RestResource;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.iface.Interface;
import com.eviware.soapui.model.iface.InterfaceListener;
import com.eviware.soapui.model.iface.Operation;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.mock.MockOperation;
import com.eviware.soapui.model.mock.MockResponse;
import com.eviware.soapui.model.mock.MockService;
import com.eviware.soapui.model.mock.MockServiceListener;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.project.ProjectListener;
import com.eviware.soapui.model.support.ProjectListenerAdapter;
import com.eviware.soapui.model.testsuite.LoadTest;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestSuite;
import com.eviware.soapui.model.testsuite.TestSuiteListener;
import com.eviware.soapui.security.SecurityTest;

/**
 * This class is an adapter from SoapUI projects to an Eclipse tree model
 * 
 * @author Lars H
 */
public abstract class AbstractContentProvider implements ITreeContentProvider
{
	@SuppressWarnings( "unused" )
	private final static Logger log = Logger.getLogger( AbstractContentProvider.class );

	private static final ModelItem[] NO_ELEMENTS = new ModelItem[0];

	private Viewer viewer;

	private EclipseMenuAppender menuAppender;

	protected final ListenerSupport<Project, ProjectListener> projectListeners;
	protected final ListenerSupport<Interface, InterfaceListener> interfaceListeners;
	protected final ListenerSupport<TestSuite, TestSuiteListener> testSuiteListeners;
	protected final ListenerSupport<MockService, MockServiceListener> mockServiceListeners;
	protected final ListenerSupport<ModelItem, PropertyChangeListener> propertyListeners;

	private IDoubleClickListener doubleClickListener = new IDoubleClickListener()
	{
		public void doubleClick( DoubleClickEvent event )
		{
			ModelItem element = SelectionUtil.getSelectedElement( event.getSelection(), ModelItem.class );
			if( element != null )
				open( element );
		}
	};

	protected abstract static class ListenerSupport<M, L>
	{
		private HashSet<M> listeningTo = new HashSet<M>();

		private L listener;

		public ListenerSupport( L listener )
		{
			this.listener = listener;
		}

		public void addListener( M element )
		{
			if( !listeningTo.contains( element ) )
			{
				addListenerImpl( element, listener );
				listeningTo.add( element );
			}
		}

		public void removeListenersForRemovedElements( M[] remainingElements )
		{
			HashSet<M> removed = new HashSet<M>( listeningTo );

			for( M element : remainingElements )
			{
				removed.remove( element );
			}

			for( M modelElement : removed )
			{
				removeListenerImpl( modelElement, listener );
			}
		}

		public void removeListener( M element )
		{
			if( listeningTo.contains( element ) )
			{
				removeListenerImpl( element, listener );
				listeningTo.remove( element );
			}
		}

		public void removeAll()
		{
			for( M modelItem : listeningTo )
			{
				removeListenerImpl( modelItem, listener );
			}
			listeningTo.clear();
		}

		protected abstract void addListenerImpl( M model, L listener );

		protected abstract void removeListenerImpl( M model, L listener );
	}

	public AbstractContentProvider()
	{
		projectListeners = new ListenerSupport<Project, ProjectListener>( new InternalProjectListener() )
		{
			protected void addListenerImpl( Project project, ProjectListener listener )
			{
				project.addProjectListener( listener );
			}

			protected void removeListenerImpl( Project project, ProjectListener listener )
			{
				project.removeProjectListener( listener );
			}
		};

		interfaceListeners = new ListenerSupport<Interface, InterfaceListener>( new InternalInterfaceListener() )
		{
			protected void addListenerImpl( Interface iface, InterfaceListener listener )
			{
				iface.addInterfaceListener( listener );
			}

			protected void removeListenerImpl( Interface iface, InterfaceListener listener )
			{
				iface.removeInterfaceListener( listener );
			}
		};

		testSuiteListeners = new ListenerSupport<TestSuite, TestSuiteListener>( new InternalTestSuiteListener() )
		{
			protected void addListenerImpl( TestSuite testSuite, TestSuiteListener listener )
			{
				testSuite.addTestSuiteListener( listener );
			}

			protected void removeListenerImpl( TestSuite testSuite, TestSuiteListener listener )
			{
				testSuite.removeTestSuiteListener( listener );
			}
		};

		mockServiceListeners = new ListenerSupport<MockService, MockServiceListener>( new InternalMockServiceListener() )
		{
			protected void addListenerImpl( MockService mockService, MockServiceListener listener )
			{
				mockService.addMockServiceListener( listener );
			}

			protected void removeListenerImpl( MockService mockService, MockServiceListener listener )
			{
				mockService.removeMockServiceListener( listener );
			}
		};

		propertyListeners = new ListenerSupport<ModelItem, PropertyChangeListener>( new InternalPropertyChangeListener() )
		{
			protected void addListenerImpl( ModelItem item, PropertyChangeListener listener )
			{
				//item.addPropertyChangeListener( ModelItem.NAME_PROPERTY, listener );
				//item.addPropertyChangeListener( ModelItem.ICON_PROPERTY, listener );
				item.addPropertyChangeListener( listener );
			}

			protected void removeListenerImpl( ModelItem iface, PropertyChangeListener listener )
			{
				//iface.removePropertyChangeListener( ModelItem.NAME_PROPERTY, listener );
				//iface.removePropertyChangeListener( ModelItem.ICON_PROPERTY, listener );
				iface.removePropertyChangeListener( listener );
			}
		};
	}

	/**
	 * Call this when the menu is created.
	 */
	protected void initMenuAppender()
	{
		if( menuAppender == null )
		{
			menuAppender = EclipseMenuAppender.createIfReady( viewer );
		}
	}

	public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
	{
		if( this.viewer instanceof StructuredViewer )
		{
			StructuredViewer oldViewer = ( StructuredViewer )this.viewer;
			oldViewer.removeDoubleClickListener( doubleClickListener );
		}

		this.viewer = viewer;

		if( viewer instanceof StructuredViewer )
		{
			StructuredViewer newViewer = ( StructuredViewer )viewer;
			newViewer.addDoubleClickListener( doubleClickListener );
		}
	}

	protected void open( ModelItem element )
	{
	}

	protected Viewer getViewer()
	{
		return viewer;
	}

	public Object[] getElements( Object inputElement )
	{
		return null;
	}

	public Object[] getChildren( Object parent )
	{
		ModelItem[] elements = NO_ELEMENTS;
		if( parent instanceof Project )
		{
			Project project = ( Project )parent;
			int interfaceCount = project.getInterfaceCount();
			int testSuiteCount = project.getTestSuiteCount();
			int mockServiceCount = project.getMockServiceCount();
			elements = new ModelItem[interfaceCount + testSuiteCount + mockServiceCount];
			for( int i = 0; i < interfaceCount; i++ )
			{
				Interface iface = project.getInterfaceAt( i );
				elements[i] = iface;
				propertyListeners.addListener( elements[i] );
			}
			for( int i = 0; i < testSuiteCount; i++ )
			{
				TestSuite testSuite = project.getTestSuiteAt( i );
				int index = i + interfaceCount;
				elements[index] = testSuite;
				propertyListeners.addListener( elements[index] );
			}
			for( int i = 0; i < mockServiceCount; i++ )
			{
				MockService mockService = project.getMockServiceAt( i );
				int index = i + interfaceCount + testSuiteCount;
				elements[index] = mockService;
				propertyListeners.addListener( elements[index] );
			}
			projectListeners.addListener( project );
		}
		else if( parent instanceof TestSuite )
		{
			TestSuite testSuite = ( TestSuite )parent;
			elements = new ModelItem[testSuite.getTestCaseCount()];
			for( int i = 0; i < testSuite.getTestCaseCount(); i++ )
			{
				elements[i] = testSuite.getTestCaseAt( i );
				propertyListeners.addListener( elements[i] );
			}
			testSuiteListeners.addListener( testSuite );
		}
		else if( parent instanceof TestCase )
		{
			TestCase testCase = ( TestCase )parent;
			/*
			 * elements = testCase.getChildren().toArray(new ModelItem[] {});
			 * for(ModelItem child : elements) { nameListeners.addListener(child);
			 * }
			 */
			return new Object[] { new TestStepsElement( testCase ), new LoadTestsElement( testCase ),
					new SecurityTestsElement( testCase ) };
		}
		else if( parent instanceof TestStepsElement )
		{
			TestCase testCase = ( ( TestStepsElement )parent ).getTestCase();
			elements = new ModelItem[testCase.getTestStepCount()];
			for( int i = 0; i < testCase.getTestStepCount(); i++ )
			{
				elements[i] = testCase.getTestStepAt( i );
				propertyListeners.addListener( elements[i] );
			}
		}
		else if( parent instanceof LoadTestsElement )
		{
			TestCase testCase = ( ( LoadTestsElement )parent ).getTestCase();
			elements = new ModelItem[testCase.getLoadTestCount()];
			for( int i = 0; i < testCase.getLoadTestCount(); i++ )
			{
				elements[i] = testCase.getLoadTestAt( i );
				propertyListeners.addListener( elements[i] );
			}
		}
		else if( parent instanceof SecurityTestsElement )
		{
			TestCase testCase = ( ( SecurityTestsElement )parent ).getTestCase();
			elements = new ModelItem[testCase.getSecurityTestCount()];
			for( int i = 0; i < testCase.getSecurityTestCount(); i++ )
			{
				elements[i] = testCase.getSecurityTestAt( i );
				propertyListeners.addListener( elements[i] );
			}
		}
		else if( parent instanceof MockService )
		{
			MockService mockService = ( MockService )parent;
			elements = new ModelItem[mockService.getMockOperationCount()];
			for( int i = 0; i < mockService.getMockOperationCount(); i++ )
			{
				elements[i] = mockService.getMockOperationAt( i );
				propertyListeners.addListener( elements[i] );
			}
			mockServiceListeners.addListener( mockService );
		}
		else if( parent instanceof MockOperation )
		{
			MockOperation mockOperation = ( MockOperation )parent;
			elements = new ModelItem[mockOperation.getMockResponseCount()];
			for( int i = 0; i < mockOperation.getMockResponseCount(); i++ )
			{
				elements[i] = mockOperation.getMockResponseAt( i );
				propertyListeners.addListener( elements[i] );
			}
			// Mock response add/remove events are notified to MockServiceListener.
		}
		else if( parent instanceof Interface )
		{
			Interface iface = ( Interface )parent;
			elements = new ModelItem[iface.getOperationCount()];
			for( int i = 0; i < iface.getOperationCount(); i++ )
			{
				elements[i] = iface.getOperationAt( i );
				propertyListeners.addListener( elements[i] );
			}
			interfaceListeners.addListener( iface );
		}
		else if( parent instanceof Operation )
		{
			if( parent instanceof RestResource )
			{
				RestResource resource = ( RestResource )parent;
				elements = new ModelItem[resource.getRestMethodCount()];
				for( int i = 0; i < resource.getRestMethodCount(); i++ )
				{
					elements[i] = resource.getRestMethodAt( i );
					propertyListeners.addListener( elements[i] );
				}
			}
			else
			{
				Operation operation = ( Operation )parent;
				elements = new ModelItem[operation.getRequestCount()];
				for( int i = 0; i < operation.getRequestCount(); i++ )
				{
					elements[i] = operation.getRequestAt( i );
					propertyListeners.addListener( elements[i] );
				}
			}
		}
		else if( parent instanceof RestMethod )
		{
			RestMethod method = ( RestMethod )parent;
			elements = new ModelItem[method.getRequestCount()];
			for( int i = 0; i < method.getRequestCount(); i++ )
			{
				elements[i] = method.getRequestAt( i );
				propertyListeners.addListener( elements[i] );
			}
		}
		return elements;
	}

	public Object getParent( Object element )
	{
		if( element instanceof Project )
			return SoapuiAdapterFactory.getProjectFile( ( Project )element );
		else if( element instanceof Interface )
			return ( ( Interface )element ).getProject();
		else if( element instanceof Operation )
			return ( ( Operation )element ).getInterface();
		else if( element instanceof Request )
		{
			if( element instanceof RestRequest )
				return ( ( RestRequest )element ).getRestMethod();
			else
				return ( ( Request )element ).getOperation();
		}
		else if( element instanceof RestMethod )
			return ( ( RestMethod )element ).getOperation();
		else if( element instanceof TestSuite )
			return ( ( TestSuite )element ).getProject();
		else if( element instanceof TestCase )
			return ( ( TestCase )element ).getTestSuite();
		else if( element instanceof TestStep )
			return new TestStepsElement( ( ( TestStep )element ).getTestCase() );
		else if( element instanceof TestStepsElement )
			return ( ( TestStepsElement )element ).getTestCase();
		else if( element instanceof LoadTest )
			return new LoadTestsElement( ( ( LoadTest )element ).getTestCase() );
		else if( element instanceof LoadTestsElement )
			return ( ( LoadTestsElement )element ).getTestCase();
		else if( element instanceof SecurityTest )
			return new SecurityTestsElement( ( ( SecurityTest )element ).getTestCase() );
		else if( element instanceof SecurityTestsElement )
			return ( ( SecurityTestsElement )element ).getTestCase();
		else if( element instanceof MockService )
			return ( ( MockService )element ).getProject();
		else
			return null;
	}

	public boolean hasChildren( Object element )
	{
		if( element instanceof IFile )
			return SoapuiAdapterFactory.isSoapuiProject( ( IFile )element );
		if( element instanceof Project )
			return ( ( Project )element ).getInterfaceCount() + ( ( Project )element ).getTestSuiteCount() > 0;
		if( element instanceof Interface )
			return ( ( Interface )element ).getOperationCount() > 0;
		if( element instanceof Operation )
		{
			if( element instanceof RestResource )
				return ( ( RestResource )element ).getRestMethodCount() > 0;
			else
				return ( ( Operation )element ).getRequestCount() > 0;
		}
		if( element instanceof RestMethod )
			return ( ( RestMethod )element ).getRequestCount() > 0;
		if( element instanceof MockService )
			return ( ( MockService )element ).getMockOperationCount() > 0;
		if( element instanceof MockOperation )
			return ( ( MockOperation )element ).getMockResponseCount() > 0;
		if( element instanceof TestSuite )
			return ( ( TestSuite )element ).getTestCaseCount() > 0;
		if( element instanceof TestCase )
		{
			// return ((TestCase)element).getTestStepCount() +
			// ((TestCase)element).getLoadTestCount() > 0;
			return true;
		}
		else if( element instanceof TestStepsElement )
		{
			TestCase testCase = ( ( TestStepsElement )element ).getTestCase();
			return testCase.getTestStepCount() > 0;
		}
		else if( element instanceof LoadTestsElement )
		{
			TestCase testCase = ( ( LoadTestsElement )element ).getTestCase();
			return testCase.getLoadTestCount() > 0;
		}
		else if( element instanceof SecurityTestsElement )
		{
			TestCase testCase = ( ( SecurityTestsElement )element ).getTestCase();
			return testCase.getSecurityTestCount() > 0;
		}
		return false;
	}

	public void dispose()
	{
		projectListeners.removeAll();
		interfaceListeners.removeAll();
		propertyListeners.removeAll();
	}

	private class InternalProjectListener extends ProjectListenerAdapter
	{
		// TODO Check if we have to listen on environment changes
		public void interfaceAdded( Interface iface )
		{
			// When the content is actually a soapui-project.xml file,
			// the view will not be updated if we send the Project.
			// Object element = iface.getProject());
			Object element = null;
			updateView( element );
			interfaceListeners.addListener( iface );
		}

		public void interfaceRemoved( Interface iface )
		{
			// TODO Eclipse jbossws: Adapt IFile "soapui-project.xml" to Project to
			// handle this.
			// When the content is actually a soapui-project.xml file,
			// the view will not be updated if we send the Project.
			// Object element = iface.getProject());
			Object element = null;
			interfaceListeners.removeListener( iface );
			updateView( element );
		}

		public void testSuiteAdded( TestSuite testSuite )
		{
			Object element = null;
			testSuiteListeners.addListener( testSuite );
			updateView( element );
		}

		public void testSuiteRemoved( TestSuite testSuite )
		{
			Object element = null;
			testSuiteListeners.removeListener( testSuite );
			updateView( element );
		}

		public void testSuiteMoved( TestSuite testSuite, int index, int offset )
		{
			Object element = null;
			updateView( element );
		}

		public void mockServiceAdded( MockService mockService )
		{
			Object element = null;
			mockServiceListeners.addListener( mockService );
			updateView( element );
		}

		public void mockServiceRemoved( MockService mockService )
		{
			Object element = null;
			mockServiceListeners.removeListener( mockService );
			updateView( element );
		}
	}

	private class InternalInterfaceListener implements InterfaceListener
	{
		public void operationAdded( Operation element )
		{
			updateView( getParent( element ) );
		}

		public void operationRemoved( Operation element )
		{
			updateView( getParent( element ) );
		}

		public void operationUpdated( Operation operation )
		{
		}

		public void requestAdded( Request element )
		{
			updateView( getParent( element ) );
		}

		public void requestRemoved( Request element )
		{
			updateView( getParent( element ) );
		}
	}

	private class InternalTestSuiteListener implements TestSuiteListener
	{
		public void loadTestAdded( LoadTest element )
		{
			updateView( getParent( element ) );
		}

		public void loadTestRemoved( LoadTest element )
		{
			updateView( getParent( element ) );
		}

		public void testCaseAdded( TestCase element )
		{
			updateView( getParent( element ) );
		}

		public void testCaseRemoved( TestCase element )
		{
			updateView( getParent( element ) );
		}

		public void testCaseMoved( TestCase testCase, int index, int offset )
		{
			updateView( getParent( testCase ) );
		}

		public void testStepAdded( TestStep element, int index )
		{
			updateView( getParent( element ) );
		}

		public void testStepMoved( TestStep element, int fromIndex, int offset )
		{
			updateView( getParent( element ) );
		}

		public void testStepRemoved( TestStep element, int index )
		{
			updateView( getParent( element ) );
		}

		public void securityTestAdded( SecurityTest element )
		{
			updateView( getParent( element ) );
		}

		public void securityTestRemoved( SecurityTest element )
		{
			updateView( getParent( element ) );
		}
	}

	private class InternalMockServiceListener implements MockServiceListener
	{
		public void mockOperationAdded( MockOperation element )
		{
			updateView( getParent( element ) );
		}

		public void mockOperationRemoved( MockOperation element )
		{
			updateView( getParent( element ) );
		}

		public void mockResponseAdded( MockResponse element )
		{
			updateView( getParent( element ) );
		}

		public void mockResponseRemoved( MockResponse element )
		{
			updateView( getParent( element ) );
		}
	}

	private class InternalPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange( PropertyChangeEvent evt )
		{
			// When the content is actually a soapui-project.xml file,
			// the view will not be updated if we send the Project.
			Object element = evt.getSource();
			if( element instanceof Project )
				element = null;

			updateView( element );
		}
	}

	public void updateView( final Object element )
	{
		SwtUtils.INSTANCE.invokeLater( new Runnable()
		{
			public void run()
			{
				try
				{
					if( element != null && viewer instanceof StructuredViewer )
					{
						( ( StructuredViewer )viewer ).refresh( element );
					}
					else if( viewer != null )
					{
						viewer.refresh();
					}
				}
				catch( RuntimeException e )
				{
					e.printStackTrace();
				}
			}
		} );
	}
}
