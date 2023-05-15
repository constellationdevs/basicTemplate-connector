package coop.constellation.connectorservices.basictemplate.helpers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtensifi.cufx.CustomData;
import com.xtensifi.cufx.ValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataStructureHelpers {

    /**
     * Turn custom data into a map of names to values
     * 
     * @param customData cufx cusotm data
     * @return map of names to values
     */
    public static Map<String, String> customDataToMap(CustomData customData) {
        return customData.getValuePair().stream()
                .collect(Collectors.toMap(ValuePair::getName, ValuePair::getValue, (vp1, vp2) -> vp2));
    }

    /**
     * Maps "false" and "true" to "off" and "on"
     * 
     * @throws IOException if input neither "false" nor "true"
     */
    public static String falseTrueToOffOn(String s) throws IOException {
        if (s.equalsIgnoreCase("true")) {
            return "On";
        } else if (s.equalsIgnoreCase("false")) {
            return "Off";
        } else {
            throw new IOException("Could not convert " + s + " to Off/On");
        }
    }

    /**
     * Converts a boolean to "On" or "Off"
     */
    public static String boolToOffOn(boolean b) {
        return b ? "On" : "Off";
    }

    /**
     * Converts a pojo into name/value pairs.
     * 
     * @param mapper the object mapper used to parse the pojo
     * @param obj    pojo to convert
     * @return list of name/value pairs, one for each property of the object
     */
    public static List<BasicNameValuePair> toNameValuePairs(ObjectMapper mapper, Object obj) throws IOException {
        try {
            Map<String, String> thisAsMap = mapper.convertValue(obj, new TypeReference<>() {
            });
            return thisAsMap.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IOException("Could not convert object into name value pairs. " + e.getMessage(), e);
        }
    }

    public static <T> Optional<T> getLast(List<T> list) {
        if (list.size() == 0)
            return Optional.empty();
        else
            return Optional.of(list.get(list.size() - 1));
    }

    /**
     * Converts Boolean to boolean, returning false if Boolean was null
     */
    public static boolean boolToPrimitive(Boolean x) {
        return x == null ? false : x;
    }
}
