package edu.uci.ics.tsamonte.service.movies.models.people.get;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.tsamonte.service.movies.base.Result;
import edu.uci.ics.tsamonte.service.movies.models.ResponseModel;

public class PersonGetResponseModel extends ResponseModel {
    @JsonProperty(value = "person", required = true)
    private PersonModelGet person;

    @JsonCreator
    public PersonGetResponseModel(Result result,
                                  @JsonProperty(value = "person", required = true) PersonModelGet person) {
        super(result);
        this.person = person;
    }

    @JsonProperty(value = "person")
    public PersonModelGet getPerson() {
        return person;
    }
}
