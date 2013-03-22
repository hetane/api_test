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

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.eviware.soapui.support.UISupport;
import com.eviware.x.dialogs.Worker;
import com.eviware.x.dialogs.XProgressDialog;
import com.eviware.x.dialogs.XProgressMonitor;

/**
 * 
 * @author Lars H
 */
public class SwtProgressDialog implements XProgressDialog
{
   private final static Logger log = Logger.getLogger(SwtProgressDialog.class);

	private String label;
	private int length;
	private Worker worker;
	private final boolean canCancel;
   private volatile Exception error;

   public SwtProgressDialog(String label, int length, String initialValue, boolean canCancel)
	{
		this.label = label;
		this.length = length;
		this.canCancel = canCancel;
	}

	public void setVisible(boolean visible)
	{
	}

	public void run(Worker worker) throws Exception
	{
		this.worker = worker;
      this.error = null;
		
      if(Display.getCurrent() != null)
      {
         runInternal();
      }
      else
      {
         // Warning: Display.syncExec may deadlock with other threads.
         log.debug("Display.syncExec");
         Display.getDefault().syncExec(new Runnable()
         {
            public void run()
            {
               runInternal();
            }
         });
      }
      
      if(error != null)
         throw error;
	}
   
   private void runInternal()
   {
      ProgressMonitorDialog dialog = null;
      try
      {
         // getActiveShell() fails if not called from an UI thread.
         Shell shell = Display.getDefault().getActiveShell();
         
         InternalRunnableWithProgress runnable = new InternalRunnableWithProgress();
         dialog = new ProgressMonitorDialog(shell)
         {
            @Override
            protected void cancelPressed()
            {
               if( worker.onCancel() )
               {
               	super.cancelPressed();
               	getProgressMonitor().setCanceled(true);
               	close();
               }
            }
         };
         
         dialog.setCancelable( canCancel );
         
         boolean fork = true;
			dialog.run(true, canCancel, new InternalRunnableWithProgress());
		}
		catch (InvocationTargetException e)
		{
			if(dialog != null) dialog.close();
			if (e.getCause() instanceof Error) throw (Error) e.getCause();
			Exception cause = (Exception) e.getCause();
			UISupport.showErrorMessage(cause);
		}
		catch (InterruptedException e)
		{
         if(dialog != null) dialog.close();
         if (e.getCause() instanceof Error) throw (Error) e.getCause();
         error = (Exception) e.getCause();
         UISupport.showErrorMessage(error);
      }
   }
   
   private class InternalRunnableWithProgress implements IRunnableWithProgress
   {
      public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
      {
         XProgressMonitor monitorAdapter = new XProgressMonitor()
         {
            public void setProgress(int worked, String string)
            {
               monitor.worked(worked);
               monitor.subTask(string);
            }
         };
         
         monitor.beginTask(label, length);
         worker.construct(monitorAdapter);
         worker.finished();
         monitor.done();
      }      
   }

	public void setCancelLabel(String label)
	{
		// TODO Auto-generated method stub
		
	}
}
