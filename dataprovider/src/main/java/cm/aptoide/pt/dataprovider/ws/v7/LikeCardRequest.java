package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by jdandrade on 06/12/2016.
 */
public class LikeCardRequest extends V7<BaseV7Response, LikeCardRequest.Body> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";
  private final String accessToken;
  private final String cardId;
  private final int rating;

  public LikeCardRequest(Body body, String baseHost, String accessToken, String cardId, int rating) {
    super(body, baseHost);
    this.accessToken = accessToken;
    this.cardId = cardId;
    this.rating = rating;
  }

  public static LikeCardRequest of(TimelineCard timelineCard, String cardType, String ownerHash, int rating, BodyInterceptor bodyInterceptor,
      String accessToken) {
    LikeCardRequest.Body body = new LikeCardRequest.Body();

    return new LikeCardRequest((LikeCardRequest.Body) bodyInterceptor.intercept(body),
        BASE_HOST, accessToken, timelineCard.getCardId(), rating);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setReview(body, cardId, accessToken, String.valueOf(rating), true);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    public Body() {
    }
  }
}
