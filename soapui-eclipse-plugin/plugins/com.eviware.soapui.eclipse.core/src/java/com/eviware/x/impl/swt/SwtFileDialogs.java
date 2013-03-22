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

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.eviware.soapui.eclipse.SoapUIActivator;
import com.eviware.x.dialogs.XFileDialogs;

/**
 * 
 * @author Lars H
 */
public class SwtFileDialogs implements XFileDialogs
{
	private static Object result = null;

	public File openWsdl( final String title )
	{
      SwtDialogs.threadLog.debug("SwtFileDialogs.openWsdl -> runInUIThread");
		SwtDialogs.runInUIThread( new Runnable()
		{
			public void run()
			{
            SwtDialogs.threadLog.debug("SwtFileDialogs.openWsdl: run");
				FileDialog dialog = new FileDialog( SwtDialogs.getShell(), SWT.APPLICATION_MODAL
							| SWT.OPEN );
				dialog.setText( title );

				dialog.setFilterExtensions( new String[] { "*.wsdl" } );
				dialog.setFilterNames( new String[] { "WSDL filesx" } );

				String _result = dialog.open();
				result = _result == null ? null : new File( _result );
			}
		} );

		return ( File ) result;
	}

	public File open( Object action, final String title, final String extension,
				final String fileType, final String current )
	{
      return openFile( title, extension, fileType, current, SWT.APPLICATION_MODAL | SWT.OPEN );
   }

   private static File openFile(final String title, final String extension,
         final String fileType, final String current, final int style )
   {
      SwtDialogs.threadLog.debug("SwtFileDialogs.openFile -> runInUIThread");
		SwtDialogs.runInUIThread( new Runnable()
		{
			public void run()
			{
            SwtDialogs.threadLog.debug("SwtFileDialogs.openFile: run");
				Shell shell = SwtDialogs.getShell();
            FileDialog dialog = new FileDialog( shell, style );
				dialog.setText( title );
            dialog.setFileName(current);

				if( extension != null && fileType != null )
				{
					dialog.setFilterExtensions( new String[] { fixExtension( extension ) } );
					dialog.setFilterNames( new String[] { fileType } );
				}

				String _result = dialog.open();
				result = _result == null ? null : new File( _result );
			}
		} );

		return ( File ) result;
	}

	public File openDirectory( Object action, final String title, File defaultDirectory )
	{
      SwtDialogs.threadLog.debug("SwtFileDialogs.openDirectory -> runInUIThread");
		SwtDialogs.runInUIThread( new Runnable()
		{
			public void run()
			{
            SwtDialogs.threadLog.debug("SwtFileDialogs.openDirectory: run");
				DirectoryDialog dialog = new DirectoryDialog( SwtDialogs.getShell(),
							SWT.APPLICATION_MODAL | SWT.OPEN );
				dialog.setText( title );

				String _result = dialog.open();
				result = _result == null ? null : new File( _result );
			}
		} );

		return ( File ) result;
	}

   public File openFileOrDirectory(Object action, String title, File defaultDirectory)
   {
      // TODO Eclipse: openFileOrDirectory is not implemented, only files can be selected.
      //      For now, this is only called from Pro code, so it is not a problem in practice.
      //      But I have no idea when it will become a problem.
      SoapUIActivator.logError(title + ": openFileOrDirectory not fully implemented", new Exception());

      String current = (defaultDirectory != null ? defaultDirectory.toString() : null);
      return open( action, title, null, null, current );
   }

	public File openXML( Object action, String title )
	{
		return open( action, title, "*.xml", "XML files", null );
	}

	public File saveAs( Object action, final String title, final String extension,
				final String fileType, final File defaultFile )
	{
      SwtDialogs.threadLog.debug("SwtFileDialogs.saveAs[" + title + "] -> runInUIThread");
		SwtDialogs.runInUIThread( new Runnable()
		{
			public void run()
			{
            SwtDialogs.threadLog.debug("SwtFileDialogs.saveAs: run");
				FileDialog dialog = new FileDialog( SwtDialogs.getShell(), SWT.APPLICATION_MODAL
							| SWT.SAVE );
				dialog.setText( title );

				if( defaultFile != null )
					dialog.setFileName( defaultFile.getName() );

				if( extension != null && fileType != null )
				{
					dialog.setFilterExtensions( new String[] { fixExtension( extension ) } );
					dialog.setFilterNames( new String[] { fileType } );
				}

				String _result = dialog.open();
				result = _result == null ? null : new File( _result );
			}
		} );

		return ( File ) result;
	}

   public File saveAsDirectory(Object action, final String title, final File defaultDirectory)
   {
      SwtDialogs.threadLog.debug("SwtFileDialogs.saveAsDirectory[" + title + "] -> runInUIThread");
      SwtDialogs.runInUIThread( new Runnable()
      {
         public void run()
         {
            SwtDialogs.threadLog.debug("SwtFileDialogs.saveAs: run");
            DirectoryDialog dialog = new DirectoryDialog( SwtDialogs.getShell(),
                  SWT.APPLICATION_MODAL | SWT.SAVE );
            dialog.setText( title );

//            if( defaultDirectory != null )
//               dialog.setFileName( defaultDirectory.getName() );

            String _result = dialog.open();
            result = _result == null ? null : new File( _result );
         }
      } );

      return ( File ) result;
   }

	public File saveAs( Object action, String title )
	{
		return saveAs( action, title, null, null, null );
	}

	private static String fixExtension( String extension )
	{
		if( extension.startsWith( "*." ) )
			return extension;
		else if( extension.startsWith( "." ) )
			return "*" + extension;
		else
			return "*." + extension;
	}
}
