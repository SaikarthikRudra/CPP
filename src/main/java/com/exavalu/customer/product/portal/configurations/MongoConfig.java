package com.exavalu.customer.product.portal.configurations;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

	@Value("${mongodb.uri}")
	private String mongoUri;

//    @Value("${spring.data.mongodb.port}")
//    private int mongoPort;

    @Value("${spring.data.mongodb.database1}")
    private String mongoDatabase1;

    @Value("${spring.data.mongodb.database2}")
    private String mongoDatabase2;

    @Value("${spring.data.mongodb.database3}")
    private String mongoDatabase3;

    @Value("${spring.data.mongodb.database4}")
    private String mongoDatabase4;

	@Primary
	@Bean
	public MongoTemplate MumbaiMongoTemplate() {
		MongoClient mongoClient = MongoClients.create(mongoUri);
		return new MongoTemplate(mongoClient, mongoDatabase3);
	}
	@Bean
	public MongoTemplate KolkataMongoTemplate() {
		MongoClient mongoClient = MongoClients.create(mongoUri);
		return new MongoTemplate(mongoClient, mongoDatabase1);
	}

	@Bean
	public MongoTemplate HyderabadMongoTemplate() {
		MongoClient mongoClient = MongoClients.create(mongoUri);
		return new MongoTemplate(mongoClient, mongoDatabase2);
	}

	@Bean
	public MongoTemplate BangaloreMongoTemplate() {
		MongoClient mongoClient = MongoClients.create(mongoUri);
		return new MongoTemplate(mongoClient, mongoDatabase4);
	}

}
