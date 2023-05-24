package coop.constellation.connectorservices.basictemplate.handlers;

import com.xtensifi.dspco.ConnectorMessage;

public interface BasicTemplateHandler {
    public void init();

    public String requestExternalData(String tileParameter1, String ParamName, String ParamValue,
            ConnectorMessage connectorMessage);
}