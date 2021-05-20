package com.innovat.RegistroPresenze;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
public class BeanConfig {

	@Bean
	public Map<String, String> getSessionMap() {
		return Hazelcast.newHazelcastInstance().getMap("tokenMap");
	}
}
