package edu.uci.ics.tsamonte.service.movies.models.get;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.tsamonte.service.movies.models.MovieModel;

public class GetPageMovieModel extends MovieModel {
    // add: numVotes, budget, revenue, overview, genres[], people[]
    @JsonProperty(value = "num_votes", required = true)
    private int num_votes;

    @JsonProperty(value = "budget")
    private String budget;

    @JsonProperty(value = "revenue")
    private String revenue;

    @JsonProperty(value = "overview")
    private String overview;

    @JsonProperty(value = "genres", required = true)
    private GenreModel[] genres;

    @JsonProperty(value = "people", required = true)
    private PersonModel[] people;

    @JsonCreator
    public GetPageMovieModel(@JsonProperty(value = "movie_id", required = true) String movie_id,
                             @JsonProperty(value = "title", required = true) String title,
                             @JsonProperty(value = "year", required = true) int year,
                             @JsonProperty(value = "director", required = true) String director,
                             @JsonProperty(value = "rating", required = true) float rating,
                             @JsonProperty(value = "num_votes", required = true) int num_votes,
                             @JsonProperty(value = "budget") String budget,
                             @JsonProperty(value = "revenue") String revenue,
                             @JsonProperty(value = "overview") String overview,
                             @JsonProperty(value = "backdrop_path") String backdrop_path,
                             @JsonProperty(value = "poster_path") String poster_path,
                             @JsonProperty(value = "hidden") Boolean hidden,
                             @JsonProperty(value = "genres", required = true) GenreModel[] genres,
                             @JsonProperty(value = "people", required = true)PersonModel[] people) {
        super(movie_id, title, year, director, rating, backdrop_path, poster_path, hidden);
        this.num_votes = num_votes;
        this.budget = budget;
        this.revenue = revenue;
        this.overview = overview;
        this.genres = genres;
        this.people = people;
    }
}
