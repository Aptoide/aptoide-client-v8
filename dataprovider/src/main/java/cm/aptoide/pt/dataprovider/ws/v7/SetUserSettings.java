package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by pedroribeiro on 01/06/17.
 */
public class SetUserSettings extends V7<BaseV7Response, SetUserSettings.Body> {

  protected SetUserSettings(Body body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static String getHost() {
    return BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static SetUserSettings of(boolean adultContentEnabled, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    final Body body = new Body(adultContentEnabled);
    body.setMature(adultContentEnabled);
    return new SetUserSettings(body, getHost(), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setUserSettings(body);
  }

  public static class Body extends BaseBody {
    public boolean mature;

    public Body(boolean mature) {
      this.mature = mature;
    }

    @Override public boolean isMature() {
      return mature;
    }

    @Override public void setMature(boolean mature) {
      this.mature = mature;
    }
  }
}
