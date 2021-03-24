package edu.uci.ics.tsamonte.service.movies.models;

public class MovieSearchQueryModel {
    private String title;
    private Integer year;
    private String director;
    private String genre;
    private Boolean hidden;
    private Integer limit = 10;
    private Integer offset = 0;
    private String orderby = "title"; // could be "rating" or "year"
    private String direction = "asc"; // could be "desc"

    public MovieSearchQueryModel(String title, Integer year, String director, String genre, Boolean hidden,
                                 Integer limit, Integer offset, String orderby, String direction) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.genre = genre;
        this.hidden = hidden;
        if(limit != null) { this.limit = limit; }
        if(offset != null) { this.offset = offset; }
        if(orderby != null) { this.orderby = orderby; }
        if(direction != null) { this.direction = direction; }
    }

    public String getTitle() {
        return title;
    }

    public Integer getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getGenre() {
        return genre;
    }

    public Boolean showHidden() {
        return hidden;
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
