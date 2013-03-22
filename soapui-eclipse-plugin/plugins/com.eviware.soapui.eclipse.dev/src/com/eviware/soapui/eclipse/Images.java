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

package com.eviware.soapui.eclipse;

import java.lang.reflect.Field;
import java.net.URL;

import javax.swing.ImageIcon;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import com.eviware.soapui.SoapUI;

/**
 * Cache and dispose images.
 * 
 * @author Lars H
 */
public class Images
{
	private static ImageRegistry registry;

	private static ImageRegistry getRegistry()
	{
		if( registry == null )
			registry = new ImageRegistry();
		return registry;
	}

	private Images()
	{
	}

	public static ImageDescriptor getImageDescriptor( String fileName )
	{
		ImageRegistry reg = getRegistry();
		ImageDescriptor descriptor = reg.getDescriptor( fileName );
		if( descriptor == null )
		{
			descriptor = createImageDescriptor( fileName );
			reg.put( fileName, descriptor );
		}
		return descriptor;
	}

	private static ImageDescriptor createImageDescriptor( String fileName )
	{
		URL url = SoapUI.class.getResource( fileName );
		return ImageDescriptor.createFromURL( url );
	}

	public static Image getImage( String fileName )
	{
		ImageRegistry reg = getRegistry();
		Image image = reg.get( fileName );
		if( image == null )
		{
			ImageDescriptor descriptor = createImageDescriptor( fileName );
			reg.put( fileName, descriptor );
			image = reg.get( fileName );
		}

		return image;
	}

	public static Image getImage( ImageIcon icon )
	{
		Class<? extends ImageIcon> imageIcon = icon.getClass();
		try
		{
			Field imageField = imageIcon.getDeclaredField( "location" );
			imageField.setAccessible( true );
			URL location = ( URL )imageField.get( icon );
			return getImage( location.getFile() );
		}
		catch( SecurityException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( NoSuchFieldException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( IllegalArgumentException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( IllegalAccessException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void dispose()
	{
		if( registry != null )
		{
			registry.dispose();
			registry = null;
		}
	}
}
