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
package com.eviware.soapui.netbeans.module.templates;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.actions.iface.GenerateMockServiceAction;
import com.eviware.soapui.impl.wsdl.actions.iface.GenerateWsdlTestSuiteAction;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlImporter;
import com.eviware.soapui.netbeans.module.Installer;
import com.eviware.soapui.support.SoapUIException;
import com.eviware.soapui.support.UISupport;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.apache.xmlbeans.XmlException;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public class SoapUIProjectWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wiz;

    public SoapUIProjectWizardIterator() {
    }

    public static SoapUIProjectWizardIterator createIterator() {
        return new SoapUIProjectWizardIterator();
    }

    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[]{new SoapUIProjectWizardPanel()};
    }

    private String[] createSteps() {
        return new String[]{NbBundle.getMessage(SoapUIProjectWizardIterator.class, "LBL_CreateProjectStep")};
    }

    public Set instantiate() throws IOException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(SoapUI.class.getClassLoader());

        Set resultSet = new LinkedHashSet();
        try {
            File dirF = FileUtil.normalizeFile((File) wiz.getProperty("projdir"));
            dirF.mkdirs();

            FileObject dir = FileUtil.toFileObject(dirF);
            File projectFile = new File(dirF, "soapui-project.xml");

            WsdlProject project = null;

            String importProject = "";
            if (wiz.getProperty("importProject") != null) {
                importProject = wiz.getProperty("importProject").toString().trim();
            }


            if (importProject.equals("")) {
                SoapUI.log("Creating initial project to " + projectFile.getAbsolutePath());
                project = (WsdlProject) Installer.getWorkspace().createProject(dirF.getName(), projectFile);

            } else {
                SoapUI.log("Importing project from " + wiz.getProperty("importProject"));
                File importProjectFile = FileUtil.normalizeFile((File) wiz.getProperty("importProject"));
                try {
                    project = new WsdlProject(importProjectFile.getAbsolutePath());
                } catch (XmlException e) {
                    ErrorManager.getDefault().notify(e);
                    return null;
                }
                project.saveAs(projectFile.getAbsolutePath());
                project.setName(dirF.getName());
            }
            Installer.getWorkspace().save(true);

            String wsdl = (wiz.getProperty("wsdl") == null) ? null : wiz.getProperty("wsdl").toString();
            if (!((wsdl == null) || (wsdl.equals("")))) {
                String wsdlUrl = null;
                if (wsdl.startsWith("http")) {
                    wsdlUrl = wsdl;
                } else {
                    wsdlUrl = new File(wsdl).toURI().toURL().toString();
                }

                WsdlInterface[] results = WsdlImporter.importWsdl(project, wsdlUrl);
                if (results != null && results.length > 0) {
                    UISupport.showInfoMessage(
                            NbBundle.getMessage(SoapUIProjectWizardIterator.class, "ImportWsdlInfoLabel", results.length),
                            NbBundle.getMessage(SoapUIProjectWizardIterator.class, "ImportWsdlInfoTitle"));

                    Boolean createTestSuite = (wiz.getProperty("generateTestSuite") == null) ? false : (Boolean) wiz.getProperty("generateTestSuite");
                    if (createTestSuite) {
                        GenerateWsdlTestSuiteAction generateTestSuiteAction = new GenerateWsdlTestSuiteAction();
                        generateTestSuiteAction.generateTestSuite(results[0], true);
                    }

                    Boolean createMockService = (wiz.getProperty("generateMockService") == null) ? false : (Boolean) wiz.getProperty("generateMockService");
                    if (createMockService) {
                        GenerateMockServiceAction generateMockAction = new GenerateMockServiceAction();
                        generateMockAction.generateMockService(results[0], true);
                    }
                } else {
                    UISupport.showErrorMessage(NbBundle.getMessage(SoapUIProjectWizardIterator.class, "FailedToImportInterfacesError"));
                }


            }

            project.save();

            // Always open top dir as a project:
            resultSet.add(dir);

            File parent = dirF.getParentFile();
            if (parent != null && parent.exists()) {
                ProjectChooser.setProjectsFolder(parent);
            }

        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
        return resultSet;

    }

    public void initialize(WizardDescriptor wiz) {

        this.wiz = wiz;
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) {
                // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps);
            }
        }
    }

    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty("projdir", null);
        this.wiz.putProperty("name", null);
        this.wiz.putProperty("wsdl", null);
        this.wiz.putProperty("generateMockService", null);
        this.wiz.putProperty("generateTestSuite", null);
        this.wiz.putProperty("importProject", null);
        this.wiz = null;
        panels = null;
    }

    public String name() {
        return MessageFormat.format("{0} of {1}", new Object[]{new Integer(index + 1), new Integer(panels.length)});
    }

    public boolean hasNext() {
        return index < panels.length - 1;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {
    }

    public final void removeChangeListener(ChangeListener l) {
    }
}
