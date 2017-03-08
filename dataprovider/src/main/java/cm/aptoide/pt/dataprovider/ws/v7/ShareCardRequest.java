package cm.aptoide.pt.dataprovider.ws.v7;

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

  private final String cardId;
  private final String accessToken;

  protected ShareCardRequest(Body body, String baseHost, String cardId, String accessToken) {
    super(body, baseHost);
    this.cardId = cardId;
    this.accessToken = accessToken;
  }

  public static ShareCardRequest of(TimelineCard timelineCard, String accessToken,
      BodyInterceptor bodyInterceptor) {
    final ShareCardRequest.Body body = new ShareCardRequest.Body(timelineCard.getCardId());
    return new ShareCardRequest((ShareCardRequest.Body) bodyInterceptor.intercept(body),
        BASE_HOST, timelineCard.getCardId(), accessToken);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(V7.Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.shareCard(body, cardId, accessToken);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    private String cardId;

    public Body(String cardId) {
      this.cardId = cardId;
    }
  }
}

