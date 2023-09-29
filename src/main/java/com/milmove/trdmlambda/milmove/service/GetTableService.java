package com.milmove.trdmlambda.milmove.service;


import java.util.Map;
import org.apache.cxf.frontend.ClientProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.cxf.endpoint.Client;

import com.milmove.trdmlambda.milmove.config.TrdmProps;
import com.milmove.trdmlambda.milmove.model.gettable.GetTableRequest;
import com.milmove.trdmlambda.milmove.model.gettable.GetTableResponse;
import com.milmove.trdmlambda.milmove.util.ClientPasswordCallback;
import com.milmove.trdmlambda.milmove.util.MyLogInterceptor;
import com.milmove.trdmlambda.milmove.util.SHA512PolicyLoader;

import jakarta.xml.ws.BindingProvider;
import trdm.returntableservice.ReturnTable;
import trdm.returntableservice.ReturnTableInput;
import trdm.returntableservice.ReturnTableRequestElement;
import trdm.returntableservice.ReturnTableWSSoapHttpPort;
import trdm.returntableservice.ReturnTableInput.TRDM;

@Service
public class GetTableService {

    @Autowired
    private TrdmProps trdmProps; // Not currently used

    /**
     * Processes REST request for getTable
     * 
     * @param request GetTableRequest
     * @return GetTableResponse
     */
    public GetTableResponse getTableRequest(GetTableRequest request) {
        // return callSoapWebService(trdmProps.getServiceUrl(), "POST", request);
        buildSoapBody(request);
        return null;
    }

    /**
     * Builds SOAP body from REST request
     * 
     * @param request - GetTableRequest
     * @return built SOAP XML body with header.
     */
    private void buildSoapBody(GetTableRequest request) {
        ReturnTable returnTable = new ReturnTable();
        ReturnTableWSSoapHttpPort returnTableWSSoapHttpPort = returnTable.getReturnTableWSSoapHttpPort();

        ReturnTableRequestElement requestElement = new ReturnTableRequestElement();
        ReturnTableInput input = new ReturnTableInput();
        TRDM trdm = new TRDM();
        trdm.setPhysicalName(request.getPhysicalName());
        trdm.setReturnContent(Boolean.valueOf(request.getPhysicalName()));
        trdm.setContentUpdatedSinceDateTime(null);

        input.setTRDM(trdm);
        requestElement.setInput(input);

        Client client = ClientProxy.getClient(returnTableWSSoapHttpPort);
        client.getInInterceptors().add(new MyLogInterceptor());
        client.getOutInterceptors().add(new MyLogInterceptor());

        Map<String, Object> ctx = ((BindingProvider) returnTableWSSoapHttpPort).getRequestContext();
        ctx.put("ws-security.callback-handler", ClientPasswordCallback.class.getName());
        // ctx.put("ws-security.encryption.properties", "config/crypto.properties");
        ctx.put("ws-security.signature.properties", "etc/client_sign.properties");
        ctx.put("ws-security.encryption.username", "milmove");

        // Register custom algorithm
        new SHA512PolicyLoader(client.getBus());

        returnTableWSSoapHttpPort.getTable(requestElement);

        // return null;
    }
}