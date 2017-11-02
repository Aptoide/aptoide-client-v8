package cm.aptoide.pt.dataprovider.ws.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 30/10/2017.
 */

public class PostRead {
  @JsonProperty("uid") private final String postId;
  @JsonProperty("type") private final String postType;

  public PostRead(String postId, String postType) {
    this.postId = postId;
    this.postType = postType;
  }
}
