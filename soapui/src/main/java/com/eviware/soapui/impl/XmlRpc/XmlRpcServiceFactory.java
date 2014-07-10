package com.eviware.soapui.impl.XmlRpc;

import com.eviware.soapui.config.InterfaceConfig;
import com.eviware.soapui.config.XmlRpcServiceConfig;
import com.eviware.soapui.impl.InterfaceFactory;
import com.eviware.soapui.impl.wsdl.WsdlProject;

public class XmlRpcServiceFactory implements InterfaceFactory<XmlRpcService> {
    public static String getType() {
        return "xmlrpc";
    }

    @Override
    public XmlRpcService createNew(WsdlProject project, String name) {
        XmlRpcService service = build(project, project.getConfig().addNewInterface());
        service.setName(name);

        return service;
    }

    @Override
    public XmlRpcService build(WsdlProject project, InterfaceConfig config) {
        return new XmlRpcService(project, (XmlRpcServiceConfig) config.changeType(XmlRpcServiceConfig.type));
    }
}
