package edu.uci.ics.tsamonte.service.movies.models.people.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.tsamonte.service.movies.base.Result;
import edu.uci.ics.tsamonte.service.movies.models.ResponseModel;

public class PersonSearchResponseModel extends ResponseModel {
    @JsonProperty(value = "people", required = true)
    private PersonModelSearch[] people;

    @JsonCreator
    public PersonSearchResponseModel(Result result,
                                     @JsonProperty(value = "people", required = true) PersonModelSearch[] people) {
        super(result);
        this.people = people;
    }

    @JsonProperty(value = "people")
    public PersonModelSearch[] getPeople() {
        return people;
    }
}
