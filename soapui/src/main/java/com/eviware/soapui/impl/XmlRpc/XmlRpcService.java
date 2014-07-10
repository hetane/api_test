package com.eviware.soapui.impl.XmlRpc;

import com.eviware.soapui.config.XmlRpcServiceConfig;
import com.eviware.soapui.impl.support.AbstractInterface;
import com.eviware.soapui.impl.support.definition.support.AbstractDefinitionContext;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.iface.Operation;

import java.util.List;

public class XmlRpcService extends AbstractInterface<XmlRpcServiceConfig> {
    public XmlRpcService(WsdlProject project, XmlRpcServiceConfig interfaceConfig) {
        super(interfaceConfig, project, "/XmlRpcService.png");
    }

    @Override
    public Operation getOperationAt(int index) {
        return null;
    }

    @Override
    public int getOperationCount() {
        return 0;
    }

    @Override
    public Operation getOperationByName(String name) {
        return null;
    }

    @Override
    public String getTechnicalId() {
        return null;
    }

    @Override
    public List<Operation> getOperationList() {
        return null;
    }

    @Override
    public String getInterfaceType() {
        return null;
    }

    @Override
    public AbstractDefinitionContext getDefinitionContext() {
        return null;
    }

    @Override
    public String getDefinition() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public boolean isDefinitionShareble() {
        return false;
    }
}
