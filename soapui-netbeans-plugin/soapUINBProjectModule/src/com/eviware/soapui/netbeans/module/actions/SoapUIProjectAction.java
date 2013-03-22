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

import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.actions.iface.GenerateMockServiceAction;
import com.eviware.soapui.impl.wsdl.actions.iface.GenerateWsdlTestSuiteAction;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlImporter;
import com.eviware.soapui.model.support.ModelSupport;
import com.eviware.soapui.netbeans.module.Installer;
import com.eviware.soapui.netbeans.module.NetBeansSoapUIProject;
import com.eviware.soapui.support.MessageSupport;
import com.eviware.soapui.support.UISupport;
import com.eviware.x.form.ValidationMessage;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.XFormField;
import com.eviware.x.form.XFormFieldListener;
import com.eviware.x.form.XFormFieldValidator;
import com.eviware.x.form.support.ADialogBuilder;
import com.eviware.x.form.support.AField;
import com.eviware.x.form.support.AField.AFieldType;
import com.eviware.x.form.support.AForm;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.WindowManager;

public final class SoapUIProjectAction extends CookieAction {
    private static XFormDialog dialog;

    private DataObject currentDataObject;
    private File currentWsdlFile;
    private Project currentProject;
    
    public static final String CREATE_NEW_OPTION = "<Create New>";

    protected void performAction(Node[] activatedNodes) {
        addWsdlToSoapUIProject( currentWsdlFile, currentProject );
    }
    
    public static void addWsdlToSoapUIProject( File wsdlFile, Project wsdlProject  )
    {
        try {
            // create soapui-project from wsdl file
            WsdlProject project = null;
            if (UISupport.getMainFrame() == null) {
                UISupport.setMainFrame(WindowManager.getDefault().getMainWindow());
            }
            
            if( dialog == null )
            {
               dialog = ADialogBuilder.buildDialog(CreateProjectForm.class);
               dialog.getFormField(CreateProjectForm.PROJECT_PATH).addFormFieldValidator(new XFormFieldValidator() {

                    public ValidationMessage[] validateField(XFormField arg0) {
                        if( arg0.isEnabled() && new File( arg0.getValue() ).exists())
                            return new ValidationMessage[] 
                            { new ValidationMessage( 
                                      NbBundle.getMessage(SoapUIProjectAction.class, "ProjectPathAlreadyExistsError", arg0.getValue() ), arg0)};
                        
                        return null;
                    }
                });
                
                dialog.getFormField(CreateProjectForm.ADD_TO_PROJECT).addFormFieldListener(new XFormFieldListener() {

                    public void valueChanged(XFormField arg0, String arg1, String arg2) {
                        dialog.getFormField(CreateProjectForm.PROJECT_PATH).setEnabled(arg1.equals(CREATE_NEW_OPTION));
                    }
                });
            }
            
            List<com.eviware.soapui.model.project.Project> projects = Installer.getOpenSoapUIProjects();
            dialog.setOptions(CreateProjectForm.ADD_TO_PROJECT, ModelSupport.getNames(new String[]{CREATE_NEW_OPTION}, projects ));
            dialog.getFormField(CreateProjectForm.PROJECT_PATH).setEnabled(true);
            
            String path = FileUtil.toFile( wsdlProject.getProjectDirectory() ).getAbsolutePath() + "Test";
            int cnt=2;
            while( new File( path ).exists() )
            {
                path = FileUtil.toFile( wsdlProject.getProjectDirectory() ).getAbsolutePath() + "Test" + cnt;
                cnt++;
            }
            
            dialog.setValue(CreateProjectForm.PROJECT_PATH, path);
            if( dialog.show())
            {
                // get project to add to if selected
                String projectName = dialog.getValue( CreateProjectForm.ADD_TO_PROJECT );
                if( !projectName.equals(CREATE_NEW_OPTION))
                {
                    for( int c = 0; c < projects.size() && project == null ; c++ )
                    {
                        if( projects.get( c ).getName().equals(projectName))
                            project = (WsdlProject) projects.get( c );
                    }
                }
                
                // create new project
                if( project == null )
                {
                    File soapUIProjectDir = new File( dialog.getValue( CreateProjectForm.PROJECT_PATH));
                    soapUIProjectDir.mkdirs();
                    
                    File projectFile = new File(soapUIProjectDir, "soapui-project.xml");
                    project = new WsdlProject( projectFile.getAbsolutePath() );
                    project.setName(soapUIProjectDir.getName());
                    project.save();
                    ProjectChooser.setProjectsFolder(soapUIProjectDir);
                    Project soapUINBProject = ProjectManager.getDefault().findProject(FileUtil.toFileObject(soapUIProjectDir));
                    OpenProjects.getDefault().open(new Project[]{soapUINBProject}, false);
                    project = (WsdlProject) ((NetBeansSoapUIProject)soapUINBProject).getSoapUIProject();
                }
                
                // import into project
                if( project != null )
                {
                    WsdlInterface[] results = WsdlImporter.importWsdl(project, wsdlFile.toURI().toURL().toString());

                    if (results != null && results.length > 0) 
                    {
                        UISupport.showInfoMessage(
                                NbBundle.getMessage(SoapUIProjectAction.class, "ImportSuccessLabel", results.length ),
                                NbBundle.getMessage(SoapUIProjectAction.class, "ImportSuccessTitle" ));
                        
                        boolean createTestSuite = dialog.getBooleanValue(CreateProjectForm.GENERATE_TESTSUITE);
                        if (createTestSuite) {
                            GenerateWsdlTestSuiteAction generateTestSuiteAction = new GenerateWsdlTestSuiteAction();
                            generateTestSuiteAction.generateTestSuite(results[0],true);
                        }

                        boolean createMockService = dialog.getBooleanValue(CreateProjectForm.GENERATE_MOCKSERVICE);
                        if (createMockService) {
                            GenerateMockServiceAction generateMockAction = new GenerateMockServiceAction();
                            generateMockAction.generateMockService(results[0],true);
                        }
                    } 
                    else 
                    {
                        UISupport.showErrorMessage(NbBundle.getMessage(SoapUIProjectAction.class, "MissingBindingsError" ));
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            UISupport.showErrorMessage(ex);
            ErrorManager.getDefault().notify(ex);
        }

    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return Installer.getOpenSoapUIProjects().isEmpty() ? 
                NbBundle.getMessage(SoapUIProjectAction.class, "CTL_CreateSoapUIProjectAction") :
                NbBundle.getMessage(SoapUIProjectAction.class, "CTL_AddToSoapUIProjectAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{DataObject.class};
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
        currentDataObject = null;
        currentWsdlFile = null;
        currentProject = null;
        if (node.length > 0) {
            currentDataObject = (DataObject) node[0].getCookie(DataObject.class);

            if (currentDataObject != null) {
                FileObject fileObject = currentDataObject.getPrimaryFile();
                if (fileObject.getExt().equalsIgnoreCase("wsdl")) {
                    currentWsdlFile = FileUtil.toFile(fileObject);
                }

                if (!fileObject.isFolder()) {
                    fileObject = fileObject.getParent();
                }

                while (fileObject != null && currentProject == null) {
                    try {
                        currentProject = ProjectManager.getDefault().findProject(fileObject);
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (currentProject == null) {
                        fileObject = fileObject.getParent();
                    }
                }
            }
            if (currentProject == null && node[0].getLookup().lookup(Project.class) != null) {
                currentProject = (Project) node[0].getLookup().lookup(Project.class);
            }
        }

        return super.enable(node);
    }

    
    
    private FileObject getTemplateFile(String name) {
        FileObject fo = getFolder() != null ? getFolder().getFileObject(name) : null;
        return fo;
    }

    private FileObject getFolder() {
        FileObject folder = null;
        if (folder == null) {
            folder = Repository.getDefault().getDefaultFileSystem().findResource("Templates/Project/Enterprise");
        }

        return folder;
    }

    public static final MessageSupport messages = MessageSupport.getMessages( SoapUIProjectAction.class );
    
    @AForm( name="Form.Title", description="Form.Description", helpUrl="http://www.soapui.org/IDE-Plugins/netbean.html")
    public interface CreateProjectForm
    {
        @AField( description="Form.AddToProject.Description", type=AFieldType.ENUMERATION )
        public final static String ADD_TO_PROJECT = messages.get("Form.AddToProject.Label");
        
        @AField( description="Form.ProjectPath.Description", type=AFieldType.FOLDER )
        public final static String PROJECT_PATH = messages.get("Form.ProjectPath.Label");
        
        @AField( description="Form.GenerateTestSuite.Description", type=AFieldType.BOOLEAN )
        public final static String GENERATE_TESTSUITE = messages.get("Form.GenerateTestSuite.Label");
        
        @AField( description="Form.GenerateMockService.Description", type=AFieldType.BOOLEAN )
        public final static String GENERATE_MOCKSERVICE = messages.get("Form.GenerateMockService.Label");
    }
}

