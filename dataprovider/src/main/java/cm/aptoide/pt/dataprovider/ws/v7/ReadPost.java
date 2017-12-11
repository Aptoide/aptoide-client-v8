package cm.aptoide.pt.dataprovider.ws.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 30/10/2017.
 */

public class ReadPost {
  @JsonProperty("uid") private final String postId;
  @JsonProperty("type") private final String postType;

  public ReadPost(String postId, String postType) {
    this.postId = postId;
    this.postType = postType;
  }
}
