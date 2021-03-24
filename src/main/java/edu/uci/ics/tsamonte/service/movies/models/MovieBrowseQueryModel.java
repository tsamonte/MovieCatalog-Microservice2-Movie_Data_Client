package edu.uci.ics.tsamonte.service.movies.models;

import java.util.ArrayList;

public class MovieBrowseQueryModel {
    private ArrayList<String> keywords;
    private Integer limit = 10;
    private Integer offset = 0;
    private String orderby = "title";
    private String direction = "asc";
    private Boolean hidden;

    public MovieBrowseQueryModel(ArrayList<String> keywords, Integer limit, Integer offset,
                                 String orderby, String direction, Boolean hidden) {
        this.keywords = keywords;
        if(limit != null) { this.limit = limit; }
        if(offset != null) { this.offset = offset; }
        if(orderby != null) { this.orderby = orderby; }
        if(direction != null) { this.direction = direction; }
        this.hidden = hidden;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public String getOrderby() {
        return orderby;
    }

    public String getDirection() {
        return direction;
    }

    public Boolean showHidden() {
        return hidden;
    }
}
