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

package com.eviware.soapui.eclipse.editors;

import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

import com.eviware.soapui.impl.wsdl.WsdlRequest;

/**
 * Editor input for a SOAP request.
 * @author Lars H
 */
public abstract class MessageEditorInput implements IStorageEditorInput
{
   private WsdlRequest request;
   
   public MessageEditorInput(WsdlRequest request)
   {
      if(request == null)
         throw new IllegalArgumentException("request is null");
      this.request = request;
   }
   
   public boolean equals(Object object)
   {
      if(object == null)
         return false;
      if(object.getClass() != getClass())
         return false;
      MessageEditorInput that = (MessageEditorInput) object;
      return this.request.equals(that.request);
   }

   public WsdlRequest getRequest()
   {
      return request;
   }
   
   public boolean exists()
   {
      return true;
   }

   public String getName()
   {
      return request.getName();
   }
   
   public IPersistableElement getPersistable()
   {
      // TODO Eclipse: Persist editors.
      return null;
   }
}
