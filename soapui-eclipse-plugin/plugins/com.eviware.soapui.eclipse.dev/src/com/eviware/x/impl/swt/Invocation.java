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
import java.lang.reflect.Method;

import com.eviware.x.form.XFormField;
import com.eviware.x.form.XFormOptionsField;
import com.eviware.x.form.XFormTextField;

/**
 * 
 * @author lars
 */
class Invocation
{
   private Method method;
   private Object[] args;
   private InvocationRecorder returnedObjectHandler;
   
   public Invocation(Method method, Object[] args)
   {
      this.method = method;
      this.args = args;
   }
   
   public void invokeOn(Object target)
   throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
   {
      Object returnedValue = method.invoke(target, args);
      
      if(returnedObjectHandler != null && returnedValue != null)
         returnedObjectHandler.playback(returnedValue);
   }

   /**
    * Return a mock or null if the return type is not an interface.
    * @return A mock or null. The caller should not use returned values that are not interfaces.
    */
   public Object getReturnedValue()
   {
      Class<?> returnType = method.getReturnType();
      if(returnType.isInterface())
      {
         // Create a new recorder.
         Class<?>[] implementedInterfaces = getInterfaceHierarchy(returnType);
         returnedObjectHandler = new InvocationRecorder(implementedInterfaces);
         return returnedObjectHandler.getMock();
      }
      else
      {
         return null;
      }
   }
   
   /**
    * Special handling to mock sub-interfaces.
    * @param clazz
    * @return
    */
   private Class[] getInterfaceHierarchy(Class clazz)
   {
      if(clazz == XFormField.class)
         return new Class[] { XFormField.class, XFormOptionsField.class, XFormTextField.class };
      else
         return new Class[] { clazz };
   }
}
