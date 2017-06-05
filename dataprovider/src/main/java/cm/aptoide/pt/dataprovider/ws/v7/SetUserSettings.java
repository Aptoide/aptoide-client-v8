package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.BaseV7Response;
import lombok.Data;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by pedroribeiro on 01/06/17.
 */

public class SetUserSettings extends V7<BaseV7Response, SetUserSettings.Body> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  protected SetUserSettings(Body body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor);
  }

  public static SetUserSettings of(boolean adultContentEnabled, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor) {
    Body body = new Body(adultContentEnabled);
    body.setMature(adultContentEnabled);
    return new SetUserSettings(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setUserSettings(body);
  }

  @Data public static class Body extends BaseBody {
    public boolean mature;

    public Body(boolean mature) {
      this.mature = mature;
    }
  }
}
