package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import lombok.AllArgsConstructor;
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
  private static String CONTEXT = "timeline";

  protected SendEventRequest(Body body, String baseHost) {
    super(body, baseHost);
  }

  public static SendEventRequest of(String accessToken, Body.Data data, String eventName) {
    NAME = eventName;
    BaseBodyDecorator decorator = new BaseBodyDecorator(
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID());
    SendEventRequest.Body body = new SendEventRequest.Body(data);

    return new SendEventRequest((Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.addEvent(NAME, ACTION, CONTEXT, body);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    private Data data;

    public Body(Data data) {
      this.data = data;
    }

    @lombok.Data @Builder @AllArgsConstructor public static class Data {
      private String cardType;
      private String source;
      private Specific specific;
    }

    @lombok.Data @Builder @AllArgsConstructor public static class Specific {
      private String store;
      private String app;
      private String url;
      private String similar_to;
      private String based_on;
    }
  }
}
