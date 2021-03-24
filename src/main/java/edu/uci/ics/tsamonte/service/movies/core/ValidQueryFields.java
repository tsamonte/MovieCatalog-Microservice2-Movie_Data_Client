package edu.uci.ics.tsamonte.service.movies.core;

public class ValidQueryFields {
    public static boolean isValidLimit(int limit){
        if(limit == 10 || limit == 25 || limit == 50 || limit == 100) return true;
        return false;
    }

    public static boolean isValidOffset(int limit, int offset) {
        if (offset % limit == 0) return true;
        return false;
    }

    public static boolean isValidOrderBy(String orderBy) {
        if(orderBy.equals("title") || orderBy.equals("rating") || orderBy.equals("year")) return true;
        return false;
    }

    public static boolean isValidDirection(String direction) {
        if(direction.equalsIgnoreCase("asc") || direction.equalsIgnoreCase("desc")) return true;
        return false;
    }

    public static boolean isValidPeopleOrderBy(String orderBy) {
        if(orderBy.equals("name") || orderBy.equals("birthday") || orderBy.equals("popularity")) return true;
        return false;
    }
}
