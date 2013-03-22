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

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

class CleanResizeListener extends ControlAdapter {
    private Rectangle oldRect = null;
    public void controlResized(ControlEvent e) {
        assert e != null;
        assert Display.getCurrent() != null;     // On SWT event thread
        
        // Prevent garbage from Swing lags during resize. Fill exposed areas 
        // with background color. 
        Composite composite = (Composite)e.widget;
        //Rectangle newRect = composite.getBounds();
        //newRect = composite.getDisplay().map(composite.getParent(), composite, newRect);
        Rectangle newRect = composite.getClientArea();
        if (oldRect != null) {
            int heightDelta = newRect.height - oldRect.height;
            int widthDelta = newRect.width - oldRect.width;
            if ((heightDelta > 0) || (widthDelta > 0)) {
                GC gc = new GC(composite);
                try {
                    gc.fillRectangle(newRect.x, oldRect.height, newRect.width, heightDelta);
                    gc.fillRectangle(oldRect.width, newRect.y, widthDelta, newRect.height);
                } finally {
                    gc.dispose();
                }
            }
        }
        oldRect = newRect;
    }
}
