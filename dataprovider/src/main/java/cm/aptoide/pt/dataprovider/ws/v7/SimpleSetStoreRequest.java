package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.BaseV7Response;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;

/**
 * Created by pedroribeiro on 15/12/16.
 */

public class SimpleSetStoreRequest extends V7<BaseV7Response, SimpleSetStoreRequest.Body> {

  private static final String BASE_HOST = "https://ws75-primary.aptoide.com/api/7/";

  protected SimpleSetStoreRequest(Body body, String baseHost) {
    super(body, baseHost);
  }

  public static SimpleSetStoreRequest of(String accessToken, String aptoideClientUUID, String storeName, String storeTheme) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    Body body = new Body(accessToken, storeName, storeTheme);
    return new SimpleSetStoreRequest((Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.editStore(body);
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {

    private String storeName;
    private String storeProperties;
    private String accessToken;

    public Body(String accessToken, String storeName, String storeTheme) {
      this.accessToken = accessToken;
      this.storeName = storeName;
      try {
        storeProperties = (new JSONObject().put("theme", storeTheme)).toString();
      } catch (JSONException e) {
        Logger.e(SimpleSetStoreRequest.Body.class.getSimpleName(), "Couldn't build store_properties json", e);
      }
  }

  }
}
