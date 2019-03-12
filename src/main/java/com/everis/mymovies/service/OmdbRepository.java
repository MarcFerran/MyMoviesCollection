package com.everis.mymovies.service;

import com.omertron.omdbapi.OMDBException;
import com.omertron.omdbapi.OmdbApi;
import com.omertron.omdbapi.model.OmdbVideoFull;
import com.omertron.omdbapi.tools.OmdbBuilder;

public class OmdbRepository {
    //b8003d99
    //75714e82
    //ba3fb5fb
    //partreon: 24de38bd
    private OmdbApi omDb;

    public OmdbRepository(final String apiKey) {
        this.omDb = new OmdbApi(apiKey);
    }

    public OmdbVideoFull getInfoByImdbId(final String imdbId) throws OMDBException {
        return omDb.getInfo(new OmdbBuilder().setImdbId(imdbId).build());
    }

    public OmdbVideoFull getInfoByTitle(final String movieTitle) throws OMDBException {
        return omDb.getInfo(new OmdbBuilder().setTitle(movieTitle).build());
    }

}
