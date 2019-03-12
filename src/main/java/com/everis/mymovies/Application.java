package com.everis.mymovies;

import com.everis.mymovies.service.OmdbRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.everis.mymovies")
@EnableMongoRepositories(basePackages = "com.everis.mymovies.service")
public class Application {

    @Value("${myMoviesCollection.omdb.apiKey}")
    private String omdbApiKey;

    @Bean
    public OmdbRepository omdbRepository() {
        return new OmdbRepository(omdbApiKey);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
