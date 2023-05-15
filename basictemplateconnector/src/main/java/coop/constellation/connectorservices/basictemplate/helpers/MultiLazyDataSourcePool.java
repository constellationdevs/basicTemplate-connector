package coop.constellation.connectorservices.basictemplate.helpers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.dspco.ConnectorMessage;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Math.toIntExact;

/**
 * DataSource that gets created when needed using request parameters.
 * Manages multiple hikari datasource pools.
 * Each pool is identified by a user-defined function.
 */
@Slf4j
public class MultiLazyDataSourcePool {

    // define a callback for pools when they're evicted from the cache
    private static final RemovalListener<String, ManagedDataSourcePool> poolRemovalListener = (s, managedDataSourcePool,
            removalCause) -> {
        log.info("removal listener fired for " + s);
        if (managedDataSourcePool != null && managedDataSourcePool.getPool() != null) {
            log.info("closing the pool for %s because it %s", s, removalCause.toString());
            managedDataSourcePool.getPool().close();
        } else {
            if (managedDataSourcePool == null)
                log.error("unable to close the pool because the managed data source is null for " + s);
            else if (managedDataSourcePool.getPool() == null)
                log.error("unable to close the pool because the pool is null for " + s);
        }
    };

    // Thread-safe cache of pool ids to pools.
    private static final Cache<String, ManagedDataSourcePool> pools = Caffeine.newBuilder()
            .expireAfterAccess(24, TimeUnit.HOURS)
            .scheduler(Scheduler.systemScheduler())
            .removalListener(poolRemovalListener)
            .build();

    public interface PoolIdSupplier {
        String getPoolId();
    }

    /**
     * Gets a DataSource described by values in params. This method is thread safe.
     *
     * If relevant values change, the old data source is closed and a new one is
     * returned.
     * Be aware that the data source will thrash and degrade performance if this
     * method is constantly called with
     * params that contain differing CP settings.
     *
     * When a DataSource is requested, the user-supplied function is invoked to
     * return the id for the pool you'r requesting.
     * If the requested pool currently exists, the HikariParams associated with that
     * pool are checked for any differences
     * from the pool's last configuration. If there are changes, the original pool
     * is shutdown and replaced with a new pool
     * using the new configuration. If there are not changes, the original pool is
     * returned.
     *
     * This class maintains a thread-safe cache of pool IDs to pools and their
     * configurations.
     * Pools will be evicted from the cache if enough time passes without them being
     * used.
     *
     * @param params Request params. These should contain parameters that map to the
     *               HikariParams model. Any missing hikari params will default to
     *               natural hikari defaults.
     * @return A reference to the DataSource defined by the params
     */
    public static DataSource getDataSource(final Map<String, String> params, PoolIdSupplier poolIdSupplier,
            ConnectorLogging clog, ConnectorMessage cm) {

        final HikariParams hikariParams = new HikariParams(params);

        // We have a cache that maps pool Ids to pool locks, and a cache that maps pool
        // ids to the pools themselves.
        // First, determine which pool is being requested.
        String poolId = poolIdSupplier.getPoolId();
        clog.info(cm, "This is the pool id: " + poolId);

        ManagedDataSourcePool managedDataSourcePool = pools.get(poolId,
                (id) -> // loader for new data source if there wasn't one in the cache.
                {
                    clog.info(cm, "No pool found in the cache -- creating a new pool for id: " + id);
                    return new ManagedDataSourcePool(hikariParams, setupConnectionPool(hikariParams, clog, cm),
                            new ReentrantLock());
                });

        if (managedDataSourcePool == null) {
            throw new RuntimeException("Could not find lock for pool " + poolId);
        }

        // Each poolId's operations need to be synchronized, so first acquire the lock
        // for this pool.
        Lock poolLock = managedDataSourcePool.getPoolLock();

        try {
            poolLock.lock();
            // Now we have acquired the lock. It's safe to compare the new params to the old

            // Reset the connection pool if the new HikariParams are different from the old
            // ones.
            if (!hikariParams.equals(managedDataSourcePool.hikariParams)) {
                clog.info(cm, "The params have changed for the pool.  Creating a new one pool with id " + poolId);

                // tear down old pool
                managedDataSourcePool.getPool().close();

                // create new pool, and update the cache entry
                HikariDataSource newPool = setupConnectionPool(hikariParams, clog, cm);
                managedDataSourcePool.setHikariParams(hikariParams);
                managedDataSourcePool.setPool(newPool);
            } else {
                clog.info(cm,
                        "The params are for a new pool or have NOT changed for the existing pool. Using pool with id: "
                                + poolId);

            }
            return managedDataSourcePool.getPool();
        } finally {
            // unlock the pool after method returns OR after an exception
            poolLock.unlock();
        }
    }

    /**
     * Sets up a Hikari connection pool with the params passed in, and then runs the
     * supplied consumer with the data source.
     */
    private static HikariDataSource setupConnectionPool(HikariParams hikariParams, ConnectorLogging clog,
            ConnectorMessage cm) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(org.postgresql.Driver.class.getName());
        dataSource.setJdbcUrl(hikariParams.getLocalDbUrl());
        dataSource.setUsername(hikariParams.getLocalDbUser());
        dataSource.setPassword(hikariParams.getLocalDbPassword());
        dataSource.setConnectionTimeout(hikariParams.getLocalCpConnectionTimeout());
        dataSource.setMaximumPoolSize(hikariParams.getLocalCpMaxPoolSize());
        dataSource.setIdleTimeout(hikariParams.getLocalCpIdleTimeout());
        dataSource.setMinimumIdle(hikariParams.getLocalCpMinimumIdle());
        dataSource.setMaxLifetime(hikariParams.getLocalCpMaxLifetime());
        dataSource.setLeakDetectionThreshold(hikariParams.getLocalCpLeakDetectionThreshold());
        if (hikariParams.getLocalCpPoolName() != null) {
            dataSource.setPoolName(hikariParams.getLocalCpPoolName());
        }
        if (hikariParams.getLocalCpConnectionInitSql() != null) {
            dataSource.setConnectionInitSql(hikariParams.getLocalCpConnectionInitSql());
        }
        clog.info(cm, "created a Hikari datasource for " + dataSource.getJdbcUrl());
        return dataSource;
    }

    /**
     * Models for the parameters that can be used to control a Hikari DataSource
     * pool.
     * Provides a constructor to create an object of this model from a params map.
     * Values that are missing will default to the natural hikariCP defaults.
     */
    @EqualsAndHashCode
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class HikariParams {

        // Required Parameters
        private final String localDbUrl;
        private final String localDbUser;
        private final String localDbPassword;

        // Optional Parameters
        private final Long localCpConnectionTimeout;
        private final Long localCpIdleTimeout;
        private final Long localCpMaxLifetime;
        private final Integer localCpMinimumIdle;
        private final Integer localCpMaxPoolSize;
        private final String localCpPoolName;
        private final Long localCpLeakDetectionThreshold;
        private final String localCpConnectionInitSql;

        public HikariParams(Map<String, String> params) {
            // required params
            this.localDbUrl = params.get("localDbUrl");
            this.localDbUser = params.get("localDbUser");
            this.localDbPassword = params.get("localDbPassword");

            // read other params.
            // set defaults to hikari's normal defaults
            this.localCpConnectionTimeout = getLongParamOrDefault(params, "localCpConnectionTimeout", 30000L);
            this.localCpIdleTimeout = getLongParamOrDefault(params, "localCpIdleTimeout", 600000L);
            this.localCpMaxLifetime = getLongParamOrDefault(params, "localCpMaxLifetime", 1800000L);
            this.localCpMaxPoolSize = toIntExact(getLongParamOrDefault(params, "localCpMaxPoolSize", 10L));
            this.localCpMinimumIdle = toIntExact(
                    getLongParamOrDefault(params, "localCpMinIdle", this.localCpMaxPoolSize.longValue()));
            this.localCpPoolName = params.get("localCpPoolName");
            this.localCpLeakDetectionThreshold = getLongParamOrDefault(params, "localCpLeakDetectionThreshold", 0L);
            this.localCpConnectionInitSql = params.get("localCpConnectionInitSql");
        }

        private Long getLongParamOrDefault(Map<String, String> parms, String key, Long defaultLong) {
            try {
                String stringVal = parms.getOrDefault(key, defaultLong.toString());
                return Long.parseLong(stringVal);
            } catch (Exception e) {
                return defaultLong;
            }
        }

    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class ManagedDataSourcePool {
        private HikariParams hikariParams;
        private HikariDataSource pool;
        private ReentrantLock poolLock;
    }
}
