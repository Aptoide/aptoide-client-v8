package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by jdandrade on 24/11/2016.
 */

public class ShareCardRequest extends V7<ShareCardResponse, ShareCardRequest.Body> {

  private final String cardId;

  protected ShareCardRequest(Body body, String cardId, BodyInterceptor bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
    this.cardId = cardId;
  }

  public static ShareCardRequest of(TimelineCard timelineCard, BodyInterceptor bodyInterceptor) {
    final ShareCardRequest.Body body = new ShareCardRequest.Body(timelineCard.getCardId());
    return new ShareCardRequest(body, timelineCard.getCardId(), bodyInterceptor);
  }

  @Override protected Observable<ShareCardResponse> loadDataFromNetwork(V7.Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.shareCard(body, cardId, body.getAccessToken());
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {

    private final String cardId;
  }
}

