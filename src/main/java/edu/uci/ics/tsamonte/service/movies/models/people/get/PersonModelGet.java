package edu.uci.ics.tsamonte.service.movies.models.people.get;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.tsamonte.service.movies.models.people.search.PersonModelSearch;

public class PersonModelGet extends PersonModelSearch {
    @JsonProperty(value = "gender")
    private String gender;

    @JsonProperty(value = "deathday")
    private String deathday;

    @JsonProperty(value = "biography")
    private String biography;

    @JsonProperty(value = "birthplace")
    private String birthplace;

    @JsonCreator
    public PersonModelGet(@JsonProperty(value = "person_id", required = true) int person_id,
                          @JsonProperty(value = "name", required = true) String name,
                          @JsonProperty(value = "gender") String gender,
                          @JsonProperty(value = "birthday") String birthday,
                          @JsonProperty(value = "deathday") String deathday,
                          @JsonProperty(value = "biography") String biography,
                          @JsonProperty(value = "birthplace") String birthplace,
                          @JsonProperty(value = "popularity") Float popularity,
                          @JsonProperty(value = "profile_path") String profile_path) {
        super(person_id, name, birthday, popularity, profile_path);
        this.gender = gender;
        this.deathday = deathday;
        this.biography = biography;
        this.birthplace = birthplace;
    }

    @JsonProperty(value = "gender")
    public String getGender() {
        return gender;
    }

    @JsonProperty(value = "deathday")
    public String getDeathday() {
        return deathday;
    }

    @JsonProperty(value = "biography")
    public String getBiography() {
        return biography;
    }

    @JsonProperty(value = "birthplace")
    public String getBirthplace() {
        return birthplace;
    }
}
