package com.eviware.soapui.impl.wsdl.actions.project;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.XmlRpcServiceBuilder;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.support.HelpUrls;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.support.ADialogBuilder;
import com.eviware.x.form.support.AField;
import com.eviware.x.form.support.AForm;

/**
 * Add new XML-RPC Interface
 */

public class AddXmlRpcAction extends AbstractSoapUIAction<WsdlProject> {

    public AddXmlRpcAction() {
        super("New XML-RPC Service from URI", "Adds a new XML-RPC Service to this project");
    }

    public void perform(WsdlProject project, Object param) {
        XFormDialog dialog = ADialogBuilder.buildDialog(Form.class);
        dialog.setValue(Form.URL, "");

        while (dialog.show()) {
            String url = dialog.getValue(Form.URL).trim();

            try {
                XmlRpcServiceBuilder.CreateService(project, url);
                break;
            } catch (Exception e) {
                SoapUI.logError(e);
            }
        }
    }

    @AForm(name = "New XML-RPC Service from URI", description = "Adds a new XML-RPC Service to this project", helpUrl = HelpUrls.NEWPROJECT_HELP_URL, icon = UISupport.TOOL_ICON_PATH)
    public interface Form {
        @AField(description = "URL or filename of XML-RPC definition", type = AField.AFieldType.FILE)
        public final static String URL = "XML-RPC Location:";
    }
}
