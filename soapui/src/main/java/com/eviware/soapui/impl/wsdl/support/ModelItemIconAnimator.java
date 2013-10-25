/*
 *  SoapUI, copyright (C) 2004-2012 smartbear.com
 *
 *  SoapUI is free software; you can redistribute it and/or modify it under the
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  SoapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.impl.wsdl.support;

import com.eviware.soapui.model.support.AbstractAnimatableModelItem;
import com.eviware.soapui.support.UISupport;

import javax.swing.*;
import java.util.List;

/**
 * Class to animate the icon of a ModelItem
 *
 * @author ole.matzura
 */

public class ModelItemIconAnimator<T extends AbstractAnimatableModelItem<?>>
{
	private final T target;
	private int index = 0;
	private boolean stopped = true;
	private boolean enabled = true;
	private ImageIcon baseIcon;
	private ImageIcon[] animateIcons;

	SwingWorker<Void, ImageIcon> worker;

	public ModelItemIconAnimator( T target, String baseIcon, String animationBaseIcon, int num, String type )
	{
		this.target = target;
		this.baseIcon = UISupport.createImageIcon( baseIcon );

		animateIcons = new ImageIcon[num];

		for( int c = 0; c < animateIcons.length; c++ )
		{
			animateIcons[c] = UISupport.createImageIcon( createImageName( animationBaseIcon, type, c ) );
		}
	}

	private String createImageName( String animationBaseIcon, String type, int c )
	{
		return String.format("%s_%d.%s", animationBaseIcon, c + 1, type);
	}

	public void stop()
	{
		worker.cancel( true );
	}

	public int getIndex()
	{
		return index;
	}

	public boolean isStopped()
	{
		return worker == null || worker.getState() == SwingWorker.StateValue.DONE;
	}

	public void start()
	{
		if( !enabled )
			return;

		/*
		 * mock service to be run needs to be stopped first.
		 * 
		 * if service is restart action occurs while it is running, than run()
		 * needs to finish first so service can be started again. If that is 
		 * case than force stopping mock service.
		 * 
		 */
		if( isStopped() )
		{
			worker = new SwingWorker<Void, ImageIcon>()
			{
				@Override
				protected Void doInBackground() throws Exception
				{
					if( System.getProperty( "soapui.enablenamedthreads" ) != null )
						Thread.currentThread().setName( "ModelItemIconAnimator for " + target.getName() );

					while( !stopped )
					{
						try
						{
							index = index >= animateIcons.length - 1 ? 0 : index + 1;

							publish( getIcon() );
							Thread.sleep( 500 );
						}
						catch( InterruptedException e )
						{
							stopped = true;
						}
					}

					return null;
				}

				@Override
				protected void process( List<ImageIcon> chunks )
				{
					ImageIcon imageIcon = chunks.get( chunks.size() - 1 );
					target.setIcon( imageIcon );
				}

				@Override
				protected void done()
				{
					target.setIcon( getIcon() );
				}

			};
			stopped = false;
			worker.execute();
		}
	}

	public ImageIcon getBaseIcon()
	{
		return baseIcon;
	}

	public ImageIcon getIcon()
	{
		if( !isStopped() )
		{
			return animateIcons[getIndex()];
		}

		return baseIcon;
	}

	public T getTarget()
	{
		return target;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled( boolean enabled )
	{
		this.enabled = enabled;
		if( !stopped )
			stopped = enabled;
	}
}
