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
package com.eviware.soapui.netbeans.module.actions;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.xmlbeans.XmlException;

import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.actions.iface.GenerateWsdlTestSuiteAction;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlImporter;
import com.eviware.soapui.netbeans.module.NetBeansPluginUISupport;
import com.eviware.soapui.netbeans.module.NetBeansSoapUIProjectFactory;
import com.eviware.soapui.netbeans.module.SoapUINodeFactory;
import com.eviware.soapui.support.SoapUIException;


public final class SoapUICreateTestsAction extends CookieAction {

    //private DataObject currentDataObject;
    private URL currentWsdlUrl;
    private Project currentProject;

    protected void performAction(Node[] activatedNodes) {
        if(currentProject!=null && currentWsdlUrl!=null) {
            try {
                addWsdlToSoapUIProject(currentProject, currentWsdlUrl);
            } catch (Exception ex) {
            }
        }
    }

    public static void addWsdlToSoapUIProject(Project project, URL currentWsdlUrl) throws Exception {
        com.eviware.soapui.model.project.Project soapUIProject = null;
        FileObject soapUIProjectConfig = project.getProjectDirectory().
                getFileObject(NetBeansSoapUIProjectFactory.SOAP_UI_PROJECT_FILE_NAME);
        if(soapUIProjectConfig==null || (soapUIProject=NetBeansPluginUISupport.
                getDefault().getSoapUIProjectByFile(FileUtil.toFile
                (soapUIProjectConfig).getAbsolutePath()))==null) {
            // create new project
            File projectFile = new File(FileUtil.toFile(project.getProjectDirectory()), NetBeansSoapUIProjectFactory.SOAP_UI_PROJECT_FILE_NAME);
            soapUIProject = new WsdlProject( projectFile.getAbsolutePath() );
            ((WsdlProject)soapUIProject).setName(ProjectUtils.getInformation(project).getDisplayName()+"WebServicesTests");
            soapUIProject.save();
        }
        if(soapUIProject instanceof WsdlProject) {
            final WsdlInterface[] results = WsdlImporter.importWsdl((WsdlProject) soapUIProject, currentWsdlUrl.toString());
            if( results.length > 0)
            {
                GenerateWsdlTestSuiteAction generateTestSuiteAction = new GenerateWsdlTestSuiteAction();
                generateTestSuiteAction.generateTestSuite(results[0],true);
                 soapUIProject.save();
            }
           
        }
        
        SoapUINodeFactory.refreshNodeList(project);
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(SoapUICreateTestsAction.class, "CTL_CreateSoapUITests");
    }

    protected Class[] cookieClasses() {
        return new Class[]{FileObject.class};
    }

    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] node) {
        boolean flag = false;
        if (node.length > 0) {
            Object wsdlUrlKey = node[0].getValue("wsdl-url");
            if (wsdlUrlKey instanceof String) {
                try {
                    currentWsdlUrl = new URL((String) wsdlUrlKey);
                    flag = true;
                } catch (Exception e) {
                }
                if (flag) {
                    FileObject fileObject = node[0].getLookup().lookup(FileObject.class);
                    if (fileObject == null ||
                            (currentProject = FileOwnerQuery.getOwner(fileObject)) == null) {
                        flag = false;
                    }
                }
            }
        }
        return flag && super.enable(node);
    }

}

