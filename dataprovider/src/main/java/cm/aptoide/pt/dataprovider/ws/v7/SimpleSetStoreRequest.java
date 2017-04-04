package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;

/**
 * Created by pedroribeiro on 15/12/16.
 */

public class SimpleSetStoreRequest extends V7<BaseV7Response, SimpleSetStoreRequest.Body> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  protected SimpleSetStoreRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static SimpleSetStoreRequest of(String storeName, String storeTheme,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    Body body = new Body(storeName, storeTheme);
    return new SimpleSetStoreRequest(body, bodyInterceptor);
  }

  public static SimpleSetStoreRequest of(long storeId, String storeTheme, String storeDescription,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    Body body = new Body(storeId, storeTheme, storeDescription);
    return new SimpleSetStoreRequest(body, bodyInterceptor);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.editStore(body);
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {

    private String storeName;
    private Long storeId;
    @Getter @Setter private StoreProperties storeProperties;

    public Body(String storeName, String storeTheme) {
      this.storeName = storeName;
      storeProperties = new StoreProperties(storeTheme, null);
    }

    public Body(long storeId, String storeTheme, String storeDescription) {
      this.storeId = storeId;
      storeProperties = new StoreProperties(storeTheme, storeDescription);
    }
  }

  @Data public static class StoreProperties {

    @JsonProperty("theme") private String theme;
    @JsonProperty("description") private String description;

    public StoreProperties(String theme, String description) {
      this.theme = theme;
      this.description = description;
    }
  }
}
