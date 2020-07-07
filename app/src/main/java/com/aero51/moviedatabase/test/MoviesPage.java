package com.aero51.moviedatabase.test;

import androidx.room.Ignore;

import com.aero51.moviedatabase.repository.model.tmdb.movie.TopRatedMovie;

import java.util.List;

public class MoviesPage {

    private Integer page;
    private Integer total_results;
    private Integer total_pages;
    private List<Movie> results;



    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotal_results() {
        return total_results;
    }

    public void setTotal_results(Integer total_results) {
        this.total_results = total_results;
    }

    public Integer getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(Integer total_pages) {
        this.total_pages = total_pages;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
