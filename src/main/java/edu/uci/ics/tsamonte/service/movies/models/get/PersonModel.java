package edu.uci.ics.tsamonte.service.movies.models.get;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonModel {
    @JsonProperty(value = "person_id", required = true)
    private int person_id;

    @JsonProperty(value = "name", required = true)
    private String name;

    public PersonModel() {}

    @JsonCreator
    public PersonModel(@JsonProperty(value = "person_id", required = true) int person_id,
                       @JsonProperty(value = "name", required = true) String name) {
        this.person_id = person_id;
        this.name = name;
    }

    @JsonProperty(value = "person_id")
    public int getPerson_id() {
        return person_id;
    }

    @JsonProperty(value = "name")
    public String getName() {
        return name;
    }
}
