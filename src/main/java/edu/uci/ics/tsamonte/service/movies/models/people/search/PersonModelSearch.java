package edu.uci.ics.tsamonte.service.movies.models.people.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.tsamonte.service.movies.models.get.PersonModel;

public class PersonModelSearch extends PersonModel {
    @JsonProperty(value = "birthday")
    private String birthday;

    @JsonProperty(value = "popularity")
    private Float popularity;

    @JsonProperty(value = "profile_path")
    private String profile_path;

    public PersonModelSearch() {}
    @JsonCreator
    public PersonModelSearch(@JsonProperty(value = "person_id", required = true) int person_id,
                             @JsonProperty(value = "name", required = true) String name,
                             @JsonProperty(value = "birthday") String birthday,
                             @JsonProperty(value = "popularity") Float popularity,
                             @JsonProperty(value = "profile_path") String profile_path) {
        super(person_id, name);
        this.birthday = birthday;
        this.popularity = popularity;
        this.profile_path = profile_path;
    }

    @JsonProperty(value = "birthday")
    public String getBirthday() {
        return birthday;
    }

    @JsonProperty(value = "popularity")
    public Float getPopularity() {
        return popularity;
    }
    @JsonProperty(value = "profile_path")
    public String getProfile_path() {
        return profile_path;
    }
}
