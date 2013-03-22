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

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;

public class SwtDialogsTest extends TestCase
{
   public static void main(String[] args)
   {
      Display display = new Display();
      SwtDialogs dialogs = new SwtDialogs();
      
      String answer = dialogs.prompt("question", "title");
      System.out.println("answer=" + answer);

      char[] password = dialogs.promptPassword("question", "title");
      System.out.println("password=" + Arrays.toString(password));
      
      display.dispose();
   }
}
