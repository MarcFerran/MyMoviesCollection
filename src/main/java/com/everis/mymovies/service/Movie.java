package com.everis.mymovies.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Movie")
@JsonDeserialize(as = Movie.class)
//@Builder
public class Movie {

    @Id
    private String imdbId;
    @Transient
    private String title;
    private Integer timesWatched;
    private String resolution;
    private String path;
    private String language;
    private Date lastTimeWached;
    private Boolean pendingToWatch;
    private Integer sizeInMb;

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTimesWatched() {
        return timesWatched;
    }

    public void setTimesWatched(Integer timesWatched) {
        this.timesWatched = timesWatched;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getSizeInMb() {
        return sizeInMb;
    }

    public void setSizeInMb(Integer sizeInMb) {
        this.sizeInMb = sizeInMb;
    }

    public Date getLastTimeWached() {
        return lastTimeWached;
    }

    public void setLastTimeWached(Date lastTimeWached) {
        this.lastTimeWached = lastTimeWached;
    }

    public Boolean getPendingToWatch() {
        return pendingToWatch;
    }

    public void setPendingToWatch(Boolean pendingToWatch) {
        this.pendingToWatch = pendingToWatch;
    }
}
