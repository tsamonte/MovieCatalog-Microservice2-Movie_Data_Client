package edu.uci.ics.tsamonte.service.movies.models.thumbnail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.tsamonte.service.movies.base.Result;
import edu.uci.ics.tsamonte.service.movies.models.ResponseModel;

public class ThumbnailResponseModel extends ResponseModel {
    @JsonProperty(value = "thumbnails", required = true)
    private ThumbnailModel[] thumbnails;

    @JsonCreator
    public ThumbnailResponseModel(Result result,
                                  @JsonProperty(value = "thumbnails", required = true) ThumbnailModel[] thumbnails) {
        super(result);
        this.thumbnails = thumbnails;
    }

    @JsonProperty(value = "thumbnails")
    public ThumbnailModel[] getThumbnails() {
        return thumbnails;
    }
}
