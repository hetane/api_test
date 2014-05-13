package com.eviware.soapui.impl.wsdl.panels.teststeps.support;

import com.eviware.soapui.support.UISupport;
import com.jgoodies.forms.builder.ButtonBarBuilder;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;

/**
 * Created by aleshin on 5/13/2014.
 */
public class ShowCodeCompletionAction extends AbstractAction {

    private RSyntaxTextArea editArea;
    private JDialog dialog;

    public ShowCodeCompletionAction(RSyntaxTextArea editArea) {
        super("Code Completion");
        if (UISupport.isMac()) {
            putValue(Action.ACCELERATOR_KEY, UISupport.getKeyStroke("esc"));
        } else {
            putValue(Action.ACCELERATOR_KEY, UISupport.getKeyStroke("control space"));
        }
        this.editArea = editArea;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        show();
    }

    private void show() {
        if (dialog == null) {
            buildDialog();
        }

        editArea.requestFocusInWindow();
        UISupport.showDialog(dialog);

    }


    private void buildDialog() {
        Window window = SwingUtilities.windowForComponent(editArea);

        dialog = new JDialog(window, "Code Completion");
        dialog.setModal(false);

    }
}
