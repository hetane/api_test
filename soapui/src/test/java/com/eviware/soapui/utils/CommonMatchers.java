package com.eviware.soapui.utils;

import com.eviware.soapui.impl.wsdl.WsdlTestSuite;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.project.Project;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

/**
 * Useful Hamcrest matchers for common object types.
 */
public class CommonMatchers
{


	public static Matcher<String> endsWith( final String suffix )
	{
		return new TypeSafeMatcher<String>()
		{
			@Override
			public boolean matchesSafely( String stringToMatch )
			{
				return stringToMatch.endsWith( suffix );
			}

			@Override
			public void describeTo( Description description )
			{
				description.appendText( "a String that ends with " + suffix );
			}
		};
	}

	public static Matcher<String> startsWith( final String prefix )
	{
		return new TypeSafeMatcher<String>()
		{
			@Override
			public boolean matchesSafely( String stringToMatch )
			{
				return stringToMatch.startsWith( prefix );
			}

			@Override
			public void describeTo( Description description )
			{
				description.appendText( "a String that starts with " + prefix );
			}
		};
	}
}
