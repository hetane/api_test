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

package com.eviware.x.impl.swt.support;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

class RecursiveContainerListener implements ContainerListener {
    private final ContainerListener listener;
    
    RecursiveContainerListener(ContainerListener listener) {
        assert listener != null;
        
        this.listener = listener;
    }

    private void handleAdd(Container source, Component c) {
        assert source != null;
        assert c != null;
        assert listener != null;
        assert EventQueue.isDispatchThread();    // On AWT event thread
        
        // System.out.println("Listening to: " + c);
        listener.componentAdded(new ContainerEvent(source, ContainerEvent.COMPONENT_ADDED, c));
        if (c instanceof Container) {
            ((Container)c).addContainerListener(this);
        }
    }
    
    private void handleRemove(Container source, Component c) {
        assert source != null;
        assert c != null;
        assert listener != null;
        assert EventQueue.isDispatchThread();    // On AWT event thread

        // System.out.println("Stopped Listening to: " + c);
        listener.componentRemoved(new ContainerEvent(source, ContainerEvent.COMPONENT_REMOVED, c));
        if (c instanceof Container) {
            ((Container)c).removeContainerListener(this);
        }
    }
    
    private void handleAllAdds(Container source, Component child) {
        assert source != null;
        assert child != null;
        assert EventQueue.isDispatchThread();    // On AWT event thread

        if (child instanceof Container) {
            Container container = (Container)child;
            Component[] children = container.getComponents();
            for (int i = 0; i < children.length; i++) {
                handleAllAdds(container, children[i]);
            }
        }
        handleAdd(source, child);
    }
    
    private void handleAllRemoves(Container source, Component child) {
        assert source != null;
        assert child != null;
        assert EventQueue.isDispatchThread();    // On AWT event thread

        if (child instanceof Container) {
            Container container = (Container)child;
            Component[] children = container.getComponents();
            for (int i = 0; i < children.length; i++) {
                handleAllRemoves(container, children[i]);
            }
        }
        handleRemove(source, child);

    }

    public void componentAdded(ContainerEvent e) {
        assert e != null;
        assert EventQueue.isDispatchThread();    // On AWT event thread
        
        Container source = (Container)e.getSource();
        handleAllAdds(source, e.getChild());
    }
    
    public void componentRemoved(ContainerEvent e) {
        assert e != null;
        assert EventQueue.isDispatchThread();    // On AWT event thread
        
        Container source = (Container)e.getSource();
        handleAllRemoves(source, e.getChild());
    }
}

