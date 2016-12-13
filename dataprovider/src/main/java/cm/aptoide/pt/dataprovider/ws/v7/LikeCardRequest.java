package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by jdandrade on 06/12/2016.
 */
public class LikeCardRequest extends V7<BaseV7Response, LikeCardRequest.Body> {
  private static final String BASE_HOST = "http://ws75-primary.aptoide.com/api/7/";
  private static String email;
  private static String access_token;
  private static String cardId;
  private static int rating;

  public LikeCardRequest(LikeCardRequest.Body body, String baseHost) {
    super(body, baseHost);
  }

  public static LikeCardRequest of(TimelineCard timelineCard, String cardType, String ownerHash,
      String accessToken, String aptoideClientUUID, String userEmail, int ratng) {
    email = userEmail;
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

  @AllArgsConstructor @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true)
  public static class Body extends BaseBody {

    private String cardId;

    //@Builder @lombok.Data @AllArgsConstructor public static class CardData {
    //  private String type;
    //  private List<App> packages;
    //  private String url;
    //  private String title;
    //  private String thumbnailurl;
    //  private String publisherid;
    //  private String publisherurl;
    //  private String publisherlogo;
    //  private Date date;
    //  private String cardId;
    //  private String ownerHash;
    //}
  }
}
