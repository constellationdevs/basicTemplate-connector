package coop.constellation.connectorservices.basictemplate;

import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.connectorservices.common.workflow.*;
import com.xtensifi.dspco.ConnectorMessage;
import coop.constellation.connectorservices.basictemplate.helpers.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import coop.constellation.connectorservices.basictemplate.controller.BaseParamsSupplier;
import coop.constellation.connectorservices.basictemplate.helpers.EnhancedConnectorLogging;
import coop.constellation.connectorservices.basictemplate.helpers.StdoutConnectorLogging;

import static org.springframework.http.HttpStatus.OK;

@Configuration
public class BeansConfig {

    @Bean
    @Profile("!local")
    ConnectorHubService connectorHubService() {
        return new ConnectorHubServiceImpl();
    }

    @AllArgsConstructor
    public static class LocalConnectorResponseEntityBuilder implements ConnectorResponseEntityBuilder {
        @Override
        public ResponseEntity<ConnectorMessage> build(HttpStatus status,
                CompletableFuture<ConnectorMessage> messageFuture) {
            ConnectorMessage message;
            try {
                message = messageFuture.get(); // block
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            ResponseEntity<ConnectorMessage> responseEntity = ResponseEntity.status(OK).body(message);
            return responseEntity;
        }
    }

    /**
     * This is the default response entity builder used when deployed.
     * It doesn't utilize the completableFuture. Instead, it returns an OK status
     * immediately.
     * The future is assumed to POST its results to the connector hub in deployment.
     * This is mainly meant to be used in a with the MockConnectorHubService.
     */
    @Bean
    @Profile("!local")
    ConnectorResponseEntityBuilder responseEntityBuilder() {
        return (status, message) -> ResponseEntity.status(OK).build();
    }

    /**
     * This builder waits for the completable future before returning its response.
     * This is intended to be used in a local profile alongside the
     * MockConnectorHubService.
     */
    @Bean
    @Profile("local")
    ConnectorResponseEntityBuilder localResponseEntityBuilder() {
        return new LocalConnectorResponseEntityBuilder();
    }

    @Bean
    @Profile("!local")
    ConnectorLogging connectorLogging() {
        return new EnhancedConnectorLogging();
    }

    @Bean
    @Profile("local")
    ConnectorLogging localConnectorLogging() {
        return new StdoutConnectorLogging();
    }

    /**
     * Set up extra params that this connector should use as a base for every
     * request.
     */
    @Bean
    BaseParamsSupplier baseParamsSupplier() {
        return () -> Map.of("localCpConnectionInitSql", "SET TIME ZONE 'UTC';");
    }

    // @Bean
    // ConnectorHubService connectorHubService() {
    // return new ConnectorHubServiceImpl();
    // }

    @Bean
    ConnectorConfig connectorConfig() {
        return new ConnectorConfig();
    }

    /**
     * Configure a blind index builder for creating member number indices to speed
     * up
     * querying for member numbers (since member numbers are encrypted in storage).
     */
    @Bean
    BlindIndexBuilder blindIndexBuilder() {
        return new BlindIndexBuilder.DefaultBlindIndexBuilder();
    }

}