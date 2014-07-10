package com.eviware.soapui.impl;

import com.eviware.soapui.impl.XmlRpc.XmlRpcService;
import com.eviware.soapui.impl.XmlRpc.XmlRpcServiceFactory;
import com.eviware.soapui.impl.rest.RestURIParser;
import com.eviware.soapui.impl.rest.support.RestURIParserImpl;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.support.UISupport;

public class XmlRpcServiceBuilder {

    public static void CreateService(WsdlProject project, String URI) throws Exception {
        ModelItem service = buildService(project, URI);

        UISupport.select(service);
        UISupport.showDesktopPanel(service);
    }

    private static ModelItem buildService(WsdlProject project, String URI) throws Exception {
        RestURIParser parser = new RestURIParserImpl(URI);

        XmlRpcService service = (XmlRpcService) project.addNewInterface(parser.getEndpoint(), XmlRpcServiceFactory.getType());
        service.setName(parser.getResourceName());

        return service;
    }
}
