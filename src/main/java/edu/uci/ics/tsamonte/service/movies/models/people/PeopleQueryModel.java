package edu.uci.ics.tsamonte.service.movies.models.people;

public class PeopleQueryModel {
    private String name;
    private Integer limit = 10;
    private Integer offset = 0;
    private String orderby = "title";
    private String direction = "asc";

    public PeopleQueryModel() {}

    public PeopleQueryModel(String name, Integer limit, Integer offset, String orderby, String direction) {
        this.name = name;
        if(limit != null) { this.limit = limit; }
        if(offset != null) { this.offset = offset; }
        if(orderby != null) { this.orderby = orderby; }
        if(direction != null) { this.direction = direction; }
    }

    public String getName() {
        return name;
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
}
