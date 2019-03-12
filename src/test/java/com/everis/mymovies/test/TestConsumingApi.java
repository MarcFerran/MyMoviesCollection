package com.everis.mymovies.test;

import com.everis.mymovies.service.Movie;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class TestConsumingApi {

    public static void main(String[] args) {

//        new TestConsumingApi().getMovies();
        //new TestConsumingApi().getMoviesById();
//        new TestConsumingApi().addFilm2();
//        new TestConsumingApi().getTitleInitialMovies();
//        new TestConsumingApi().loadIDlist();
        new TestConsumingApi().deleteManyMovies(500);
//        new TestConsumingApi().addInitialMovies();
//        new TestConsumingApi().addMovies();
        //new TestConsumingApi().updateMovies("Spring MVC");
        //new TestConsumingApi().deleteMovies("Spring MVC");

    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public void addInitialMovies() {
        HttpHeaders headers = getheader();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/movies/";
        final ArrayList<Movie> movies = objectMapper.readValue(loadMovies(), objectMapper.getTypeFactory().constructCollectionType(List.class, Movie.class));
        movies.stream().forEach(movie -> {
            HttpEntity<Movie> httpEntity = new HttpEntity<>(movie, headers);
            try {
                restTemplate.postForLocation(url, httpEntity);
            } catch (Exception e) {
                System.err.println("Problem with movie: " + movie.getImdbId());
                e.printStackTrace();
            }
        });
    }

    @SneakyThrows
    public void getTitleInitialMovies() {
        HttpHeaders headers = getheader();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/movies/omdb/{id}";
        List<Movie> movieList = new ArrayList<>();
        final ArrayList<Movie> movies = objectMapper.readValue(loadMovies(), objectMapper.getTypeFactory().constructCollectionType(List.class, Movie.class));
        movies.stream().forEach(movie -> {
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
            try {
                ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class, movie.getImdbId());
                movie.setTitle((String)responseEntity.getBody().get("Title"));
                movie.setLastTimeWached(new Date());
                movieList.add(movie);
            } catch (Exception e) {
                System.err.println("Problem with movie: " + movie.getImdbId());
                e.printStackTrace();
            }
        });
        writeInFile(objectMapper.writeValueAsString(movieList), "C:\\idsResolved.json");
    }

    @SneakyThrows
    public void loadIDlist(){
        File file = new File("C:\\ids.txt");
        List<Movie> movieList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Movie movie = new Movie();
                movie.setImdbId("tt"+line.split(",")[1]);
                movie.setLanguage("English");
                movie.setPath("C:/myMovies");
                movie.setSizeInMb(1024);
                movie.setTimesWatched(0);
                movie.setResolution("480p");
                movieList.add(movie);
            }
            writeInFile(objectMapper.writeValueAsString(movieList), "C:\\ids.json");
        } catch (Exception e) {}

    }

    @SneakyThrows
    private void writeInFile(String fileTxt, String filePath) {
        File newFile = new File(filePath);
        if (newFile.createNewFile())
        {
            System.out.println("File is created!");
        } else {
            System.out.println("File already exists.");
        }
        FileWriter writer = new FileWriter(newFile);
        writer.write(fileTxt);
        writer.close();
    }

    private String moviesTestLoad = "https://raw.githubusercontent.com/MarcFerran/moviesJson/master/initialMovies.json";
    private String moviesInitialLoad = "https://raw.githubusercontent.com/MarcFerran/moviesJson/master/moviesToLoad.json";
    @SneakyThrows
    public String loadMovies() {
        final HttpURLConnection httpURLConnection =
                (HttpURLConnection) new URL(moviesInitialLoad).openConnection();
        return toString(httpURLConnection.getInputStream());
    }

    private String toString(InputStream is) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        final StringBuffer stringBuffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
        }
        return stringBuffer.toString();
    }

    public void getMovies() {
        HttpHeaders headers = getheader();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/movies";
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Movie[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Movie[].class);
        Movie[] movie = responseEntity.getBody();
        for (Object object : movie) {
            System.out.println(object);
        }
    }

    public void getMoviesById() {
        HttpHeaders headers = getheader();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/movies/{id}";
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Movie> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Movie.class, "MacbookPro");
        Movie movie = responseEntity.getBody();
        System.out.println(movie);
    }

    public void addMovies() {
        HttpHeaders headers = getheader();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/movies/";
        Movie movie = createMovie();
        HttpEntity<Movie> httpEntity = new HttpEntity<>(movie, headers);
        URI uri = restTemplate.postForLocation(url, httpEntity);
        movie = createMovie2();
        httpEntity = new HttpEntity<>(movie, headers);
        restTemplate.postForLocation(url, httpEntity);
//        restTemplate.exchange(url, HttpMethod.POST, httpEntity, Void.class);
//		System.out.println(uri.getPath());
    }

//    public void addFilm2() {
//        HttpHeaders headers = getheader();
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://localhost:8080/films/";
//        final Film film = new Film("Spring", "Boot", new Date().toString());
//        HttpEntity<Film> httpEntity = new HttpEntity<Film>(film, headers);
//        URI uri = restTemplate.postForLocation(url, httpEntity);
////		System.out.println(uri.getPath());
//    }

//
//    public void updateMovies(String movieName) {
//        HttpHeaders headers = getheader();
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://localhost:8080/movies/" + movieName;
//        final Movie movie = new Movie("Spring MVC", "Spring Security", "13/05/2019");
//        HttpEntity<Movie> httpEntity = new HttpEntity<>(movie, headers);
//        restTemplate.put(url, httpEntity);
//    }

    public void deleteMovies(String movieName) {
        HttpHeaders headers = getheader();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/movies/{id}";
        HttpEntity<Movie> httpEntity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Void.class, movieName);
    }

    public void deleteManyMovies(int numberOfMoviesToDelete) {
        HttpHeaders headers = getheader();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/movies/deleteMany/{numberOfMoviesToDelete}";
        HttpEntity<Movie> httpEntity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Void.class, numberOfMoviesToDelete);
    }



    private static HttpHeaders getheader() {
//		String credentials = "user:password";
//		String encodedCredentials = new String(Base64.getEncoder().encodeToString(credentials.getBytes()));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.add("Authorization", "Basic " + encodedCredentials);
		return headers;
    }

    private Movie createMovie() {
        final Movie movie =  new Movie();
        movie.setLanguage("English");
        movie.setPath("C:/myMovies/savingPrivateRyan.avi");
        movie.setResolution("1080p");
        movie.setSizeInMb(600);
        movie.setTitle("saving private ryan");
        movie.setTimesWatched(10);
        movie.setLastTimeWached(new Date());
        movie.setPendingToWatch(false);
        return movie;
    }

    private Movie createMovie2() {
        final Movie movie =  new Movie();
        movie.setLanguage("English");
        movie.setPath("C:/myMovies/bohemianRhapsody.avi");
        movie.setResolution("1080p");
        movie.setSizeInMb(1500);
        movie.setTitle("Bohemian Rhapsody");
        movie.setTimesWatched(10);
        movie.setLastTimeWached(new Date());
        movie.setPendingToWatch(false);
        return movie;
    }

}
