package coop.constellation.connectorservices.basictemplate.handlers;

import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.dspco.ConnectorMessage;
import com.xtensifi.dspco.UserData;

import org.springframework.jdbc.core.JdbcTemplate;

import coop.constellation.connectorservices.basictemplate.controller.ConnectorControllerBase;
import coop.constellation.connectorservices.basictemplate.helpers.MultiLazyDataSourcePool;
import javax.sql.DataSource;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

abstract class HandlerBase implements HandlerLogic {

    /**
     * Creates a JdbcTemplate using the lazy pooling datasource.
     * 
     * @param parms The parms passed to the request
     * @return
     */
    protected JdbcTemplate createJdbcTemplate(final Map<String, String> parms, ConnectorLogging clog,
            ConnectorMessage cm) {
        return new JdbcTemplate(MultiLazyDataSourcePool.getDataSource(parms, () -> parms.get("org"), clog, cm));
    }

    /**
     * Creates a NamedParameterJdbcTemplate using the lazy pooling datasource.
     * 
     * @param parms The parms passed to the request
     * @return
     */
    NamedParameterJdbcTemplate createNamedParameterJdbcTemplate(final Map<String, String> parms, ConnectorLogging clog,
            ConnectorMessage cm) {
        return new NamedParameterJdbcTemplate(
                MultiLazyDataSourcePool.getDataSource(parms, () -> parms.get("org"), clog, cm));
    }

    DataSource getDataSource(final Map<String, String> params, ConnectorLogging clog, ConnectorMessage cm) {
        return MultiLazyDataSourcePool.getDataSource(params, () -> params.get("org"), clog, cm);
    }

    public JdbcTemplate createJdbc(ConnectorMessage connectorMessage, Map<String, String> baseParams,
            ConnectorLogging clog) {
        Map<String, String> parms = ConnectorControllerBase.getAllParams(connectorMessage, baseParams);

        return createJdbcTemplate(parms, clog, connectorMessage);
    }

    Boolean isAauthenticated(UserData userData) {

        String memberID = userData.getUserId();
        try {
            if (memberID == null || memberID == "") {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;

        }
    }

}