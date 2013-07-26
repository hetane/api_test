package com.eviware.soapui.cucumber.gui;

import com.eviware.soapui.SoapUI;
import org.fest.swing.core.*;
import org.fest.swing.core.Robot;
import org.fest.swing.exception.WaitTimedOutError;
import org.fest.swing.finder.DialogFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

import static org.fest.swing.finder.WindowFinder.findDialog;
import static org.fest.swing.finder.WindowFinder.findFrame;
import static org.fest.swing.launcher.ApplicationLauncher.application;

public class FESTRunner
{

	private Robot robot;
	private FrameFixture frame;

	@Before
	public void startSoapUI()
	{
		robot = BasicRobot.robotWithCurrentAwtHierarchy();

		try
		{
			frame = findFrame( new GenericTypeMatcher<Frame>( Frame.class )
			{
				protected boolean isMatching( Frame frame )
				{
					return frame.getTitle().startsWith( "soapUI" ) && frame.isShowing();
				}
			} ).using( robot );
		}
		catch( WaitTimedOutError e )
		{
			robot.cleanUp();
			application( SoapUI.class ).start();
			robot = BasicRobot.robotWithCurrentAwtHierarchy();
			frame = findFrame( new GenericTypeMatcher<Frame>( Frame.class )
			{
				protected boolean isMatching( Frame frame )
				{
					return frame.getTitle().startsWith( "soapUI" ) && frame.isShowing();
				}
			} ).using( robot );
		}
	}

	@After
	public void tearDown()
	{
		frame.cleanUp();
	}

	@Test
	public void createNewRestProjectTest() throws InterruptedException
	{
		JMenuItemFixture menuItemFixture = frame.menuItemWithPath( "File", "New REST Project" );
		menuItemFixture.click();

		DialogFixture dialogFixture = WindowFinder.findDialog( new DialogTitleMatcher( "New REST Project" ) ).using( getRobot() );
		Thread.sleep( 1000 );

		dialogFixture.textBox().click().setText( "http://maps.googleapis.com/maps/api/geocode/xml?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&sensor=false" );
		Thread.sleep( 1000 );

		GenericTypeMatcher<JButton> buttonTextMatcher = new ButtonTextMatcher( "OK" );
		dialogFixture.button( buttonTextMatcher ).click();
		Thread.sleep( 1000 );

		frame.panel( "Navigator" ).tree( "Navigator tree" ).clickPath("REST Project/maps.googleapis.com");
		Thread.sleep( 1000 );
	}


	public FrameFixture getFrame()
	{
		return frame;
	}

	public Robot getRobot()
	{
		return robot;
	}

	public void closeOpenDialogs()
	{
		try
		{
			DialogFinder dialog = findDialog( Dialog.class ).withTimeout( 1000 );
			dialog.using( getRobot() ).close();
		}
		catch( WaitTimedOutError e )
		{
			e.printStackTrace();
		}
	}

	public void shutdown()
	{
		closeOpenDialogs();
		getRobot().cleanUpWithoutDisposingWindows();
	}

	private static class ButtonTextMatcher extends GenericTypeMatcher<JButton>
	{
		String buttonText;

		public ButtonTextMatcher( String buttonText )
		{
			super( JButton.class );
			this.buttonText = buttonText;
		}

		@Override
		protected boolean isMatching( JButton button )
		{
			return buttonText.equals( button.getText() );
		}
	}

	private static class DialogTitleMatcher extends GenericTypeMatcher<Dialog>
	{
		String dialogTitle;

		public DialogTitleMatcher( String dialogTitle )
		{
			super( Dialog.class );
			this.dialogTitle = dialogTitle;
		}

		@Override
		protected boolean isMatching( Dialog dialog )
		{
			return dialogTitle.equals( dialog.getTitle() ) && dialog.isShowing();
		}
	}
}
