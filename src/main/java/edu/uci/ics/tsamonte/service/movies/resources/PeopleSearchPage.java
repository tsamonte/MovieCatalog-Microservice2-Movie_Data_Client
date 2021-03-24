package edu.uci.ics.tsamonte.service.movies.resources;

import edu.uci.ics.tsamonte.service.movies.base.Result;
import edu.uci.ics.tsamonte.service.movies.core.MovieRecords;
import edu.uci.ics.tsamonte.service.movies.core.ValidQueryFields;
import edu.uci.ics.tsamonte.service.movies.models.people.search.PeopleSearchQueryModel;
import edu.uci.ics.tsamonte.service.movies.models.people.search.PersonModelSearch;
import edu.uci.ics.tsamonte.service.movies.models.people.search.PersonSearchResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("people")
public class PeopleSearchPage {
    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response peopleSearchResponse(@Context HttpHeaders headers, @QueryParam("name") String name,
                                         @QueryParam("birthday") String birthday, @QueryParam("movie_title") String movie_title,
                                         @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset,
                                         @QueryParam("orderby") String orderby, @QueryParam("direction") String direction) {

        PersonSearchResponseModel responseModel;
        try {
            // Get header strings
            String email = headers.getHeaderString("email");
            String session_id = headers.getHeaderString("session_id");
            String transaction_id = headers.getHeaderString("transaction_id");

            // Checking if certain fields have valid values
            // if not valid, set to default value (?)
            if(limit != null && !ValidQueryFields.isValidLimit(limit)) {
                limit = 10;
            }

            if(limit != null) {
                if (offset != null && !ValidQueryFields.isValidOffset(limit, offset)) {
                    offset = 0;
                }
            }
            else {
                if(offset != null && !ValidQueryFields.isValidOffset(10, offset)) {
                    offset = 0;
                }
            }
            if(orderby != null && !ValidQueryFields.isValidPeopleOrderBy(orderby)) {
                orderby = "name";
            }

            if(direction != null && !ValidQueryFields.isValidDirection(direction)) {
                direction = "asc";
            }

            PeopleSearchQueryModel queryModel = new PeopleSearchQueryModel(name, birthday, movie_title, limit, offset, orderby, direction);

            PersonModelSearch[] results = MovieRecords.retrievePeopleSearchResults(queryModel);

            Response.ResponseBuilder builder;
            // resultCode = 212; 200 OK; "Found people with search parameters."
            if (results != null) {
                responseModel = new PersonSearchResponseModel(Result.PEOPLE_FOUND, results);
            }
            // resultCode = 213; 200 OK; "No people found with search parameters."
            else {
                responseModel = new PersonSearchResponseModel(Result.NO_PEOPLE_FOUND, results);
            }

            builder = Response.status(Response.Status.OK).entity(responseModel);

            // Pass along headers
            builder.header("email", email);
            builder.header("session_id", session_id);
            builder.header("transaction_id", transaction_id);

            return builder.build();
        }
        catch (Exception e) {
            e.printStackTrace();
            responseModel = new PersonSearchResponseModel(Result.INTERNAL_SERVER_ERROR, null);
            return responseModel.buildResponse();
        }
    }
}
