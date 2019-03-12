package com.everis.mymovies.test;

import com.everis.mymovies.service.Movie;
import lombok.SneakyThrows;
import org.abelsromero.parallels.jobs.ExecutionDetails;
import org.abelsromero.parallels.jobs.ParallelExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import static java.lang.String.format;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class MainParallels {

    private static final Integer THREAD_COUNT = 5;
    private static final Integer EXECUTIONS_COUNT = 100;
    private static final String MAIN_URL = "http://localhost:8080/movies/";
    private static final String FILE_PATH = "C:\\LoadTesting\\ids.txt";
    private RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        new MainParallels().runAddGetDeleteMovie();
//        new MainParallels().runGetAllMovies();
    }

    private ExecutionDetails runAddGetDeleteMovie() {
        final File file = new File(FILE_PATH);
        final ParallelExecutor executor = new ParallelExecutor(THREAD_COUNT, EXECUTIONS_COUNT);
        final ExecutionDetails results = executor.run(() -> {
            try {
                final HttpHeaders headers = getHeaders();
                HttpEntity<Movie> httpEntity = new HttpEntity<>(createMovie(chooseRandomId(file)), headers);
                final ResponseEntity<String> responseEntityAdd = restTemplate.exchange(MAIN_URL, POST, httpEntity, String.class);
                if (responseEntityAdd.getStatusCode() != CREATED) return false;
                httpEntity = new HttpEntity<>(headers);
                final ResponseEntity<Map> responseEntityGet = restTemplate.exchange(MAIN_URL.concat("{id}"), GET, httpEntity, Map.class, responseEntityAdd.getBody());
                if (responseEntityGet.getStatusCode() != OK) return false;
                final ResponseEntity<Void> responseEntityDelete = restTemplate.exchange(MAIN_URL.concat("{id}"), DELETE, httpEntity, Void.class, responseEntityAdd.getBody());
                if (responseEntityDelete.getStatusCode() != OK) return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        });
        return printExecutionDetails(results);
    }

    private ExecutionDetails runGetAllMovies() {
        final HttpEntity<Movie> httpEntity = new HttpEntity<>(getHeaders());
        final ParallelExecutor executor = new ParallelExecutor(10, 100);
        final ExecutionDetails results = executor.run(() -> {
            try {
                final ResponseEntity<Movie[]> responseEntity = restTemplate.exchange(MAIN_URL, GET, httpEntity, Movie[].class);
                if (responseEntity.getStatusCode() != OK) return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        });
        return printExecutionDetails(results);
    }

    private ExecutionDetails printExecutionDetails(final ExecutionDetails executionDetails) {
        System.out.println("Execution time (ms):\t" + executionDetails.getTime());
        System.out.println("Ratio (ops/sec):\t" + executionDetails.getJobsPerSecond());
        System.out.println(format("Successful ops: %s", executionDetails.getSuccessfulJobs().getCount()));
        System.out.println(format("Successful Avg. time (millis): %s", executionDetails.getSuccessfulJobs().getAvgTime()));
        System.out.println(format("Successful Min. time (millis): %s", executionDetails.getSuccessfulJobs().getMinTime()));
        System.out.println(format("Successful Max. time (millis): %s", executionDetails.getSuccessfulJobs().getMaxTime()));
        System.out.println(format("Failed ops: %s", executionDetails.getFailedJobs().getCount()));
        System.out.println(format("Failed Avg. time (millis): %s", executionDetails.getFailedJobs().getAvgTime()));
        return executionDetails;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private Movie createMovie(final String imdbId) {
        final Movie movie = new Movie();
        movie.setImdbId(imdbId);
        movie.setLanguage("English");
        movie.setPath("C:/myMovies/");
        movie.setResolution("1080p");
        movie.setSizeInMb(1500);
        movie.setTimesWatched(10);
        movie.setLastTimeWached(new Date());
        movie.setPendingToWatch(false);
        return movie;
    }

    @SneakyThrows
    private String chooseRandomId(final File file) {
        String result = null;
        Random rand = new Random();
        int n = 0;
        for (Scanner sc = new Scanner(file); sc.hasNext(); ) {
            ++n;
            String line = sc.nextLine();
            if (rand.nextInt(n) == 0) result = line;
        }
        return result;
    }

}
