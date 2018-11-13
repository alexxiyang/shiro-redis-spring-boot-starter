package org.crazycake.shiro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

@Configuration
@PropertySource("classpath:shiro-redis.properties")
@EnableConfigurationProperties({RedisManagerProperties.class, CacheManagerProperties.class, RedisSessionDAOProperties.class})
@ConditionalOnProperty(name = "shiro-redis.enabled", matchIfMissing = true)
public class ShiroRedisAutoConfiguration {

    @Autowired
    private RedisManagerProperties redisManagerProperties;

    @Autowired
    private CacheManagerProperties cacheManagerProperties;

    @Autowired
    private RedisSessionDAOProperties redisSessionDAOProperties;

    @Bean
    @ConditionalOnMissingBean
    public IRedisManager redisManager() {
        IRedisManager redisManager = null;
        if ("standalone".equals(redisManagerProperties.getDeployMode())) {
            redisManager = createStandaloneRedisManager();
        }
        if ("sentinel".equals(redisManagerProperties.getDeployMode())) {
            redisManager = createRedisSentinelManager();
        }
        if ("cluster".equals(redisManagerProperties.getDeployMode())) {
            redisManager = createRedisClusterManager();
        }

        return redisManager;
    }

    private IRedisManager createRedisClusterManager() {
        IRedisManager redisManager;
        RedisClusterManager clusterManager = new RedisClusterManager();
        if (!StringUtils.isEmpty(redisManagerProperties.getHost())) {
            clusterManager.setHost(redisManagerProperties.getHost());
        }
        if (redisManagerProperties.getTimeout() != null) {
            clusterManager.setTimeout(redisManagerProperties.getTimeout());
        }
        if (redisManagerProperties.getSoTimeout() != null) {
            clusterManager.setSoTimeout(redisManagerProperties.getSoTimeout());
        }
        if (redisManagerProperties.getMaxAttempts() != null) {
            clusterManager.setMaxAttempts(redisManagerProperties.getMaxAttempts());
        }
        if (!StringUtils.isEmpty(redisManagerProperties.getPassword())) {
            clusterManager.setPassword(redisManagerProperties.getPassword());
        }
        if (redisManagerProperties.getDatabase() != null) {
            clusterManager.setDatabase(redisManagerProperties.getDatabase());
        }
        if (redisManagerProperties.getCount() != null) {
            clusterManager.setCount(redisManagerProperties.getCount());
        }

        redisManager = clusterManager;
        return redisManager;
    }

    private IRedisManager createRedisSentinelManager() {
        IRedisManager redisManager;
        RedisSentinelManager sentinelManager = new RedisSentinelManager();
        if (!StringUtils.isEmpty(redisManagerProperties.getHost())) {
            sentinelManager.setHost(redisManagerProperties.getHost());
        }
        if (redisManagerProperties.getTimeout() != null) {
            sentinelManager.setTimeout(redisManagerProperties.getTimeout());
        }
        if (!StringUtils.isEmpty(redisManagerProperties.getMasterName())) {
            sentinelManager.setMasterName(redisManagerProperties.getMasterName());
        }
        if (redisManagerProperties.getSoTimeout() != null) {
            sentinelManager.setSoTimeout(redisManagerProperties.getSoTimeout());
        }
        if (!StringUtils.isEmpty(redisManagerProperties.getPassword())) {
            sentinelManager.setPassword(redisManagerProperties.getPassword());
        }
        if (redisManagerProperties.getDatabase() != null) {
            sentinelManager.setDatabase(redisManagerProperties.getDatabase());
        }
        if (redisManagerProperties.getCount() != null) {
            sentinelManager.setCount(redisManagerProperties.getCount());
        }

        redisManager = sentinelManager;
        return redisManager;
    }

    private IRedisManager createStandaloneRedisManager() {
        IRedisManager redisManager;
        RedisManager standaloneManager = new RedisManager();
        if (!StringUtils.isEmpty(redisManagerProperties.getHost())) {
            standaloneManager.setHost(redisManagerProperties.getHost());
        }
        if (redisManagerProperties.getTimeout() != null) {
            standaloneManager.setTimeout(redisManagerProperties.getTimeout());
        }
        if (!StringUtils.isEmpty(redisManagerProperties.getPassword())) {
            standaloneManager.setPassword(redisManagerProperties.getPassword());
        }
        if (redisManagerProperties.getDatabase() != null) {
            standaloneManager.setDatabase(redisManagerProperties.getDatabase());
        }
        if (redisManagerProperties.getCount() != null) {
            standaloneManager.setCount(redisManagerProperties.getCount());
        }

        redisManager = standaloneManager;
        return redisManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisSessionDAO redisSessionDAO(IRedisManager redisManager) {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager);
        if (redisSessionDAOProperties.getExpire() != null) {
            redisSessionDAO.setExpire(redisSessionDAOProperties.getExpire());
        }
        if (!StringUtils.isEmpty(redisSessionDAOProperties.getKeyPrefix())) {
            redisSessionDAO.setKeyPrefix(redisSessionDAOProperties.getKeyPrefix());
        }
        if (redisSessionDAOProperties.getSessionInMemoryTimeout() != null) {
            redisSessionDAO.setSessionInMemoryTimeout(redisSessionDAOProperties.getSessionInMemoryTimeout());
        }
        return redisSessionDAO;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisCacheManager cacheManager(IRedisManager redisManager) {
        RedisCacheManager cacheManager = new RedisCacheManager();
        cacheManager.setRedisManager(redisManager);
        if (!StringUtils.isEmpty(cacheManagerProperties.getPrincipalIdFieldName())) {
            cacheManager.setPrincipalIdFieldName(cacheManagerProperties.getPrincipalIdFieldName());
        }
        if (cacheManagerProperties.getExpire() != null) {
            cacheManager.setExpire(cacheManagerProperties.getExpire());
        }
        if (!StringUtils.isEmpty(cacheManagerProperties.getKeyPrefix())) {
            cacheManager.setKeyPrefix(cacheManagerProperties.getKeyPrefix());
        }
        return cacheManager;
    }
}
