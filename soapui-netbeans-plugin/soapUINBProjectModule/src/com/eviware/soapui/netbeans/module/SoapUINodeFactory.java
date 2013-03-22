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

import com.eviware.soapui.support.SoapUIException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Milan Kuchtiak
 */
public class SoapUINodeFactory implements NodeFactory, PropertyChangeListener {

    private static Map<Project,WsNodeList> factories = new HashMap<Project,WsNodeList>();
    
    public static void refreshNodeList( Project project )
    {
        if( factories.containsKey(project))
            factories.get(project).fireChange();
    }
            
    
    /** Creates a new instance of SoapUINodeFactory */
    public SoapUINodeFactory() {
       OpenProjects.getDefault().addPropertyChangeListener(this);
    }

    public NodeList createNodes(Project project) {
        assert project != null;
        WsNodeList nodeList =  factories.get(project );
        if( nodeList == null ) 
        {
            nodeList = new WsNodeList(project);
            factories.put(project, nodeList);
//            System.out.println( "Creating new NodeList for project " + ProjectUtils.getInformation(project).getName());
        }
        else
        {
//           System.out.println( "Returning existing NodeList for project " + ProjectUtils.getInformation(project).getName());
        }
        return nodeList;
    }

    public static class WsNodeList implements NodeList<String> {

        //public static final String SOAP_UI_PROJECT_FILE_NAME = "soapui-project.xml";
        private static final String KEY_WEB_SERVICES_TESTS = "soapUI"; // NOI18N
        private Project project;
        private FileObject soapUIMetadata = null;
        private com.eviware.soapui.model.project.Project soapUIProject = null;
        private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
        private boolean active;

        public WsNodeList(Project proj) {
            project = proj;
        }

        public List keys() {
            List<String> result = new ArrayList<String>();
            
            if( active && soapUIMetadata == null )
               soapUIMetadata = project.getProjectDirectory().getFileObject(NetBeansSoapUIProjectFactory.SOAP_UI_PROJECT_FILE_NAME);
            
            if (active && soapUIMetadata != null && soapUIMetadata.isValid()) {
                try {
                    soapUIProject = NetBeansPluginUISupport.getDefault().getSoapUIProjectByFile(
                            FileUtil.toFile( soapUIMetadata ).getAbsolutePath() );
                    
                    if(soapUIProject!=null) 
                    {
                        result.add(KEY_WEB_SERVICES_TESTS);
                    }
                } catch (SoapUIException e) {
                    e.printStackTrace();
                }
            }
            
            return result;
        }

        public synchronized void addChangeListener(ChangeListener l) {
            listeners.add(l);
        }

        public synchronized void removeChangeListener(ChangeListener l) {
            listeners.remove(l);
        }

        private void fireChange() {
           if( active )
           {
               ArrayList<ChangeListener> list = new ArrayList<ChangeListener>();
               synchronized (this) {
                   list.addAll(listeners);
               }
               Iterator<ChangeListener> it = list.iterator();
               while (it.hasNext()) {
                   ChangeListener elem = it.next();
                   elem.stateChanged(new ChangeEvent(this));
               }
           }
        }

        public Node node(String key) {
            if (active && KEY_WEB_SERVICES_TESTS.equals(key)) {
                
                if( soapUIProject != null )
                {
                   return new SoapUITreeNodeWrapper(NetBeansPluginUISupport.getDefault().getSoapUIProjectNode(soapUIProject)) {
                  @Override
                    public String getDisplayName() {
                        return "Web Services Tests";
                    }
                };
                }
            }
            return null;
        }

        public void addNotify() {
           active = true;
        }

        public void removeNotify() {
           active = false;
        }
    }

   public void propertyChange(PropertyChangeEvent evt) {
      
      Project [] oldProjects = (Project[]) evt.getOldValue();
      if( oldProjects == null )
         return;
      
      for( Project project : oldProjects )
      {
         if( project instanceof NetBeansSoapUIProject )
            continue;
         
         WsNodeList nodeList = factories.get(project);
         if( nodeList != null && nodeList.soapUIProject != null )
         {
            try {
               factories.remove(project);
               if( !Installer.isClosing())
                  NetBeansPluginUISupport.getDefault().closeProject( nodeList.soapUIProject );
            } catch (IOException ex) {
               Exceptions.printStackTrace(ex);
            }
         }
      }
   }
}
