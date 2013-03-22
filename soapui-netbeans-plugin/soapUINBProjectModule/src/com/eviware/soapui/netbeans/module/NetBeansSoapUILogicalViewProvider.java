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
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Sofia
 */
class NetBeansSoapUILogicalViewProvider implements LogicalViewProvider {

    private final SoapUITreeNode soapUIProjectNode;
    private final NetBeansSoapUIProject netBeansSoapUIProject;


    public NetBeansSoapUILogicalViewProvider(SoapUITreeNode soapUIProjectNode, NetBeansSoapUIProject netBeansSoapUIProject) {
        this.soapUIProjectNode = soapUIProjectNode;
        this.netBeansSoapUIProject = netBeansSoapUIProject;
    }

    public Node createLogicalView() {
//        SwingUtilities.invokeLater(new Runnable() {
//
//            public void run() {
//                UISupport.setMainFrame(WindowManager.getDefault().getMainWindow());;
//            }
//        });
        
        return new SoapUITreeNodeWrapper(soapUIProjectNode, Lookups.fixed(new Object[]{
                this, 
                netBeansSoapUIProject }));
    }



    public Node findPath(Node root, Object target) {
        //leave unimplemented for now
        return null;
    }

    

}
