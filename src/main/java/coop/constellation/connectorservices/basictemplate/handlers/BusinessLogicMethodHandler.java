package coop.constellation.connectorservices.basictemplate.handlers;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.xtensifi.connectorservices.common.workflow.ConnectorState;
import com.xtensifi.dspco.ConnectorMessage;
import com.xtensifi.dspco.UserData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BusinessLogicMethodHandler extends HandlerBase {

    @Override
    public void generateResponse(Map<String, String> parms, UserData userData, ConnectorMessage connectorMessage)
            throws Exception {
        String response = "{\"response\": {\"MyResponse\": \"Response from BusinessLogicMethod\"}}";
        connectorMessage.setResponse(response);
    }

    @Override
    public String generateResponse(Map<String, String> parms, ConnectorState connectorState)
            throws IOException, ParseException {
        return null;
    }

}