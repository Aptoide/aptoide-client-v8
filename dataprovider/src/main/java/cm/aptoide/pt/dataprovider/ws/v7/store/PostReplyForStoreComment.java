package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.BaseV7Response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

public class PostReplyForStoreComment extends V7<BaseV7Response, PostReplyForStoreComment.Body> {

private static final String BASE_HOST = "http://ws75-primary.aptoide.com/api/7/";

  protected PostReplyForStoreComment(PostReplyForStoreComment.Body body, String baseHost) {
    super(body, baseHost);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.postStoreCommentReply(body, true);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    @JsonProperty("comment_id") private long comment_id;
    private final String comment_type = "STORE";
    private String body;

    public Body(long comment_id, String text) {
      this.comment_id = comment_id;
      this.body = text;
    }
  }
}
