package edu.uci.ics.tsamonte.service.movies.resources;

import edu.uci.ics.tsamonte.service.movies.MoviesService;
import edu.uci.ics.tsamonte.service.movies.base.Result;
import edu.uci.ics.tsamonte.service.movies.core.IdmCaller;
import edu.uci.ics.tsamonte.service.movies.core.MovieRecords;
import edu.uci.ics.tsamonte.service.movies.models.get.GetPageMovieModel;
import edu.uci.ics.tsamonte.service.movies.models.get.GetResponseModel;
import edu.uci.ics.tsamonte.service.movies.models.privilege.PrivilegeRequestModel;
import edu.uci.ics.tsamonte.service.movies.models.privilege.PrivilegeResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("get")
public class GetPage {
    @Path("{movie_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getResponse(@Context HttpHeaders headers, @PathParam("movie_id") String movie_id) {
        GetResponseModel responseModel;

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
            if(privilegeResponse.getResultCode() == 140) {
                hidden = true;
            }
            else {
                hidden = null;
            }

            GetPageMovieModel movie = MovieRecords.retrieveMovie(movie_id, hidden);

            Response.ResponseBuilder builder;
            // resultCode = 211; 200 OK; "No movies found with search parameters."
            if (movie == null || privilegeResponse.getResultCode() == 141){
                responseModel = new GetResponseModel(Result.NO_MOVIES_FOUND, movie);
            }
            // resultCode = 210; 200 OK; "Found movie(s) with search parameters."
            else { //if (movie != null) {
                responseModel = new GetResponseModel(Result.FOUND_MOVIES, movie);
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
            responseModel = new GetResponseModel(Result.INTERNAL_SERVER_ERROR, null);
            return responseModel.buildResponse();
        }
    }
}
