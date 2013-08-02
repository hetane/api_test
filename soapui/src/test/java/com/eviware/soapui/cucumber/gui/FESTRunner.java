package com.eviware.soapui.cucumber.gui;

import com.eviware.soapui.SoapUI;
import org.fest.swing.core.*;
import org.fest.swing.core.Robot;
import org.fest.swing.exception.WaitTimedOutError;
import org.fest.swing.finder.DialogFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.*;
import org.junit.*;

import javax.swing.*;
import java.awt.*;

import static junit.framework.Assert.assertNotNull;
import static org.fest.swing.finder.WindowFinder.findDialog;
import static org.fest.swing.finder.WindowFinder.findFrame;
import static org.fest.swing.launcher.ApplicationLauncher.application;

public class FESTRunner
{

	private Robot robot;
	private FrameFixture frame;

	private static final String WORKSPACE_NAME = "Default workspace";

	public void startSoapUI()
	{
		application( SoapUI.class ).start();
		robot = BasicRobot.robotWithCurrentAwtHierarchy();
		frame = findFrame( new FrameTitleMatcher( "soapUI" ) ).using( getRobot() );
	}

	public void tearDown()
	{
		frame.cleanUp();
	}

	public boolean projectCreatedWithName( String name )
	{
		JTreeFixture projectTreeFixture = frame.panel( "Navigator" ).tree( "Navigator tree" ).selectPath( WORKSPACE_NAME + "/" + name );

		if( projectTreeFixture == null )
			return false;
		else
			return true;
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

	protected static class ButtonTextMatcher extends GenericTypeMatcher<JButton>
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

	protected static class DialogTitleMatcher extends GenericTypeMatcher<Dialog>
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

	protected static class FrameTitleMatcher extends GenericTypeMatcher<Frame>
	{
		String frameTitle;

		public FrameTitleMatcher( String title )
		{
			super( Frame.class );
			this.frameTitle = title;
		}

		protected boolean isMatching( Frame frame )
		{
			return frame.getTitle().startsWith( frameTitle ) && frame.isShowing();
		}
	}
}
