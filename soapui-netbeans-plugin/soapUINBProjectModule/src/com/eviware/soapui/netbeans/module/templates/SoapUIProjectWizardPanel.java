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

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel just asking for basic info.
 */
public class SoapUIProjectWizardPanel implements WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel {
    
    private WizardDescriptor wizardDescriptor;
    private SoapUIProjectPanelVisual component;
    
    /** Creates a new instance of templateWizardPanel */
    public SoapUIProjectWizardPanel() {
    }
    
    public Component getComponent() {
        if (component == null) {
            component = new SoapUIProjectPanelVisual(this);
            component.setName(NbBundle.getMessage(SoapUIProjectWizardPanel.class, "LBL_CreateProjectStep"));
        }
        return component;
    }
    
    public HelpCtx getHelp() {
        return new HelpCtx(SoapUIProjectWizardPanel.class);
    }
    
    public boolean isValid() {
        getComponent();
        return component.valid(wizardDescriptor);
    }
    
    private final Set/*<ChangeListener>*/ listeners = new HashSet(1); // or can use ChangeSupport in NB 6.0
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    protected final void fireChangeEvent() {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(ev);
        }
    }
    
    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        component.read(wizardDescriptor);
    }
    
    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        component.store(d);
    }
    
    public boolean isFinishPanel() {
        return true;
    }
    
    public void validate() throws WizardValidationException {
        getComponent();
        component.validate(wizardDescriptor);
    }
    
}
