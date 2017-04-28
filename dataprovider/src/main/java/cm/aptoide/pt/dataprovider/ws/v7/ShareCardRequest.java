package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by jdandrade on 24/11/2016.
 */

public class ShareCardRequest extends V7<ShareCardResponse, ShareCardRequest.Body> {

  private final String cardId;

  protected ShareCardRequest(Body body, String cardId, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
    this.cardId = cardId;
  }

  public static ShareCardRequest of(TimelineCard timelineCard,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    final ShareCardRequest.Body body = new ShareCardRequest.Body(timelineCard.getCardId());
    return new ShareCardRequest(body, timelineCard.getCardId(), bodyInterceptor, httpClient,
        converterFactory);
  }

  @Override protected Observable<ShareCardResponse> loadDataFromNetwork(V7.Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.shareCard(body, cardId, body.getAccessToken());
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {

    private final String cardId;
  }
}

