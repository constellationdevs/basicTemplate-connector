package coop.constellation.connectorservices.basictemplate.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.xtensifi.cufx.CustomData;
import com.xtensifi.cufx.ValuePair;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JsonHelper {

    /**
     * Transforms customData into a map of strings to JsonNode values. For example:
     * "customData":[ { "valuePair":[ { "name":"cdpShowInContainer", "value":true }
     * ] }, { "valuePair":[ { "name":"cdpIsRetirementAccount", "value":false } ] } ]
     * would become:
     *
     * { "cdpShowInContainer" -> true, "cdpIsRetirementAccount" -> false }
     *
     * @param customDataValuePairs customData node
     * @return a map of all found valuePair names and values
     */
    public static HashMap<String, JsonNode> customDataValuePairsToMap(ArrayNode customDataValuePairs) {
        // iterate through each node in the custom data array.
        // Then, get its valuePair and add an entry into a hash map using its name as
        // the key
        HashMap<String, JsonNode> valuePairs = new HashMap<>(customDataValuePairs.size());
        for (JsonNode customDataItem : customDataValuePairs) {
            String name = customDataItem.get("name").textValue();
            JsonNode value = customDataItem.get("value");
            if (name != null && value != null) {
                valuePairs.put(name, value);
            }
        }
        return valuePairs;
    }

    public static HashMap<String, String> customDataToMap(CustomData customData) {
        // iterate through each node in the custom data array.
        // Then, get its valuePair and add an entry into a hash map using its name as
        // the key
        HashMap<String, String> valuePairs = new HashMap<>(customData.getValuePair().size());
        for (ValuePair customDataItem : customData.getValuePair()) {
            String name = customDataItem.getName();
            String value = customDataItem.getValue();
            if (name != null && value != null) {
                valuePairs.put(name, value);
            }
        }
        return valuePairs;
    }

    public static String createFailureResponse(Object reason, ObjectMapper mapper) {
        return createFailureResponse(reason, Collections.emptyMap(), mapper);
    }

    public static String createFailureResponse(Object message, Map<String, Object> otherProperties,
            ObjectMapper mapper) {
        Map<String, Object> responseObj = new HashMap<>();
        responseObj.put("success", false);
        responseObj.put("message", message);
        if (otherProperties != null) {
            responseObj.putAll(otherProperties);
        }
        return createResponse(responseObj, mapper);
    }

    public static String createFailureMessage(String message, ObjectMapper mapper) {
        Map<String, Object> innerResponse = new HashMap<>();
        innerResponse.put("success", false);
        innerResponse.put("message", message);
        try {
            return mapper.writeValueAsString(innerResponse);
        } catch (JsonProcessingException jpEx) {
            return "{ \"success\": false }";
        }
    }

    public static String createResponse(Object responseObj, ObjectMapper mapper) {
        Map<String, Object> outerMap = new HashMap<>();
        outerMap.put("response", responseObj);
        try {
            return mapper.writeValueAsString(outerMap);
        } catch (JsonProcessingException jpEx) {
            return createFailureResponse("Could not create success response", mapper);
        }
    }

    public static String createFailureMessageWithData(String message, ObjectMapper mapper, String keyName,
            Object dataObject) {
        Map<String, Object> innerResponse = new HashMap<>();
        innerResponse.put("success", false);
        innerResponse.put("message", message);
        innerResponse.put(keyName, dataObject);
        try {
            return mapper.writeValueAsString(innerResponse);
        } catch (JsonProcessingException jpEx) {
            return "{ \"success\": false }";
        }
    }

}
