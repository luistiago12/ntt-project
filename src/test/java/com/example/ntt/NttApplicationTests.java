package com.example.ntt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class NttApplicationTests {

	@Test
	void contextLoads() {
	}

	@Configuration
	static class TestConfig {
		@Bean
		public RedisConnectionFactory redisConnectionFactory() {
			return new LettuceConnectionFactory();
		}

		@Bean
		public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
			RedisTemplate<String, Object> template = new RedisTemplate<>();
			template.setConnectionFactory(redisConnectionFactory);
			return template;
		}
	}
}
