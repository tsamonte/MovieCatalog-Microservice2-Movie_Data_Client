package edu.uci.ics.tsamonte.service.movies.core;

import edu.uci.ics.tsamonte.service.movies.MoviesService;
import edu.uci.ics.tsamonte.service.movies.logger.ServiceLogger;
import edu.uci.ics.tsamonte.service.movies.models.MovieBrowseQueryModel;
import edu.uci.ics.tsamonte.service.movies.models.MovieSearchQueryModel;
import edu.uci.ics.tsamonte.service.movies.models.MovieModel;
import edu.uci.ics.tsamonte.service.movies.models.get.GenreModel;
import edu.uci.ics.tsamonte.service.movies.models.get.GetPageMovieModel;
import edu.uci.ics.tsamonte.service.movies.models.get.PersonModel;
import edu.uci.ics.tsamonte.service.movies.models.people.PeopleQueryModel;
import edu.uci.ics.tsamonte.service.movies.models.people.get.PersonModelGet;
import edu.uci.ics.tsamonte.service.movies.models.people.search.PeopleSearchQueryModel;
import edu.uci.ics.tsamonte.service.movies.models.people.search.PersonModelSearch;
import edu.uci.ics.tsamonte.service.movies.models.thumbnail.ThumbnailModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MovieRecords {
    // ========================SEARCH========================
    private static String buildSearch(MovieSearchQueryModel queryModel) {
        String SELECT = "SELECT DISTINCT m.movie_id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden";
        String FROM = " FROM movie AS m INNER JOIN person AS p ON m.director_id = p.person_id" +
                " INNER JOIN genre_in_movie AS gim ON m.movie_id = gim.movie_id" +
                " INNER JOIN genre AS g ON g.genre_id = gim.genre_id";
        String WHERE = " WHERE 1=1";
        String ORDERBY = " ORDER BY " + queryModel.getOrderby() + " " + queryModel.getDirection();
        String LIMIT = " LIMIT " + queryModel.getLimit() + " OFFSET " + queryModel.getOffset();

        if(queryModel.getTitle() != null) {
            WHERE += " AND m.title LIKE ?";
        }
        if(queryModel.getYear() != null) {
            WHERE += " AND m.year = ?";
        }
        if(queryModel.getDirector() != null) {
            WHERE += " AND p.name LIKE ?";
        }
        if(queryModel.getGenre() != null) {
            WHERE += " AND g.name LIKE ?";
        }

        // only show movies if user.plevel <= 4
        // if do not show hidden, only show rows where m.hidden = false
        if(queryModel.showHidden() == null || !queryModel.showHidden()) {
            WHERE += " AND hidden = FALSE";
        }

        if(queryModel.getOrderby().equals("title")) {
            ORDERBY += ", rating DESC";
        }
        else if (queryModel.getOrderby().equals("rating")) {
            ORDERBY += ", title ASC";
        }
        else if (queryModel.getOrderby().equals("year")) {
            ORDERBY += ", rating DESC";
        }

        return SELECT + FROM + WHERE + ORDERBY + LIMIT;
    }

    public static MovieModel[] retrieveSearch(MovieSearchQueryModel queryModel) {
        try {
            ArrayList<MovieModel> rows = new ArrayList<MovieModel>();

            String query = buildSearch(queryModel);
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);

            int paramIndex = 1;
            if(queryModel.getTitle() != null) {
                ps.setString(paramIndex, "%" + queryModel.getTitle() + "%");
                paramIndex++;
            }
            if(queryModel.getYear() != null) {
                ps.setInt(paramIndex, queryModel.getYear());
                paramIndex++;
            }
            if(queryModel.getDirector() != null) {
                ps.setString(paramIndex, "%" + queryModel.getDirector() + "%");
                paramIndex++;
            }
            if(queryModel.getGenre() != null) {
                ps.setString(paramIndex, "%" + queryModel.getGenre() + "%");
            }

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
            while(rs.next()) {
                String movie_id = rs.getString("movie_id");
                String title = rs.getString("title");
                Integer year = rs.getInt("year");
                String director = rs.getString("name");
                float rating = rs.getFloat("rating");
                String backdrop_path = rs.getString("backdrop_path");
                String poster_path = rs.getString("poster_path");
                Boolean hidden = rs.getBoolean("hidden");
                if(queryModel.showHidden() == null) { // if user is invalid
                    hidden = null;
                }
                MovieModel toAdd = new MovieModel(movie_id, title, year, director,
                        rating, backdrop_path, poster_path, hidden);
                rows.add(toAdd);
            }
            if(rows.size() == 0) {
                return null;
            }

            MovieModel[] mArray = new MovieModel[rows.size()];
            mArray = rows.toArray(mArray);
            return mArray;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve movie records.");
            e.printStackTrace();
            return null;
        }
    }

    // ========================BROWSE========================
    private static String buildBrowse(MovieBrowseQueryModel queryModel) {
        String SELECT = "SELECT DISTINCT m.movie_id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden";;
        String FROM = " FROM movie AS m INNER JOIN person AS p ON m.director_id = p.person_id";
        String WHERE = " WHERE 1=1";
        String ORDERBY = " ORDER BY " + queryModel.getOrderby() + " " + queryModel.getDirection();
        String LIMIT = " LIMIT " + queryModel.getLimit() + " OFFSET " + queryModel.getOffset();

        ArrayList<String> keywords = queryModel.getKeywords();
        if(keywords != null || !keywords.isEmpty()) {
            for(int i = 0; i < keywords.size(); i++) {
                FROM += String.format(" INNER JOIN keyword_in_movie AS kim%d ON m.movie_id = kim%d.movie_id", i, i);
                FROM += String.format(" INNER JOIN keyword AS k%d ON k%d.keyword_id = kim%d.keyword_id", i, i, i);

                WHERE += String.format(" AND k%d.name = ?", i);
            }
        }

        // only show movies if user.plevel <= 4
        // if do not show hidden, only show rows where m.hidden = false
        if(queryModel.showHidden() == null || !queryModel.showHidden()) {
            WHERE += " AND hidden = FALSE";
        }

        if(queryModel.getOrderby().equals("title")) {
            ORDERBY += ", rating DESC";
        }
        else if (queryModel.getOrderby().equals("rating")) {
            ORDERBY += ", title ASC";
        }
        else if (queryModel.getOrderby().equals("year")) {
            ORDERBY += ", rating DESC";
        }

        return SELECT + FROM + WHERE + ORDERBY + LIMIT;
    }

    public static MovieModel[] retrieveBrowse(MovieBrowseQueryModel queryModel) {
        try {
            ArrayList<MovieModel> rows = new ArrayList<MovieModel>();

            String query = buildBrowse(queryModel);
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);

            ArrayList<String> keywords = queryModel.getKeywords();
            for(int i = 0; i < keywords.size(); i++) {
                ps.setString(i+1, keywords.get(i));
            }

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            while(rs.next()) {
                String movie_id = rs.getString("movie_id");
                String title = rs.getString("title");
                Integer year = rs.getInt("year");
                String director = rs.getString("name");
                float rating = rs.getFloat("rating");
                String backdrop_path = rs.getString("backdrop_path");
                String poster_path = rs.getString("poster_path");
                Boolean hidden = rs.getBoolean("hidden");
                if(queryModel.showHidden() == null) {
                    hidden = null;
                }
                MovieModel toAdd = new MovieModel(movie_id, title, year, director,
                        rating, backdrop_path, poster_path, hidden);
                rows.add(toAdd);
            }
            if(rows.size() == 0) {
                return null;
            }

            MovieModel[] mArray = new MovieModel[rows.size()];
            mArray = rows.toArray(mArray);
            return mArray;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve movie records.");
            e.printStackTrace();
            return null;
        }
    }

    // ========================GET========================
    private static GenreModel[] retrieveGenres(String movie_id) {
        try {
            ArrayList<GenreModel> genres = new ArrayList<GenreModel>();

            String query = "SELECT g.genre_id, g.name" +
                    " FROM movie as m INNER JOIN genre_in_movie as gim ON m.movie_id = gim.movie_id" +
                    " INNER JOIN genre as g ON gim.genre_id = g.genre_id" +
                    " WHERE m.movie_id = ?";

            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, movie_id);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            while(rs.next()) {
                int id = rs.getInt("genre_id");
                String name = rs.getString("name");
                genres.add(new GenreModel(id, name));
            }

            GenreModel[] gArray = new GenreModel[genres.size()];
            gArray = genres.toArray(gArray);
            return gArray;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve genres.");
            e.printStackTrace();
            return null;
        }
    }

    private static PersonModel[] retrievePerson(String movie_id) {
        try {
            ArrayList<PersonModel> people = new ArrayList<PersonModel>();

            String query = "SELECT p.person_id, p.name" +
                    " FROM movie AS m INNER JOIN person_in_movie AS pim ON m.movie_id = pim.movie_id" +
                    " INNER JOIN person AS p ON pim.person_id = p.person_id" +
                    " WHERE m.movie_id = ?";

            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, movie_id);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            while(rs.next()) {
                int person_id = rs.getInt("person_id");
                String name = rs.getString("name");
                people.add(new PersonModel(person_id, name));
            }

            PersonModel[] pArray = new PersonModel[people.size()];
            pArray = people.toArray(pArray);
            return pArray;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve people.");
            e.printStackTrace();
            return null;
        }
    }

    public static GetPageMovieModel retrieveMovie(String movie_id, Boolean hidden) {
        try {
            String query = "SELECT m.movie_id, m.title, m.year, d.name, m.rating, m.num_votes, m.budget," +
                    " m.revenue, m.overview, m.backdrop_path, m.poster_path, m.hidden" +
                    " FROM movie AS m INNER JOIN person as d ON m.director_id = d.person_id" +
                    " WHERE m.movie_id = ?";

            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, movie_id);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            if(rs.next()) {
                String retMovieId = rs.getString("movie_id");
                String title = rs.getString("title");
                int year = rs.getInt("year");
                String director = rs.getString("name");
                float rating = rs.getFloat("rating");
                int num_votes = rs.getInt("num_votes");
                String budget = rs.getString("budget");
                String revenue = rs.getString("revenue");
                String overview = rs.getString("overview");
                String backdrop_path = rs.getString("backdrop_path");
                String poster_path = rs.getString("poster_path");
                Boolean retrievedHidden = rs.getBoolean("hidden");
                if(hidden == null) {
                    retrievedHidden = null;
                }
                GenreModel[] genres = retrieveGenres(movie_id);
                PersonModel[] people = retrievePerson(movie_id);

                return new GetPageMovieModel(retMovieId, title, year, director, rating, num_votes, budget,
                        revenue, overview, backdrop_path, poster_path, retrievedHidden, genres, people);
            }
            else {
                return null;
            }
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve genres.");
            e.printStackTrace();
            return null;
        }
    }

    // ========================THUMBNAIL========================
    private static String buildThumbnailQueries(String[] movie_ids) {
        String SELECT = "SELECT movie_id, title, backdrop_path, poster_path";
        String FROM = " FROM movie";
        String WHERE = " WHERE";
        if(movie_ids.length == 0) {
            WHERE += " 1=1 ";
        }
        else {
            for(int i = 0; i < movie_ids.length; i++) {
                if(i != 0) {
                    WHERE += " OR movie_id = ?";
                }
                else {
                    WHERE += " movie_id = ?";
                }
            }
        }

        return SELECT + FROM + WHERE;
    }

    public static ThumbnailModel[] retrieveThumbnails(String[] movie_ids) {
        try {
            ArrayList<ThumbnailModel> results = new ArrayList<ThumbnailModel>();

            String query = buildThumbnailQueries(movie_ids);

            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);

            for(int i = 0; i < movie_ids.length; i++) {
                ps.setString(i+1, movie_ids[i]);
            }

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            while(rs.next()) {
                String movie_id = rs.getString("movie_id");
                String title = rs.getString("title");
                String backdrop_path = rs.getString("backdrop_path");
                String poster_path = rs.getString("poster_path");

                results.add(new ThumbnailModel(movie_id, title, backdrop_path, poster_path));
            }

            ThumbnailModel[] tArray = new ThumbnailModel[results.size()];
            tArray = results.toArray(tArray);
            return tArray;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve thumbnails.");
            e.printStackTrace();
            return null;
        }
    }

    // ========================PEOPLE========================
    public static boolean personExists (String name) {
        try {
            String query = "SELECT *" +
                    " FROM person" +
                    " WHERE name LIKE ?";

            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, name);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            if(rs.next()) {
                return true;
            }
            return false;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve people.");
            e.printStackTrace();
            return false;
        }
    }

    private static String buildPeopleQuery (PeopleQueryModel queryModel, Boolean hidden) {
        String SELECT = "SELECT m.movie_id, m.title, m.year, d.name, m.rating, m.backdrop_path, m.poster_path, m.hidden";
        String FROM = " FROM movie AS m INNER JOIN person AS d ON m.director_id = d.person_id" +
                " INNER JOIN person_in_movie AS pim ON m.movie_id = pim.movie_id" +
                " INNER JOIN person AS p ON pim.person_id = p.person_id";
        String WHERE = " WHERE 1=1";
        String ORDERBY = " ORDER BY " + queryModel.getOrderby() + " " + queryModel.getDirection();
        String LIMIT = " LIMIT " + queryModel.getLimit() + " OFFSET " + queryModel.getOffset();

        WHERE += " AND p.name LIKE ?";

        // only show movies if user.plevel <= 4
        // if do not show hidden, only show rows where m.hidden = false
        if(hidden == null || !hidden) {
            WHERE += " AND hidden = FALSE";
        }

        return SELECT + FROM + WHERE + ORDERBY + LIMIT;
    }

    public static MovieModel[] retrievePeopleResults(PeopleQueryModel queryModel, Boolean hidden) {
        try {
            ArrayList<MovieModel> results = new ArrayList<MovieModel>();
            String query = buildPeopleQuery(queryModel, hidden);

            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, "%" + queryModel.getName() + "%");

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            while (rs.next()) {
                String movie_id = rs.getString("movie_id");
                String title = rs.getString("title");
                int year = rs.getInt("year");
                String director = rs.getString("name");
                float rating = rs.getFloat("rating");
                String backdrop_path = rs.getString("backdrop_path");
                String poster_path = rs.getString("poster_path");
                Boolean retrievedHidden = rs.getBoolean("hidden");
                if(hidden == null) {
                    retrievedHidden = null;
                }
                results.add(new MovieModel(movie_id, title, year, director, rating, backdrop_path, poster_path, retrievedHidden));
            }

            MovieModel[] pArray = new MovieModel[results.size()];
            pArray = results.toArray(pArray);
            return pArray;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve people.");
            e.printStackTrace();
            return null;
        }
    }

    // ========================PEOPLE/SEARCH========================
    private static String buildPeopleSearchQuery(PeopleSearchQueryModel queryModel) {
        String SELECT = "SELECT DISTINCT p.person_id, p.name, p.birthday, p.popularity, p.profile_path";
        String FROM = " FROM person AS p INNER JOIN person_in_movie AS pim ON p.person_id = pim.person_id" +
                " INNER JOIN movie AS m ON pim.movie_id = m.movie_id";
        String WHERE = " WHERE 1=1";
        String ORDERBY = " ORDER BY " + queryModel.getOrderby() + " " + queryModel.getDirection();
        String LIMIT = " LIMIT " + queryModel.getLimit() + " OFFSET " + queryModel.getOffset();

        if(queryModel.getName() != null) {
            WHERE += " AND p.name LIKE ?";
        }
        if(queryModel.getBirthday() != null) {
            WHERE += " AND p.birthday = ?";
        }
        if(queryModel.getMovie_title() != null) {
            WHERE += " AND m.title LIKE ?";
        }

        if(queryModel.getOrderby().equals("name")) {
            ORDERBY += ", popularity DESC";
        }
        else if (queryModel.getOrderby().equals("birthday")) {
            ORDERBY += ", popularity DESC";
        }
        else if (queryModel.getOrderby().equals("popularity")) {
            ORDERBY += ", name ASC";
        }

        return SELECT + FROM + WHERE + ORDERBY + LIMIT;
    }

    public static PersonModelSearch[] retrievePeopleSearchResults(PeopleSearchQueryModel queryModel) {
        try {
            ArrayList<PersonModelSearch> results = new ArrayList<PersonModelSearch>();

            String query = buildPeopleSearchQuery(queryModel);

            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);

            int paramIndex = 1;
            if(queryModel.getName() != null) {
                ps.setString(paramIndex++, "%" + queryModel.getName() + "%");
            }
            if(queryModel.getBirthday() != null) {
                ps.setString(paramIndex++, queryModel.getBirthday());
            }
            if(queryModel.getMovie_title() != null) {
                ps.setString(paramIndex++, "%" + queryModel.getMovie_title() + "%");
            }

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            while(rs.next()) {
                Integer person_id = rs.getInt("person_id");
                String name = rs.getString("name");
                String birthday = rs.getString("birthday");
                Float popularity = rs.getFloat("popularity");
                String profile_path = rs.getString("profile_path");

                results.add(new PersonModelSearch(person_id, name, birthday, popularity, profile_path));
            }

            if(results.isEmpty()) return null;

            PersonModelSearch[] pArray = new PersonModelSearch[results.size()];
            pArray = results.toArray(pArray);
            return pArray;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve people.");
            e.printStackTrace();
            return null;
        }
    }

    // ========================PEOPLE/GET========================
    public static PersonModelGet retrievePeopleGetResults(int person_id) {
        try {
            String query = "SELECT person_id, name, g.gender_name, birthday, deathday, biography, birthplace, popularity, profile_path" +
                    " FROM person AS p INNER JOIN gender AS g ON p.gender_id = g.gender_id" +
                    " WHERE person_id = ?";

            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setInt(1, person_id);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            if(rs.next()) {
                int retrieved_id = rs.getInt("person_id");
                String name = rs.getString("name");
                String gender = rs.getString("gender_name");
                String birthday = rs.getString("birthday");
                String deathday = rs.getString("deathday");
                String biography = rs.getString("biography");
                String birthplace = rs.getString("birthplace");
                Float popularity = rs.getFloat("popularity");
                String profile_path = rs.getString("profile_path");

                return new PersonModelGet(retrieved_id, name, gender, birthday, deathday, biography,
                        birthplace, popularity, profile_path);
            }
            else {
                return null;
            }
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve people.");
            e.printStackTrace();
            return null;
        }
    }
}
