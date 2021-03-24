package edu.uci.ics.tsamonte.service.movies.models.get;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.tsamonte.service.movies.base.Result;
import edu.uci.ics.tsamonte.service.movies.models.ResponseModel;

public class GetResponseModel extends ResponseModel {
    @JsonProperty(value = "movie", required = true)
    private GetPageMovieModel movie;

    @JsonCreator
    public GetResponseModel(Result result,
                            @JsonProperty(value = "movie", required = true) GetPageMovieModel movie) {
        super(result);
        this.movie = movie;
    }

    @JsonProperty(value = "movie")
    public GetPageMovieModel getMovie() {
        return movie;
    }
}
