package edu.uci.ics.tsamonte.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.tsamonte.service.movies.base.Result;

public class MovieResultsResponseModel extends ResponseModel{
    @JsonProperty(value = "movies", required = true)
    MovieModel[] movies;

    @JsonCreator
    public MovieResultsResponseModel(Result result,
                                     @JsonProperty(value = "movies", required = true) MovieModel[] movies) {
        super(result);
        this.movies = movies;
    }

    @JsonProperty(value = "movies")
    public MovieModel[] getMovies() {
        return movies;
    }
}
