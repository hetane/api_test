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

import java.awt.Dimension;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.eviware.x.dialogs.XDialogs;
import com.eviware.x.dialogs.XProgressDialog;

/**
 * 
 * @author Lars H
 */
public class SwtDialogs implements XDialogs
{
   public static final Logger threadLog = Logger.getLogger( "com.eviware.soapui.threads" );
   
	private static Object result = null;

	public static Display getDisplay()
	{
		return Display.getDefault();
	}

	public static Shell getShell()
	{
		Shell activeShell = getDisplay().getActiveShell();
		if( activeShell == null )
			activeShell = new Shell( getDisplay() );
		
		return activeShell;
	}

	public boolean confirm( final String question, final String title )
	{
      threadLog.debug("SwtDialogs.confirm[" + title + "] -> runInUIThread");
		runInUIThread( new Runnable()
		{
			public void run()
			{
            threadLog.debug("SwtDialogs.confirm: run");
			   result = new Boolean( MessageDialog.openQuestion( getShell(), title, question ));
			}
		});
		
		return ((Boolean)result).booleanValue();
	}

	public Boolean confirmOrCancel( final String question, final String title )
	{
      threadLog.debug("SwtDialogs.confirmOrCancel[" + title + "] -> runInUIThread");
		runInUIThread( new Runnable()
		{
			public void run()
			{
            threadLog.debug("SwtDialogs.confirmOrCancel: run");
				MessageDialog dialog = new MessageDialog( getShell(), title, null, question,
							MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL,
										IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 0 ); // OK
				
				result = new Boolean(  dialog.open() == 0 );
			}
		});

		return ( Boolean ) result;
	}
	

	public String prompt( final String question, final String title, final String value )
	{
      threadLog.debug("SwtDialogs.prompt[" + title + "] -> runInUIThread");
		runInUIThread( new Runnable()
		{
			public void run()
			{
            threadLog.debug("SwtDialogs.prompt: run");
				InputDialog dialog = new InputDialog( getShell(), title, question, value, null );
				if( dialog.open() == Window.OK )
					result = dialog.getValue();
				else
					result = null;
			}
		} );

		return ( String ) result;
	}

	public String prompt( final String question, final String title )
	{
	   return openInputDialog(question, title, 0);
	}
	
	private String openInputDialog( final String question, final String title, final int styleBits )
	{
      threadLog.debug("SwtDialogs.prompt[" + title + "] -> runInUIThread");
		runInUIThread( new Runnable()
		{
			public void run()
			{
            threadLog.debug("SwtDialogs.prompt: run");
				InputDialog dialog = new InputDialog( getShell(), title, question, null, null )
				{
				   @Override
				   protected int getInputTextStyle()
				   {
				      return super.getInputTextStyle() | styleBits;
				   }
				};
				if( dialog.open() == Window.OK )
					result = dialog.getValue();
				else
					result = null;
			}
		});
		
		return ( String ) result;
	}

	public Object prompt( final String question, final String title, final Object[] objects )
	{
      threadLog.debug("SwtDialogs.prompt[" + title + "] -> runInUIThread");
		runInUIThread( new Runnable()
		{
			public void run()
			{
            threadLog.debug("SwtDialogs.prompt: run");
				SwtSelectDialog dialog = new SwtSelectDialog( getShell(), question, title, objects );
				if( dialog.open() == Window.OK )
					result = dialog.getSelection();
				else
					result = null;
			}
		} );

		return result;
	}

	public static void runInUIThread( Runnable runnable )
	{
		Display display = Display.getCurrent();
		if( display == null )
		{
         threadLog.debug("runInUIThread: syncExec " + runnable.getClass().getName());
			Display.getDefault().syncExec( runnable );
		}
		else
		{
         threadLog.debug("runInUIThread: run " + runnable.getClass().getName());
			runnable.run();
		}
	}

   public static void runInUIThreadAsync( Runnable runnable )
   {
      Display display = Display.getCurrent();
      if( display == null )
      {
         threadLog.debug("runInUIThreadAsync: asyncExec " + runnable.getClass().getName());
         Display.getDefault().asyncExec( runnable );
      }
      else
      {
         threadLog.debug("runInUIThreadAsync: run " + runnable.getClass().getName());
         runnable.run();
      }
   }
   
	public Object prompt( final String question, final String title, final Object[] objects, final String value )
	{
      threadLog.debug("SwtDialogs.prompt[" + title + "] -> runInUIThread");
		runInUIThread( new Runnable()
		{
			public void run()
			{
            threadLog.debug("SwtDialogs.prompt: run");
				SwtSelectDialog dialog = new SwtSelectDialog( getShell(), question, title, objects );
				dialog.setInitialSelection( value );

				if( dialog.open() == Window.OK )
					result = dialog.getSelection();
				else
					result = null;
			}
		} );

		return result;
	}

	public void showErrorMessage( final String message )
	{
      threadLog.debug("SwtDialogs.showErrorMessage[" + message+ "] -> runInUIThread");
		runInUIThread( new Runnable()
		{
			public void run()
			{
            threadLog.debug("SwtDialogs.showErrorMessage: run");
				MessageDialog.openError( getShell(), "Error", message );
			}
		} );
	}

	public void showInfoMessage( final String message )
	{
      threadLog.debug("SwtDialogs.showInfoMessage[" + message + "] -> runInUIThread");
		runInUIThread( new Runnable()
		{
			public void run()
			{
            threadLog.debug("SwtDialogs.showInfoMessage: run");
				MessageDialog.openInformation( getShell(), "Information", message );
			}
		} );
	}

	public void showInfoMessage( final String message, final String title )
	{
      threadLog.debug("SwtDialogs.showInfoMessage[" + message + "] -> runInUIThread");
		runInUIThread( new Runnable()
		{
			public void run()
			{
            threadLog.debug("SwtDialogs.showInfoMessage: run");
				MessageDialog.openInformation( getShell(), title, message );
			}
		} );
	}

	public XProgressDialog createProgressDialog( String label, int length, String initialValue,
				boolean canCancel )
	{
		return new SwtProgressDialog( label, length, initialValue, canCancel );
	}

   public void showExtendedInfo( String title, String description, String content, Dimension size  )
   {
      SwtExtendedInfoDialog dialog = new SwtExtendedInfoDialog( getShell(), title ); 
      dialog.setMessage(description);
      dialog.setContent(content);
      dialog.setSize(size);
      dialog.addButton(IDialogConstants.OK_ID, IDialogConstants.OK_LABEL);
      dialog.show();
   }
   
   public boolean confirmExtendedInfo( String title, String description, String content, Dimension size )
   {
      SwtExtendedInfoDialog dialog = new SwtExtendedInfoDialog( getShell(), title ); 
      dialog.setMessage(description);
      dialog.setContent(content);
      dialog.setSize(size);
      dialog.addButton(IDialogConstants.OK_ID, IDialogConstants.OK_LABEL);
      dialog.addButton(IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL);
      Boolean extendedInfoResult = dialog.show();
      return extendedInfoResult == null ? false : extendedInfoResult;
   }
   
   public Boolean confirmOrCancleExtendedInfo( String title, String description, String content, Dimension size)
   {
      SwtExtendedInfoDialog dialog = new SwtExtendedInfoDialog( getShell(), title ); 
      dialog.setMessage(description);
      dialog.setContent(content);
      dialog.setSize(size);
      dialog.addButton(IDialogConstants.YES_ID, IDialogConstants.YES_LABEL);
      dialog.addButton(IDialogConstants.NO_ID, IDialogConstants.NO_LABEL);
      dialog.addButton(IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL);
      return dialog.show();
   }
   
   public int yesYesToAllOrNo(final String question, final String title) {
		threadLog.debug("SwtDialogs.yesYesToAllOrNo[" + title
				+ "] -> runInUIThread");
		runInUIThread(new Runnable() {
			public void run() {
				threadLog.debug("SwtDialogs.confirmOrCancel: run");
				MessageDialog dialog = new MessageDialog(getShell(), title,
						null, question, MessageDialog.QUESTION, new String[] {
								IDialogConstants.YES_LABEL,
								IDialogConstants.YES_TO_ALL_LABEL,
								IDialogConstants.NO_LABEL }, 0); // OK

				result = dialog.open();
			}
		});

		return (Integer) result;
	}
   
   
   public String selectXPath( String title, String info, String xml, String xpath )
   {
      return prompt( "Specify XPath expression", "Select XPath", xpath );
   }

   public char[] promptPassword(String question, String title)
   {
      return openInputDialog(question, title, SWT.PASSWORD).toCharArray();
   }

}
