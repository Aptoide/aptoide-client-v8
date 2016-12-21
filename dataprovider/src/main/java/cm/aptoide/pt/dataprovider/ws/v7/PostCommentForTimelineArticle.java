package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.BaseV7Response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

public class PostCommentForTimelineArticle
    extends V7<BaseV7Response, PostCommentForTimelineArticle.Body> {

  private static final String BASE_HOST = "http://ws75-primary.aptoide.com/api/7/";

  private PostCommentForTimelineArticle(Body body, String baseHost) {
    super(body, baseHost);
  }

  public static PostCommentForTimelineArticle of(String timelineArticleId, String text,
      String accessToken, String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    Body body = new Body(timelineArticleId, text);
    return new PostCommentForTimelineArticle((Body) decorator.decorate(body, accessToken),
        BASE_HOST);
  }

  public static PostCommentForTimelineArticle of(String timelineArticleId, long previousCommentId,
      String text, String accessToken, String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    Body body = new Body(timelineArticleId, text, previousCommentId);
    return new PostCommentForTimelineArticle((Body) decorator.decorate(body, accessToken),
        BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.postTimelineComment(body, true);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    @JsonProperty("id") private String timelineArticleId;
    @JsonProperty("comment_id") private Long previousCommentId;
    private String body;
    private String commentType = CommentType.TIMELINE.name();

    public Body(String timelineArticleId, String text) {
      this.timelineArticleId = timelineArticleId;
      this.body = text;
    }

    public Body(String timelineArticleId, String text, long previousCommentId) {
      this(timelineArticleId, text);
      this.previousCommentId = previousCommentId;
    }
  }
}
