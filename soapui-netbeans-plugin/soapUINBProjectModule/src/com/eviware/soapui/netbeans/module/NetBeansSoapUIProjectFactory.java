/*
 *  soapUI, copyright (C) 2004-2011 smartbear.com
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the
 *  terms of version 2.1 of the GNU Lesser General Public License as published by
 *  the Free Software Foundation.
 *  For the avoidance of doubt, eviware elects not to use any
 *  later versions of the LGPL License.
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details at gnu.org.
 *
 *
 *  -------------------------------------------------------------------------------------
 *  If soapUI is distributed with products from Sun, note the following:
 *  -------------------------------------------------------------------------------------
 *
 *  For the avoidance of doubt, except that if any license choice other than
 *  GPL or LGPL is available it will apply instead,
 *  Sun elects to use only the General Public License version 2 (GPLv2) at
 *  this time for any software where a choice of GPL
 *  license versions is made available with the language indicating that
 *  GPLv2 or any later version may be used, or where a
 *  choice of which version of the GPL is applied is otherwise unspecified.
 */

package com.eviware.soapui.netbeans.module;

import com.eviware.soapui.model.tree.SoapUITreeNode;
import com.eviware.soapui.support.SoapUIException;
import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Sofia
 */
public class NetBeansSoapUIProjectFactory implements ProjectFactory {

    public static final String SOAP_UI_PROJECT_FILE_NAME = "soapui-project.xml";
    
    public NetBeansSoapUIProjectFactory() {
       
    }
    
    public boolean isProject(FileObject projectDirectory) {
        // there must be an xml file named "soapui-project" in
        // the root directory for this to be a valid soap-ui project
        return projectDirectory.getFileObject(SOAP_UI_PROJECT_FILE_NAME) != null;
    }

    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        try {
            if (!isProject(dir))
                    return null;

            NetBeansPluginUISupport uiSupport = NetBeansPluginUISupport.getDefault();
            com.eviware.soapui.model.project.Project soapUIProject = uiSupport.getSoapUIProjectByFile(
                   FileUtil.toFile( dir.getFileObject(SOAP_UI_PROJECT_FILE_NAME)).getAbsolutePath());
            uiSupport.saveWorkspace(true);
            SoapUITreeNode soapUIProjectNode = uiSupport.getSoapUIProjectNode(soapUIProject);
            return new NetBeansSoapUIProject(dir, soapUIProject,soapUIProjectNode, state);
        } catch (SoapUIException suie) {
            ErrorManager.getDefault().notify(suie);
            suie.printStackTrace();
        }
        catch (Exception xe) {
            ErrorManager.getDefault().notify(xe);
            xe.printStackTrace();
        }

        return null;
    }

    public void saveProject(Project project) throws IOException, ClassCastException {
        com.eviware.soapui.model.project.Project soapUIProject = NetBeansPluginUISupport.getDefault().getSoapUIProjectByName(project.getProjectDirectory().getNameExt());
        soapUIProject.save();
    }
}
