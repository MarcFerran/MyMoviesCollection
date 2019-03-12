package com.everis.mymovies.service;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie, String> {
	
	public Movie findByImdbId(String imdbId);
	
	public void deleteByImdbId(String imdbId);

}
