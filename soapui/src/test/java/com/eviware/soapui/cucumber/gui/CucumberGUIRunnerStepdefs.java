package com.eviware.soapui.cucumber.gui;

import cucumber.annotation.en.And;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;

import static org.junit.Assert.assertTrue;

public class CucumberGUIRunnerStepdefs
{

	FESTRunner runner = new FESTRunner();
	DialogFixture dialogFixture;

	@Given( "Calvin is creating a new REST project$" )
	public void userOpensRestCreationDialog()
	{
		runner.startSoapUI();
		runner.getFrame().menuItemWithPath( "File", "New REST Project" ).click();
	}

	@And( "^inputs a (.+)$" )
	public void userInputsURI( String uri )
	{
		FESTRunner.DialogTitleMatcher matcher = new FESTRunner.DialogTitleMatcher( "New REST Project" );

		dialogFixture = WindowFinder.findDialog( matcher ).using( runner.getRobot() );
		dialogFixture.textBox().setText( uri );
	}

	@When( "^he clicks the OK button$" )
	public void userClicksButton()
	{
		FESTRunner.ButtonTextMatcher matcher = new FESTRunner.ButtonTextMatcher( "OK" );
		dialogFixture.button( matcher ).click();
	}

	@Then( "^a project named REST Project is created in the navigation tree$" )
	public void restProjectCreated()
	{
		assertTrue( runner.projectCreatedWithName( "REST Project" ) );
	}
}