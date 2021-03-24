package edu.uci.ics.tsamonte.service.movies.resources;

import edu.uci.ics.tsamonte.service.movies.base.Result;
import edu.uci.ics.tsamonte.service.movies.core.MovieRecords;
import edu.uci.ics.tsamonte.service.movies.models.people.get.PersonGetResponseModel;
import edu.uci.ics.tsamonte.service.movies.models.people.get.PersonModelGet;
import edu.uci.ics.tsamonte.service.movies.models.people.search.PersonSearchResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/people/get")
public class PeopleGetPage {
    @Path("{person_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response peopleGetResponse(@Context HttpHeaders headers, @PathParam("person_id") int person_id) {
        PersonGetResponseModel responseModel;

        try {
            // Get header strings
            String email = headers.getHeaderString("email");
            String session_id = headers.getHeaderString("session_id");
            String transaction_id = headers.getHeaderString("transaction_id");

            PersonModelGet person = MovieRecords.retrievePeopleGetResults(person_id);

            Response.ResponseBuilder builder;
            // resultCode = 212; 200 OK; "Found people with search parameters."
            if(person != null) {
                responseModel = new PersonGetResponseModel(Result.PEOPLE_FOUND, person);
            }
            // resultCode = 213; 200 OK; "No people found with search parameters."
            else {
                responseModel = new PersonGetResponseModel(Result.NO_PEOPLE_FOUND, person);
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
            responseModel = new PersonGetResponseModel(Result.INTERNAL_SERVER_ERROR, null);
            return responseModel.buildResponse();
        }
    }
}
