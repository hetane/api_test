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

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author Sofia
 */
public class InplaceComboBoxPropertyEditor implements InplaceEditor {
        private final JComboBox combo = new JComboBox();
        private PropertyEditor editor = null;
    
     public void setAvailableValues(Object[] availableValues) {
         combo.setModel(new DefaultComboBoxModel(availableValues));
    }
        
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            editor = propertyEditor;
            reset();
        }

        public JComponent getComponent() {
            return combo;
        }

        public void clear() {
            //avoid memory leaks:
            editor = null;
            model = null;
        }

        public Object getValue() {
            return combo.getSelectedItem();
        }

        public void setValue(Object object) {
            combo.setSelectedItem(object);
        }

        public boolean supportsTextEntry() {
            return true;
        }

        public void reset() {
       
            if (editor.getValue() != null) {
                combo.setSelectedItem(editor.getValue());
            }
        }

        public KeyStroke[] getKeyStrokes() {
            return new KeyStroke[0];
        }

        public PropertyEditor getPropertyEditor() {
            return editor;
        }

        public PropertyModel getPropertyModel() {
            return model;
        }

        private PropertyModel model;
        public void setPropertyModel(PropertyModel propertyModel) {
            this.model = propertyModel;
        }

        public boolean isKnownComponent(Component component) {
            return component == combo || combo.isAncestorOf(component);
        }

        public void addActionListener(ActionListener actionListener) {
           //do nothing - not needed for this component
        }

        public void removeActionListener(ActionListener actionListener) {
           //do nothing - not needed for this component
        }
    }
    
