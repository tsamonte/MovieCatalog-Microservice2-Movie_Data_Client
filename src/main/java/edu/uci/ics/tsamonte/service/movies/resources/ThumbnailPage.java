package edu.uci.ics.tsamonte.service.movies.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.tsamonte.service.movies.base.Result;
import edu.uci.ics.tsamonte.service.movies.core.IdmCaller;
import edu.uci.ics.tsamonte.service.movies.core.MovieRecords;
import edu.uci.ics.tsamonte.service.movies.models.get.GetResponseModel;
import edu.uci.ics.tsamonte.service.movies.models.privilege.PrivilegeRequestModel;
import edu.uci.ics.tsamonte.service.movies.models.privilege.PrivilegeResponseModel;
import edu.uci.ics.tsamonte.service.movies.models.thumbnail.ThumbnailModel;
import edu.uci.ics.tsamonte.service.movies.models.thumbnail.ThumbnailRequestModel;
import edu.uci.ics.tsamonte.service.movies.models.thumbnail.ThumbnailResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("thumbnail")
public class ThumbnailPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response thumbNailResponse(@Context HttpHeaders headers, String jsonText) {
        ThumbnailRequestModel requestModel;
        ThumbnailResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, ThumbnailRequestModel.class);
        }
        catch (IOException e) {
            e.printStackTrace();
            responseModel = new ThumbnailResponseModel(Result.INTERNAL_SERVER_ERROR, null);
            return responseModel.buildResponse();
        }

        // Get header strings
        String email = headers.getHeaderString("email");
        String session_id = headers.getHeaderString("session_id");
        String transaction_id = headers.getHeaderString("transaction_id");

        ThumbnailModel[] thumbnails = MovieRecords.retrieveThumbnails(requestModel.getMovie_ids());

        Response.ResponseBuilder builder;
        // resultCode = 210; 200 OK; "Found movie(s) with search parameters."
        if (thumbnails != null || thumbnails.length > 0) {
            responseModel = new ThumbnailResponseModel(Result.FOUND_MOVIES, thumbnails);
        }
        // resultCode = 211; 200 OK; "No movies found with search parameters."
        else {
            responseModel = new ThumbnailResponseModel(Result.NO_MOVIES_FOUND, thumbnails);
        }

        builder = Response.status(Response.Status.OK).entity(responseModel);

        // Pass along headers
        builder.header("email", email);
        builder.header("session_id", session_id);
        builder.header("transaction_id", transaction_id);

        return builder.build();
    }
}
