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

import org.eclipse.swt.SWT;

class Platform {
    private static String platformString = SWT.getPlatform();

    // prevent instantiation
    private Platform() {
    }
    
    public static boolean isWin32() {
        return "win32".equals(platformString); //$NON-NLS-1$
    }
    
    public static boolean isGtk() {
        return "gtk".equals(platformString); //$NON-NLS-1$
    }
    
}
