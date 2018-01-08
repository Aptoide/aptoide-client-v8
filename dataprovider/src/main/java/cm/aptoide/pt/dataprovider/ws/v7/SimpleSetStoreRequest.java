package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import okhttp3.OkHttpClient;
import org.parceler.Parcel;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by pedroribeiro on 15/12/16.
 */

public class SimpleSetStoreRequest extends V7<BaseV7Response, SimpleSetStoreRequest.Body> {

  private SimpleSetStoreRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      List<Store.SocialChannelType> storeDeleteSocialLinksList) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  @NonNull public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static SimpleSetStoreRequest of(String storeName, String storeTheme,
      String storeDescription, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, List<StoreLinks> storeLinksList,
      List<Store.SocialChannelType> storeDeleteSocialLinksList) {
    Body body = new Body(storeName, storeTheme, storeDescription, storeLinksList,
        storeDeleteSocialLinksList);
    return new SimpleSetStoreRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, storeDeleteSocialLinksList);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.editStore(body);
  }

  public static class Body extends BaseBody {

    @JsonProperty("store_links") private List<StoreLinks> storeLinksList;
    @JsonProperty("store_del_links") private List<Store.SocialChannelType>
        storeDeleteSocialLinksList;
    private StoreProperties storeProperties;
    private String storeName;

    public Body(String storeName, String storeTheme, String storeDescription,
        List<StoreLinks> storeLinksList, List<Store.SocialChannelType> storeDeleteSocialLinksList) {
      this.storeName = storeName;
      this.storeLinksList = storeLinksList;
      this.storeDeleteSocialLinksList = storeDeleteSocialLinksList;
      storeProperties = new StoreProperties(storeTheme, storeDescription);
    }

    public StoreProperties getStoreProperties() {
      return storeProperties;
    }

    public void setStoreProperties(StoreProperties storeProperties) {
      this.storeProperties = storeProperties;
    }

    public String getStoreName() {
      return storeName;
    }

    public void setStoreName(String storeName) {
      this.storeName = storeName;
    }

    public List<StoreLinks> getStoreLinksList() {
      return storeLinksList;
    }

    public void setStoreLinksList(List<StoreLinks> storeLinksList) {
      this.storeLinksList = storeLinksList;
    }

    public List<Store.SocialChannelType> getStoreDeleteSocialLinksList() {
      return storeDeleteSocialLinksList;
    }

    public void setStoreDeleteSocialLinksList(
        List<Store.SocialChannelType> storeDeleteSocialLinksList) {
      this.storeDeleteSocialLinksList = storeDeleteSocialLinksList;
    }
  }

  public static class StoreProperties {

    @JsonProperty("theme") private String theme;
    @JsonProperty("description") private String description;

    public StoreProperties(String theme, String description) {
      this.theme = theme;
      this.description = description;
    }

    public String getTheme() {
      return theme;
    }

    public void setTheme(String theme) {
      this.theme = theme;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }
  }

  @Parcel public static class StoreLinks {

    Store.SocialChannelType type;
    String url;

    public StoreLinks() {
    }

    public StoreLinks(Store.SocialChannelType type, String url) {
      this.type = type;
      this.url = url;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public Store.SocialChannelType getType() {
      return type;
    }

    public void setType(Store.SocialChannelType type) {
      this.type = type;
    }
  }
}
