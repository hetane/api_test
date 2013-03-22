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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.eviware.soapui.support.Tools;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.swing.ActionList;
import com.eviware.soapui.support.action.swing.DefaultActionList;
import com.eviware.x.form.XForm;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.XFormDialogBuilder;

/**
 * 
 * @author Lars H
 */
public class SwtXFormDialogBuilder extends XFormDialogBuilder
{
   private static Image toolsImage;
	private static Image prefsImage;
	private SwtTabbedFormDialog dialog;
	private final String name;
   
   public SwtXFormDialogBuilder(String name)
   {
      super();
		this.name = name;
		
      // TODO Eclipse: Don't create a tabbed form if there is only 1 form.
      dialog = new SwtTabbedFormDialog(name);
   }
   
   @Override
   public XForm createForm(String name)
   {
      return dialog.createForm(name);
   }

   @Override
   public XFormDialog buildDialog(ActionList actions, String description, ImageIcon icon )
   {
      dialog.setActions( actions );
      dialog.setTitle( name );
      dialog.setMessage( description );
      
      Image image = getSwtImage( icon );
      if( image != null )
      	dialog.setTitleImage( image );
      
      return dialog;
   }

	public static Image getSwtImage(ImageIcon icon)
	{
		if( icon == UISupport.TOOL_ICON )
      {
		   if( toolsImage == null )
		      toolsImage = loadImage( "/applications-system-eclipse.png" );
		   return toolsImage;
      }
      else if( icon == UISupport.OPTIONS_ICON )
      {
         if( prefsImage == null )
            prefsImage = loadImage( "/preferences-system-eclipse.png" );
         return prefsImage;
      }
      else
      {
         return null;
      }
	}
   
   private static Image loadImage(String name)
   {
      try
      {
         // TODO Eclipse: dispose image when ready. Use Images?
         URL resource = SwtFormDialog.class.getResource( name );
         if(resource != null)
            return new Image( Display.getCurrent(), resource.openStream());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      
      return null;
   }

	@Override
   public ActionList buildOkCancelActions()
	{
		DefaultActionList actions = new DefaultActionList("Actions");
		actions.addAction( new OKAction() );
		actions.addAction( new CancelAction() );
		return actions;
	}
	
	@Override
   public ActionList buildOkCancelHelpActions(String url)
	{
		DefaultActionList actions = (DefaultActionList) buildOkCancelActions();
		actions.addAction( new HelpAction( url ));
		return actions;
	}

	protected final class OKAction extends AbstractAction
	{
		public OKAction()
		{
			super( "OK" );
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if( dialog != null )
			{
				dialog.setReturnValue( XFormDialog.OK_OPTION );
				dialog.setVisible( false );
			}
		}
	}
	
	protected final class CancelAction extends AbstractAction
	{
		public CancelAction()
		{
			super( "Cancel" );
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if( dialog != null )
			{
				dialog.setReturnValue( XFormDialog.CANCEL_OPTION );
				dialog.setVisible( false );
			}
		}
	}
	
	protected final class HelpAction extends AbstractAction
	{
		private final String url;

		public HelpAction( String url )
		{
			this( "Online Help", url, KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
		}
		
		public HelpAction( String title, String url )
		{
			this( title, url, null );
		}
		
		public HelpAction( String title, String url, KeyStroke accelerator )
	   {
	      super( title );
			this.url = url;
	      putValue( Action.SHORT_DESCRIPTION, "Show online help" );
	      if( accelerator != null )
	      	putValue( Action.ACCELERATOR_KEY, accelerator );
	      
	      putValue( Action.SMALL_ICON, UISupport.createImageIcon("/online_help.gif"));
	   }
	   
	   public void actionPerformed(ActionEvent e)
		{
	   	Tools.openURL( url );
		}
	}

   @Override
   public XFormDialog buildWizard(String arg0, ImageIcon arg1, String arg2)
   {
      // TODO Eclipse: Not implemented wizards.
      return null;
   }
}
