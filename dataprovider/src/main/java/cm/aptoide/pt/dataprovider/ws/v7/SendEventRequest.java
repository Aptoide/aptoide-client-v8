package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.AnalyticsBaseBody;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by jdandrade on 25/10/2016.
 */

public class SendEventRequest extends V7<BaseV7Response, SendEventRequest.Body> {

  private static String NAME;
  private static String ACTION = "CLICK";
  private static String CONTEXT = "TIMELINE";

  protected SendEventRequest(Body body, String baseHost) {
    super(body, baseHost);
  }

  public static SendEventRequest of(String accessToken, Body.Data data, String eventName) {
    NAME = eventName;
    BaseBodyDecorator decorator = new BaseBodyDecorator(
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID());
    SendEventRequest.Body body =
        new SendEventRequest.Body(data, DataProvider.getConfiguration().getAppId());

    return new SendEventRequest((Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.addEvent(NAME, ACTION, CONTEXT, body);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends AnalyticsBaseBody<Body.Data> {

    private Data data;

    public Body(Data data, String packageName) {
      super(packageName);
      this.data = data;
    }

    @lombok.Data @Builder public static class Data {
      private String cardType;
      private String source;
      private Specific specific;

      public Data(String cardType, String source, Specific specific) {
        this.cardType = cardType;
        this.source = source;
        this.specific = specific;
      }
    }

    @lombok.Data @Builder public static class Specific {
      private String store;
      private String app;
      private String url;
      private String similar_to;
      private String based_on;

      public Specific(String store, String app, String url, String similar_to, String based_on) {
        this.store = store;
        this.app = app;
        this.url = url;
        this.similar_to = similar_to;
        this.based_on = based_on;
      }
    }
  }
}
