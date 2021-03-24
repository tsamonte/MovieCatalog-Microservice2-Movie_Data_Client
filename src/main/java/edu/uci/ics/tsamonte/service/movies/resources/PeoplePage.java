package edu.uci.ics.tsamonte.service.movies.resources;

import edu.uci.ics.tsamonte.service.movies.MoviesService;
import edu.uci.ics.tsamonte.service.movies.base.Result;
import edu.uci.ics.tsamonte.service.movies.core.IdmCaller;
import edu.uci.ics.tsamonte.service.movies.core.MovieRecords;
import edu.uci.ics.tsamonte.service.movies.core.ValidQueryFields;
import edu.uci.ics.tsamonte.service.movies.models.MovieModel;
import edu.uci.ics.tsamonte.service.movies.models.MovieResultsResponseModel;
import edu.uci.ics.tsamonte.service.movies.models.people.PeopleQueryModel;
import edu.uci.ics.tsamonte.service.movies.models.privilege.PrivilegeRequestModel;
import edu.uci.ics.tsamonte.service.movies.models.privilege.PrivilegeResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("people")
public class PeoplePage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response peopleResponse(@Context HttpHeaders headers, @QueryParam("name") String name,
                                   @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset,
                                   @QueryParam("orderby") String orderby, @QueryParam("direction") String direction) {

        MovieResultsResponseModel responseModel;

        try {
            // ==================== Calling idm/privilege ===================
            // Path of endpoint
//            String servicePath = "http://localhost:5813/api/idm";
            String servicePath = "http://" + MoviesService.getIdmConfigs().getHostName() + ":" + MoviesService.getIdmConfigs().getPort() + MoviesService.getIdmConfigs().getPath();
            String endpointPath = "/privilege";

            // Get header strings
            String email = headers.getHeaderString("email");
            String session_id = headers.getHeaderString("session_id");
            String transaction_id = headers.getHeaderString("transaction_id");

            PrivilegeRequestModel requestModel = new PrivilegeRequestModel(email, 4);

            PrivilegeResponseModel privilegeResponse = IdmCaller.makePost(servicePath, endpointPath, requestModel);

            // ==================== Main functionality of endpoint ===================
            Boolean hidden;
            if (privilegeResponse.getResultCode() == 140) {
                hidden = true;
            }
            else { // if (privilegeResponse.getResultCode() == 141) {
                hidden = null;
            }

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
            if(orderby != null && !ValidQueryFields.isValidOrderBy(orderby)) {
                orderby = "title";
            }

            if(direction != null && !ValidQueryFields.isValidDirection(direction)) {
                direction = "asc";
            }

            PeopleQueryModel queryModel = new PeopleQueryModel(name, limit, offset, orderby, direction);

            MovieModel[] results = MovieRecords.retrievePeopleResults(queryModel, hidden);

            Response.ResponseBuilder builder;
            // resultCode = 213; 200 OK; "No people found with search parameters."
            if(!MovieRecords.personExists(name)) {
                responseModel = new MovieResultsResponseModel(Result.NO_PEOPLE_FOUND, null);
            }
            else {
                // resultCode = 210; 200 OK; "Found movie(s) with search parameters."
                if (results != null) {
                    responseModel = new MovieResultsResponseModel(Result.FOUND_MOVIES, results);
                }
                // resultCode = 211; 200 OK; "No movies found with search parameters."
                else {
                    responseModel = new MovieResultsResponseModel(Result.NO_MOVIES_FOUND, results);
                }
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
            responseModel = new MovieResultsResponseModel(Result.INTERNAL_SERVER_ERROR, null);
            return responseModel.buildResponse();
        }
    }
}
