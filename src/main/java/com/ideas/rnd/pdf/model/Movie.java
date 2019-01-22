package com.ideas.rnd.pdf.model;

import java.util.Objects;

public class Movie {
    private String year;
    private String movieTitle;
    private String originalTitle;
    private String duration;
    private String actor;
    private String region;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(year, movie.year) &&
                Objects.equals(movieTitle, movie.movieTitle) &&
                Objects.equals(originalTitle, movie.originalTitle) &&
                Objects.equals(duration, movie.duration) &&
                Objects.equals(actor, movie.actor) &&
                Objects.equals(region, movie.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, movieTitle, originalTitle, duration, actor, region);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Movie{");
        sb.append("year='").append(year).append('\'');
        sb.append(", movieTitle='").append(movieTitle).append('\'');
        sb.append(", originalTitle='").append(originalTitle).append('\'');
        sb.append(", duration='").append(duration).append('\'');
        sb.append(", actor='").append(actor).append('\'');
        sb.append(", region='").append(region).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

}
