package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
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

  //private static final String BASE_HOST = "http://ws75-primary.aptoide.com/api/7/";

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";
  private static String access_token;
  private static String cardId;
  private static int rating;

  public LikeCardRequest(LikeCardRequest.Body body, String baseHost) {
    super(body, baseHost);
  }

  public static LikeCardRequest of(TimelineCard timelineCard, String cardType, String ownerHash,
      String accessToken, String aptoideClientUUID, int ratng) {
    access_token = accessToken;
    cardId = timelineCard.getCardId();
    rating = ratng;
    LikeCardRequest.Body body = new LikeCardRequest.Body(timelineCard.getCardId());

    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    return new LikeCardRequest((LikeCardRequest.Body) decorator.decorate(body, accessToken),
        BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setReview(body, cardId, access_token, String.valueOf(rating), true);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true)
  public static class Body extends BaseBody {

    private String cardId;

    public Body(String cardId) {
      this.cardId = cardId;
    }
  }
}
