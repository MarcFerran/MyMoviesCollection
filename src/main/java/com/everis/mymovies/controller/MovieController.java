package com.everis.mymovies.controller;

import com.everis.mymovies.service.Movie;
import com.everis.mymovies.service.MovieRepository;
import com.everis.mymovies.service.OmdbRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omertron.omdbapi.OMDBException;
import com.omertron.omdbapi.model.OmdbVideoFull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yamj.api.common.exception.ApiExceptionType;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private OmdbRepository omdbRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/movies")
    public ResponseEntity<List<Map>> getAll() {
        final List<Map> collect = movieRepository.findAll().stream()
                .map(movie -> {
                    final OmdbVideoFull info = getVideoInfo(movie.getImdbId());
                    return joinInfos(info, movie);
                }).filter(map -> map != null)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(collect);
    }

    @SneakyThrows
    private OmdbVideoFull getVideoInfo(final String imdbId) {
        OmdbVideoFull infoByImdbId = null;
        try {
            infoByImdbId = omdbRepository.getInfoByImdbId(imdbId);
        } catch (OMDBException e) {
            if (e.getExceptionType().equals(ApiExceptionType.HTTP_503_ERROR)) return null;
            throw e;
        }
        return infoByImdbId;
    }

    @GetMapping("/movies/{id}")
    @SneakyThrows
    public ResponseEntity<Map> getMovie(@PathVariable String id) {

        final Movie myCollectionInfo = movieRepository.findByImdbId(id);

        final OmdbVideoFull info = omdbRepository.getInfoByImdbId(id);

        final Map mapInfo = joinInfos(info, myCollectionInfo);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapInfo);
    }

    @GetMapping("/movies/omdb/{id}")
    @SneakyThrows
    public ResponseEntity<Map> getMovieOmdb(@PathVariable String id) {

        final OmdbVideoFull info = omdbRepository.getInfoByImdbId(id);

        final Map mapInfo = joinInfos(info, null);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapInfo);
    }

    @GetMapping("/movies/title/{title}")
    @SneakyThrows
    public ResponseEntity<Map> getMovieByName(@PathVariable String title) {
        OmdbVideoFull info;
        try {
            info = omdbRepository.getInfoByTitle(title);
        } catch (OMDBException e) {
            if (e.getExceptionType().equals(ApiExceptionType.ID_NOT_FOUND))
                return ResponseEntity.notFound().build();
            else throw e;
        }
        final Movie myCollectionInfo = movieRepository.findByImdbId(info.getImdbID());
        final Map infoMap = joinInfos(info, myCollectionInfo);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(infoMap);
    }

    @PostMapping("/movies")
    @SneakyThrows
    public ResponseEntity<String> addMovie(@RequestBody Movie movie) {
        OmdbVideoFull info;
        if (movie.getTitle() != null) {
            try {
                info = omdbRepository.getInfoByTitle(movie.getTitle());
            } catch (OMDBException e) {
                if (e.getExceptionType().equals(ApiExceptionType.ID_NOT_FOUND))
                    return ResponseEntity.notFound().build();
                else throw e;
            }
        } else if (movie.getImdbId() != null) {
            try {
                info = omdbRepository.getInfoByImdbId(movie.getImdbId());
            } catch (OMDBException e) {
                if (e.getExceptionType().equals(ApiExceptionType.ID_NOT_FOUND))
                    return ResponseEntity.notFound().build();
                else throw e;
            }
        } else return ResponseEntity.badRequest().build();

        movie.setImdbId(info.getImdbID());
        movieRepository.save(movie);
        return ResponseEntity.created(new URI("")).body(movie.getImdbId());
    }

    @DeleteMapping("movies/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable("id") String id) {
        movieRepository.deleteByImdbId(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("movies/deleteMany/{number}")
    public ResponseEntity<Void> deleteMovies(@PathVariable("number") Integer number) {
        final List<Movie> all = movieRepository.findAll();
        for (int x = 0; x < number && x < all.size(); x++) {
            movieRepository.deleteByImdbId(all.get(x).getImdbId());
        }
        return ResponseEntity.ok().build();
    }

    private final List<String> omdbFieldsToRemove = Arrays.asList("Error", "Response", "countries", "Season", "Episode", "tomatoMeter", "tomatoImage", "tomatoRating", "tomatoReviews", "tomatoFresh", "tomatoRotten", "tomatoConsensus", "tomatoUserMeter", "tomatoUserRating", "tomatoUserReviews", "tomatoURL");

    private void removeOMDbFields(final Map map) {
        omdbFieldsToRemove.forEach(field -> map.remove(field));
    }

    private final List<String> myCollectionFieldsToRemove = Arrays.asList("title", "imdbId", "Id");

    private void removeMyCollectionFields(final Map map) {
        myCollectionFieldsToRemove.forEach(field -> map.remove(field));
    }

    private Map joinInfos(final OmdbVideoFull info, final Movie myCollectionInfo) {
        if (info == null) return null;
        final Map infoMap = objectMapper.convertValue(info, Map.class);
        removeOMDbFields(infoMap);
        if (myCollectionInfo != null && myCollectionInfo.getImdbId() != null) {
            final Map myCollectionInfoMap = objectMapper.convertValue(myCollectionInfo, Map.class);
            removeMyCollectionFields(myCollectionInfoMap);
            infoMap.put("myCollectionInfo", myCollectionInfoMap);
        }
        return infoMap;
    }
}
