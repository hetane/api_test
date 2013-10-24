package com.eviware.soapui.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.awt.*;

@Aspect
public class EDTAspect
{

	@Pointcut("call (* javax.swing..*+.*(..)) || " +
			"call (javax.swing..*+.new(..))")
	public void swingMethods() {}

	@Pointcut("call (* javax.swing..*+.add*Listener(..)) || " +
			"call (* javax.swing..*+.remove*Listener(..)) || " +
			"call (void javax.swing.JComponent+.setText(java.lang.String))")
	public void safeMethods() {}

	@Before("swingMethods() && !safeMethods()")
	public void checkCallingThread(JoinPoint.StaticPart thisJoinPointStatic) {
		if(!EventQueue.isDispatchThread()) {
			System.err.println(
					"Swing single thread rule violation: "
							+ thisJoinPointStatic);
			Thread.dumpStack();
		}
	}

}
