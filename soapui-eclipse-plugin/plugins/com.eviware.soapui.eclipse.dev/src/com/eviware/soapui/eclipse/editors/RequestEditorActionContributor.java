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

package com.eviware.soapui.eclipse.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;

import com.eviware.soapui.eclipse.Images;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.impl.wsdl.submit.transports.http.WsdlResponse;
import com.eviware.soapui.model.iface.Submit;
import com.eviware.soapui.model.iface.SubmitContext;
import com.eviware.soapui.model.iface.SubmitListener;
import com.eviware.soapui.model.iface.Request.SubmitException;
import com.eviware.soapui.model.iface.Submit.Status;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.xml.XmlUtils;

/**
 * 
 * @author Lars H
 */
public class RequestEditorActionContributor extends BasicTextEditorActionContributor
{
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger( RequestEditorActionContributor.class );

	private class InternalSubmitListener implements SubmitListener
	{
		public boolean beforeSubmit( Submit submit, SubmitContext context )
		{
			cancelAction.setEnabled( true );
			return true;
		}

		public void afterSubmit( Submit submit, SubmitContext context )
		{
			if( submit.getRequest() != request )
				return;

			final Status status = submit.getStatus();
			WsdlResponse response = ( WsdlResponse )submit.getResponse();
			if( status != Status.CANCELED )
			{
				// setEnabled(true);
				request.setResponse( response, context );
			}

			final String message;
			final String infoMessage;
			String requestName = request.getOperation().getInterface().getName() + "." + request.getOperation().getName()
					+ ":" + request.getName();

			// TODO Eclipse: Find the editor that sent the request.
			IEditorPart editorPart = getActiveEditorPart();
			final ModelItemEditor messageEditor = ( editorPart instanceof ModelItemEditor ? ( ModelItemEditor )editorPart
					: null );

			if( status == Status.CANCELED )
			{
				message = "CANCELED";
				infoMessage = "[" + requestName + "] - CANCELED";
			}
			else
			{
				if( status == Status.ERROR || response == null )
				{
					message = "Error getting response; " + submit.getError();
					infoMessage = "Error getting response for [" + requestName + "]; " + submit.getError();
				}
				else
				{
					message = "response time: " + response.getTimeTaken() + "ms (" + response.getContentLength() + " bytes)";
					infoMessage = "Got response for [" + requestName + "] in " + response.getTimeTaken() + "ms ("
							+ response.getContentLength() + " bytes)";

					// Show response
					if( messageEditor != null )
					{
						Display.getDefault().asyncExec( new Runnable()
						{
							public void run()
							{
								messageEditor.showResponseView();
								messageEditor.setFocus();
							}
						} );
					}
				}
			}

			log.info( infoMessage );

			// Show normal status or error status.
			if( messageEditor != null )
			{
				Display.getDefault().asyncExec( new Runnable()
				{
					public void run()
					{
						// TODO Eclipse: Show status or error in the status field.
						// messageEditor.setStatusText(message, status !=
						// Status.ERROR);

						if( status == Status.ERROR )
							UISupport.getDialogs().showErrorMessage( message ); // "Error sending request");
					}
				} );
			}

			RequestEditorActionContributor.this.submit = null;

			submitAction.setEnabled( true );
			cancelAction.setEnabled( false );
		}
	}

	// TODO Eclipse: Reuse the Swing actions.
	private class SubmitAction extends Action
	{
		SubmitAction()
		{
			super( "Submit request to specified endpoint URL", Images.getImageDescriptor( "submit_request.gif" ) );
		}

		@Override
		public void run()
		{
			this.setEnabled( false );

			// TODO Eclipse: Handle unsaved changes in the request editor.
			// Now, the user has to save before submitting.
			try
			{
				submit = request.submit( new WsdlSubmitContext( null ), true );
			}
			catch( SubmitException e )
			{
				UISupport.getDialogs().showErrorMessage( "Could not submit request: " + e.getMessage() );
				e.printStackTrace();
				this.setEnabled( true );
			}
		}
	}

	private class RecreateAction extends Action
	{
		RecreateAction()
		{
			super( "Recreates a default request from the schema", Images.getImageDescriptor( "recreate_request.gif" ) );
		}

		@Override
		public void run()
		{
			boolean createOptional = UISupport.getDialogs().confirm( "Create optional elements in schema?",
					"Recreate Request" );

			// TODO Eclipse: This may fail (connection refused) in another thread
			// (SwingWorker)
			// and we don't get an exception here.
			String req = ( ( WsdlOperation )request.getOperation() ).createRequest( createOptional );

			if( request.getRequestContent() != null && request.getRequestContent().trim().length() > 0 )
			{
				boolean option = UISupport.getDialogs().confirm( "Keep existing values?", "Recreate Request" );
				if( option )
				{
					req = XmlUtils.transferValues( request.getRequestContent(), req );
				}
			}

			if( req == null )
				throw new RuntimeException( "Recreate request failed" );

			request.setRequestContent( req );

			IEditorPart editorPart = getActiveEditorPart();
			if( editorPart instanceof ModelItemEditor )
			{
				// Refresh the editor after Recreate request.
				ModelItemEditor messageEditor = ( ModelItemEditor )editorPart;
				messageEditor.updateRequest();
			}
		}
	}

	private class SetEndpointAction extends Action
	{
		SetEndpointAction()
		{
			super( "Select endpoint", Images.getImageDescriptor( "set_endpoint.gif" ) );
		}

		@Override
		public void run()
		{
			String oldEndpoint = request.getEndpoint();
			String newEndpoint = UISupport.getDialogs().prompt( "Enter the new endpoint:", "Select endpoint", oldEndpoint );
			if( newEndpoint == null )
				return;

			request.setEndpoint( newEndpoint );
		}
	}

	private class CancelAction extends Action
	{
		CancelAction()
		{
			super( "Aborts ongoing request", Images.getImageDescriptor( "cancel_request.gif" ) );
			setEnabled( false );
		}

		@Override
		public void run()
		{
			submit.cancel();
			setEnabled( false );
		}
	}

	private final InternalSubmitListener submitListener = new InternalSubmitListener();

	private final IAction submitAction = new SubmitAction();
	private final IAction cancelAction = new CancelAction();
	private final IAction recreateAction = new RecreateAction();
	private final IAction setEndpointAction = new SetEndpointAction();

	private final IAction[] actions = new IAction[] { submitAction, cancelAction, recreateAction, setEndpointAction };

	// The request in the current active editor.
	private WsdlRequest request;

	// The currently submitted request.
	// TODO Associate this with the editor that submitted it.
	private Submit submit;

	public RequestEditorActionContributor()
	{
	}

	@Override
	public void setActiveEditor( IEditorPart ed )
	{
		super.setActiveEditor( ed );

		if( request != null )
			request.removeSubmitListener( submitListener );

		IEditorInput editorInput = ed.getEditorInput();
		if( editorInput instanceof RequestEditorInput )
		{
			RequestEditorInput requestInput = ( RequestEditorInput )editorInput;
			request = requestInput.getRequest();
			request.addSubmitListener( submitListener );
		}
		else if( editorInput instanceof DesktopPanelEditorInput )
		{
			DesktopPanelEditorInput requestInput = ( DesktopPanelEditorInput )editorInput;
			// request = requestInput.getRequest();
			// request.addSubmitListener(submitListener);
		}
		else
			request = null;

		submitAction.setEnabled( request != null );
	}

	/**
	 * Set up buttons.
	 */
	@Override
	public void contributeToToolBar( IToolBarManager toolBarManager )
	{
		super.contributeToToolBar( toolBarManager );
		for( IAction a : actions )
			toolBarManager.add( a );
	}
}
