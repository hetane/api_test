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

package com.eviware.soapui.eclipse.codegen;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;

/**
 * 
 * @author Lars H
 */
public class CodeGenerator
{
   // Save even if it is not in sync with the workspace file.
   private static boolean FORCE = true;
   
   private ICompilationUnit javaFile;
   private Document doc;
   private AST ast;
   private CompilationUnit compilationUnit;
   private TypeDeclaration type;
   
   /**
    * Analyze a compilation unit.
    * @param cu
    * @throws JavaModelException
    */
   public CodeGenerator(ICompilationUnit javaFile) throws JavaModelException
   {
      IProgressMonitor monitor = null;
      this.javaFile = javaFile;
      this.doc = new Document(javaFile.getSource());
      ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setSource(doc.get().toCharArray());
      this.compilationUnit = (CompilationUnit) parser.createAST(monitor);
      this.compilationUnit.recordModifications();
      this.ast = compilationUnit.getAST();
      List types = compilationUnit.types();
      if(types.size() == 0)
         throw new IllegalStateException("There are no types in this Java file");
      this.type = (TypeDeclaration) types.get(0);
   }
   
   public void save(IProgressMonitor monitor) throws MalformedTreeException, BadLocationException, JavaModelException
   {
      TextEdit edits = compilationUnit.rewrite(doc, null);
      
      @SuppressWarnings("unused")
      UndoEdit undo = edits.apply(doc);
      
      javaFile.getBuffer().setContents(doc.get());
      javaFile.save(monitor, FORCE);
   }
   
   public TypeDeclaration getType()
   {
      return type;
   }
   
   @SuppressWarnings("unchecked")
   public NormalAnnotation addAnnotation(BodyDeclaration typeOrMember, String name)
   {
   	List modifiers = typeOrMember.modifiers();
   	Iterator i = modifiers.iterator();
   	
   	while( i.hasNext() )
   	{
   		Object obj = i.next();
   		if( obj instanceof NormalAnnotation )
   		{
   			NormalAnnotation ann = (NormalAnnotation) obj;
   			if( compareName(name, ann.getTypeName()) )
   				return ann;
   		}
   	}
   	
      NormalAnnotation annotation = ast.newNormalAnnotation();
      annotation.setTypeName( ast.newName( name ) );
      
		modifiers.add(0,annotation);
      return annotation;
   }
   
   public void addAnnotationParamString(NormalAnnotation annotation, String name, String value, String defaultValue )
   {
   	if( value == null || value.length() == 0 )
   	{
   		removeAnnotationParam( annotation, name );
   		return;
   	}
   		
      addAnnotationParam(annotation, name, string2Expression(value), defaultValue == null ? null : string2Expression( defaultValue ));
   }
   
   public void removeAnnotationParam(NormalAnnotation annotation, String name)
	{
   	for( Object mvp : annotation.values() )
		{
			if( compareName( name, ((MemberValuePair)mvp).getName()) )
			{
				((MemberValuePair)mvp).delete();
				return;
			}
		}
	}

	@SuppressWarnings("unchecked")
   public void addAnnotationParam(NormalAnnotation annotation, String name, Expression value, Expression defaultValue )
   {
		if( value.equals( defaultValue ))
		{
			removeAnnotationParam( annotation, name );
			return;
		}
		
   	for( Object mvp : annotation.values() )
		{
			if( compareName( name, ((MemberValuePair)mvp).getName()) )
			{
				((MemberValuePair)mvp).setValue( value );
				return;
			}
		}
   	
      MemberValuePair mvp = ast.newMemberValuePair();
      mvp.setName(ast.newSimpleName(name));
      mvp.setValue(value);
      annotation.values().add(mvp);
   }
   
   public Name buildName(String name)
   {
      if(name.indexOf('.') < 0)
         return ast.newSimpleName(name);
      String[] elements = nameElements(name);
      return ast.newName(elements);
   }
   
   private static String[] nameElements(String name)
   {
      return name.split("\\.\\$");
   }

   public Expression string2Expression(String string)
   {
      StringLiteral expression = ast.newStringLiteral();
      expression.setLiteralValue(string);
      return expression;
   }
   
   public AST getAst()
   {
   	return ast;
   }

	public String getAnnotationParamString(TypeDeclaration type, String typeName, String name)
	{
		List modifiers = type.modifiers();
   	Iterator i = modifiers.iterator();
   	
   	while( i.hasNext() )
   	{
   		Object obj = i.next();
   		if( obj instanceof NormalAnnotation )
   		{
   			NormalAnnotation ann = (NormalAnnotation) obj;
   			if( compareName(typeName, ann.getTypeName()) )
   			{
   				for( Object mvp : ann.values() )
   				{
   					if( compareName( name, ((MemberValuePair)mvp).getName()) )
   					{
   						Expression value = ((MemberValuePair)mvp).getValue();
							if( value == null ) return null; 
							
							if( value instanceof StringLiteral)
								return ((StringLiteral)value).getLiteralValue();
							else
								return value.toString();
   					}
   				}
   				
   				return null;
   			}
   		}
   	}
   	
   	return null;
	}

	private boolean compareName(String typeName, Name name)
	{
		if( name.isSimpleName() )
		{
			int ix = typeName.lastIndexOf( "." );
			if( ix > 0 )
				typeName = typeName.substring( ix+1 );
		}

		return name.getFullyQualifiedName().equals( typeName );
	}
}
