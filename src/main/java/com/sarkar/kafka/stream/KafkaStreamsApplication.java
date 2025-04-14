package com.sarkar.kafka.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

@EnableAsync
@SpringBootApplication
@Slf4j
public class KafkaStreamsApplication {

	public static void main(String[] args) {

		SpringApplication.run(KafkaStreamsApplication.class, args);
	}

}
