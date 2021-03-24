package edu.uci.ics.tsamonte.service.movies.models.people.search;

import edu.uci.ics.tsamonte.service.movies.models.people.PeopleQueryModel;

public class PeopleSearchQueryModel extends PeopleQueryModel {
    private String birthday;
    private String movie_title;

    public PeopleSearchQueryModel(String name, String birthday, String movie_title,
                                  Integer limit, Integer offset, String orderby, String direction) {
        super(name, limit, offset, orderby==null?"name":orderby, direction);
        this.birthday = birthday;
        this.movie_title = movie_title;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getMovie_title() {
        return movie_title;
    }
}
