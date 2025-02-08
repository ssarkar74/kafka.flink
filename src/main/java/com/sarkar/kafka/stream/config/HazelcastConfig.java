package com.sarkar.kafka.stream.config;

import com.hazelcast.config.*;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {
    @Bean
    public Config config(){
        return new Config()
                .setInstanceName("hazelcast-instance")
                .addMapConfig(new MapConfig()
                        .setName("store-cache")
                        .setTimeToLiveSeconds(200)
                        .setEvictionConfig(new EvictionConfig()
                                .setSize(100000)
                                .setMaxSizePolicy(MaxSizePolicy.FREE_HEAP_SIZE)
                                .setEvictionPolicy(EvictionPolicy.LRU)))
                .setProperty("hazelcast.logging.type", "slf4j");
    }
}
