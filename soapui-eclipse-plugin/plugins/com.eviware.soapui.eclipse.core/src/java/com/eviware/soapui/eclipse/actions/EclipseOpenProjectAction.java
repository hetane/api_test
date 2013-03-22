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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.IDE;

import com.eviware.soapui.model.project.Project;

/**
 * 
 * @author Dain Nilsson
 */
public class EclipseOpenProjectAction extends AbstractObjectAction<Project>
{
   public EclipseOpenProjectAction()
   {
      super(Project.class);
   }
   
   @Override
	public void run(IWorkbenchPart activePart, Project selectedElement) throws Exception {
	   IPath path = new Path(selectedElement.getPath());
	   IPath root = ResourcesPlugin.getWorkspace().getRoot().getLocation();
	   path = path.removeFirstSegments(root.matchingFirstSegments(path));
	   IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
	   IDE.openEditor(activePart.getSite().getPage(), f, true);
	}
}
