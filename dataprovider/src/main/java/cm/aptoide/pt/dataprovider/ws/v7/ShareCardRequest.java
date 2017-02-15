package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by jdandrade on 24/11/2016.
 */

public class ShareCardRequest extends V7<BaseV7Response, ShareCardRequest.Body> {

  //private static final String BASE_HOST = "http://54.171.127.167/shares/v1.0/";

  private static String cardId;
  private static String access_token;

  protected ShareCardRequest(ShareCardRequest.Body body, String baseHost) {
    super(body, baseHost);
  }

  public static ShareCardRequest of(TimelineCard timelineCard, String accessToken,
      String aptoideClientUUID) {
    cardId = timelineCard.getCardId();
    access_token = accessToken;
    ShareCardRequest.Body body = new ShareCardRequest.Body(timelineCard.getCardId());
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    return new ShareCardRequest((ShareCardRequest.Body) decorator.decorate(body, accessToken),
        BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(V7.Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.shareCard(body, cardId, access_token);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    private String cardId;

    public Body(String cardId) {
      this.cardId = cardId;
    }
  }
}

