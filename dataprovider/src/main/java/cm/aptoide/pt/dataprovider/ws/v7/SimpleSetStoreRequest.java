package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
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

  public static SimpleSetStoreRequest of(String accessToken, String aptoideClientUUID,
      String storeName, String storeTheme) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    Body body = new Body(accessToken, storeName, storeTheme);
    return new SimpleSetStoreRequest((Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  public static SimpleSetStoreRequest of(String accessToken, String aptoideClientUUID,
      long storeId, String storeTheme, String storeDescription) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    Body body = new Body(accessToken, storeId, storeTheme, storeDescription);
    return new SimpleSetStoreRequest((Body) decorator.decorate(body, accessToken), BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.editStore(body);
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {

    private String storeName;
    //private String storeProperties;
    private String accessToken;
    private Long storeId;
    @Getter @Setter private StoreProperties storeProperties;

    public Body(String accessToken, String storeName, String storeTheme) {
      this.accessToken = accessToken;
      this.storeName = storeName;
      storeProperties = new StoreProperties(storeTheme, null);
    }

    public Body(String accessToken, long storeId, String storeTheme, String storeDescription) {
      this.accessToken = accessToken;
      this.storeId = storeId;
      storeProperties = new StoreProperties(storeTheme, storeDescription);
    }
  }

  @Data
  public static class StoreProperties {

    @JsonProperty("theme")
    private String theme;
    @JsonProperty("description")
    private String description;

    public StoreProperties(String theme, String description) {
      this.theme = theme;
      this.description = description;
    }

  }
}
