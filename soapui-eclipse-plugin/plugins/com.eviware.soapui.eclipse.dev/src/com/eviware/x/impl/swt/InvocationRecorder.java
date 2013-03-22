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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import com.eviware.soapui.SoapUI;

/**
 * 
 * @author lars
 */
public class InvocationRecorder<T> implements InvocationHandler
{
   private T mock;
   private T target;
   private ArrayList<Invocation> recording = new ArrayList<Invocation>();
   
   @SuppressWarnings("unchecked")
   public InvocationRecorder(Class<T>... clazz)
   {
      mock = (T) Proxy.newProxyInstance(clazz[0].getClassLoader(), clazz, this);
   }
   
   public T getMock()
   {
      return mock;
   }
   
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
   {
      if(target != null)
      {
         return method.invoke(target, args);
      }
      else
      {
         Invocation invocation = new Invocation(method, args);
         recording.add(invocation);
         return invocation.getReturnedValue();
      }
   }
   
   public void playback(T target)
   {
      this.target = target;
      try
      {
         for(Invocation invocation : recording)
         {
            invocation.invokeOn(target);
         }
      }
      catch(Exception e)
      {
      	SoapUI.logError( e );
         //throw new RuntimeException(e);
      }
   }
}
