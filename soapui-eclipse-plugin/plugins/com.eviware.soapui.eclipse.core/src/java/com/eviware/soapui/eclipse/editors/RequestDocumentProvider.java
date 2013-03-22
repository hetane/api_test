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

import java.io.IOException;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.StorageDocumentProvider;

import com.eviware.soapui.eclipse.SoapUIActivator;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.model.project.Project;

/**
 * Provides a document with a SOAP request.
 * @author Lars H
 */
public class RequestDocumentProvider extends StorageDocumentProvider {

   /**
    * Create a document.
    * @param element The editor input, i.e. 
    */
   @Override
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new XMLPartitionScanner(),
					new String[] {
						XMLPartitionScanner.XML_TAG,
						XMLPartitionScanner.XML_COMMENT });
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
   
   @Override
   protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException
   {
      // Update the request.
      RequestEditorInput editorInput = (RequestEditorInput) element;
      WsdlRequest request = editorInput.getRequest();
      request.setRequestContent(document.get());
      
      // Save the whole SoapUI project.
      Project project = request.getOperation().getInterface().getProject();
      WsdlProject projectImpl = (WsdlProject) project;
      try
      {
         projectImpl.save();
      }
      catch(IOException e)
      {
         throw new CoreException(new Status(IStatus.ERROR, SoapUIActivator.PLUGIN_ID, 1,
               "Could not save project: " + project.getName(), e));
      }
   }
}
