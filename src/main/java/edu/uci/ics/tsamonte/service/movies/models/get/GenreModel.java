package edu.uci.ics.tsamonte.service.movies.models.get;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenreModel {
    @JsonProperty(value = "genre_id", required = true)
    private int genre_id;

    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonCreator
    public GenreModel(@JsonProperty(value = "genre_id", required = true) int genre_id,
                      @JsonProperty(value = "name", required = true) String name) {
        this.genre_id = genre_id;
        this.name = name;
    }

    @JsonProperty(value = "genre_id")
    public int getGenre_id() {
        return genre_id;
    }

    @JsonProperty(value = "name")
    public String getName() {
        return name;
    }
}
