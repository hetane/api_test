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

package com.eviware.soapui.eclipse.actions;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.eviware.soapui.eclipse.codegen.CodeGenerator;
import com.eviware.soapui.eclipse.util.SelectionUtil;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.types.StringToStringMap;
import com.eviware.x.form.XForm;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.XFormDialogBuilder;
import com.eviware.x.form.XFormFactory;

/**
 * Run wstools on a selected Java class.
 * 
 * @author Lars H
 */
public class WsAnnotateAction implements IObjectActionDelegate
{
   private final static Logger log = Logger.getLogger(WsAnnotateAction.class);

	private static final String WS_NAME = "Name";
	private static final String WS_SERVICENAME = "Service Name";
	private static final String WS_NAMESPACE = "Target Namespace";
	private static final String WS_PORTNAME = "PortName";
	private static final String WS_WSDLLOCATION = "WSDL Location";
	private static final String WS_ENDPOINTINTERFACE = "Endpoint Interface";

	private static final String WS_HANDLERFILE = "Handler File";

	private static final String WS_BINDINGSTYLE = "Binding Style";
	private static final String WS_BINDINGUSE = "Binding Uses";
	private static final String WS_BINDINGPARAMETERSTYLE = "Binding Parameter Style";
	
   private IType selectedClass;
   
   public WsAnnotateAction()
   {
   }
   
   public void setActivePart(IAction action, IWorkbenchPart targetPart)
   {
   }

   public void selectionChanged(IAction action, ISelection selection)
   {
      try
      {
         selectedClass = SelectionUtil.getSelectedType(selection);
      }
      catch(JavaModelException e)
      {
         log.error("Could not get selected type", e);
      }
   }
   
   public void run(IAction action)
   {
   	ICompilationUnit cu = selectedClass.getCompilationUnit();

      IProgressMonitor monitor = null;
      
		try
		{
			XFormDialog dialog = buildDialog();
			StringToStringMap values = new StringToStringMap();
			
//		 	Analyze the Java file.
         CodeGenerator codeGen = new CodeGenerator(cu);
         TypeDeclaration type = codeGen.getType();
         
         String name = codeGen.getAnnotationParamString( type, "javax.jws.WebService", "name" );
         String targetNamespace = codeGen.getAnnotationParamString( type, "javax.jws.WebService", "targetNamespace" );
         String serviceName = codeGen.getAnnotationParamString( type, "javax.jws.WebService", "serviceName" );
         String portName = codeGen.getAnnotationParamString( type, "javax.jws.WebService", "portName" );
         String wsdlLocation = codeGen.getAnnotationParamString( type, "javax.jws.WebService", "wsdlLocation" );
         String endpointInterface = codeGen.getAnnotationParamString( type, "javax.jws.WebService", "endpointInterface" );

         String serviceStyle = codeGen.getAnnotationParamString( type, "javax.jws.soap.SOAPBinding", "style" );
         String useStyle = codeGen.getAnnotationParamString( type, "javax.jws.soap.SOAPBinding", "use" ); 
         String parameterStyle = codeGen.getAnnotationParamString( type, "javax.jws.soap.SOAPBinding", "parameterStyle" );
			
         String handlerFile = codeGen.getAnnotationParamString( type, "javax.jws.HandlerChain", "file" );
         
			values.put( WS_NAME, name == null ? selectedClass.getElementName() : name );
			values.put( WS_SERVICENAME, serviceName == null ? selectedClass.getElementName() + "Service" : serviceName );
			values.put( WS_NAMESPACE, targetNamespace == null ? 
					"urn:" + selectedClass.getPackageFragment().getElementName() : targetNamespace );

			values.put( WS_PORTNAME, portName );
			values.put( WS_WSDLLOCATION, wsdlLocation );
			values.put( WS_ENDPOINTINTERFACE, endpointInterface );
			
			values.put( WS_BINDINGSTYLE, serviceStyle == null || serviceStyle.equals( "javax.jws.soap.SOAPBinding.Style.DOCUMENT") ?
				"Document" : "RPC" );
			values.put( WS_BINDINGUSE, useStyle == null || useStyle.equals( "javax.jws.soap.SOAPBinding.Use.LITERAL") ?
					"LITERAL" : "ENCODED" );
			values.put( WS_BINDINGPARAMETERSTYLE, parameterStyle == null || parameterStyle.equals( "javax.jws.soap.SOAPBinding.ParameterStyle.WRAPPED") ?
					"WRAPPED" : "BARE" );
			
			values.put( WS_HANDLERFILE, handlerFile );
			
			values = dialog.show( values );
			if( dialog.getReturnValue() == XFormDialog.CANCEL_OPTION )
				return;
			
         // Add annotations.
			NormalAnnotation annotation = codeGen.addAnnotation(type, "javax.jws.WebService");
         codeGen.addAnnotationParamString(annotation, "name", values.get( WS_NAME ), null );
         codeGen.addAnnotationParamString(annotation, "targetNamespace", values.get( WS_NAMESPACE ), null );
         codeGen.addAnnotationParamString(annotation, "serviceName", values.get( WS_SERVICENAME ), null);
         codeGen.addAnnotationParamString(annotation, "portName", values.get( WS_PORTNAME ), null );
         codeGen.addAnnotationParamString(annotation, "wsdlLocation", values.get( WS_WSDLLOCATION ), null );
         codeGen.addAnnotationParamString(annotation, "endpointInterface", values.get( WS_ENDPOINTINTERFACE ), null );
         
         annotation = codeGen.addAnnotation(type, "javax.jws.soap.SOAPBinding");
         
         if( values.get( WS_BINDINGSTYLE ).equalsIgnoreCase( "Document" ))
         	codeGen.removeAnnotationParam( annotation, "style" );
         else
         	codeGen.addAnnotationParam(annotation, "style", codeGen.getAst().newName( "javax.jws.soap.SOAPBinding.Style.RPC"), null );

         if( values.get( WS_BINDINGUSE ).equals( "LITERAL" ))
         	codeGen.removeAnnotationParam( annotation, "use" );
         else
         	codeGen.addAnnotationParam(annotation, "use", codeGen.getAst().newName( "javax.jws.soap.SOAPBinding.Use.ENCODED"), null );

         if( values.get( WS_BINDINGPARAMETERSTYLE ).equals( "WRAPPED" ))
         	codeGen.removeAnnotationParam( annotation, "parameterStyle" );
         else
         	codeGen.addAnnotationParam(annotation, "parameterStyle", codeGen.getAst().newName( "javax.jws.soap.SOAPBinding.ParameterStyle.BARE"), null );
         
         if( annotation.values().isEmpty() )
         	annotation.delete();
         
         annotation = codeGen.addAnnotation(type, "javax.jws.HandlerChain");
         codeGen.addAnnotationParamString(annotation, "file", values.get( WS_HANDLERFILE ), null );
         
         if( annotation.values().isEmpty() )
         	annotation.delete();
         
         MethodDeclaration[] methods = type.getMethods();
         for( MethodDeclaration method : methods )
         {
         	codeGen.addAnnotation( method, "javax.jws.WebMethod" );
         }
         
         // Update the Java file.
         codeGen.save(monitor);
		}
		catch (Exception e)
		{
         UISupport.showErrorMessage( e );
			e.printStackTrace();
		}
   }
   
   protected XFormDialog buildDialog()
	{
      XFormDialogBuilder builder = XFormFactory.createDialogBuilder("Generate WebService Annotations");

      XForm mainForm = builder.createForm( "Basic" );
      
      mainForm.addTextField( WS_NAME, "The name of the generated webservice", XForm.FieldType.TEXT );
		mainForm.addTextField( WS_SERVICENAME, "The name of the generated Service", XForm.FieldType.TEXT );
		mainForm.addTextField( WS_NAMESPACE, "The name of the generated Service", XForm.FieldType.TEXT );
		mainForm.addTextField( WS_PORTNAME, "The portType name for the generated Service", XForm.FieldType.TEXT );
		mainForm.addTextField( WS_WSDLLOCATION, "The external location of the services wsdl", XForm.FieldType.TEXT );
		mainForm.addTextField( WS_ENDPOINTINTERFACE, "The endpoint interface implemented by this service", XForm.FieldType.TEXT );
		
		mainForm.addSeparator();
		
		mainForm.addComboBox( WS_BINDINGSTYLE, 
				new String [] {"Document", "RPC"},  "The encoding style to use" );
		mainForm.addComboBox( WS_BINDINGUSE, 
				new String [] {"LITERAL", "ENCODED"},  "The formatting style to use" );
		mainForm.addComboBox( WS_BINDINGPARAMETERSTYLE, 
				new String [] {"WRAPPED", "BARE"},  "The parameter style to use" );
      
      mainForm.addSeparator();
      mainForm.addTextField( WS_HANDLERFILE, "The handlerfile", XForm.FieldType.PROJECT_FILE );
		
		return builder.buildDialog( builder.buildOkCancelActions(),
            "Specify values for JAX-WS WebService annotations", UISupport.TOOL_ICON );
	}
   
}
