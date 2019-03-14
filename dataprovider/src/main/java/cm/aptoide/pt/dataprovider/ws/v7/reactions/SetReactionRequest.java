package cm.aptoide.pt.dataprovider.ws.v7.reactions;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class SetReactionRequest extends V7<BaseV7Response, SetReactionRequest.Body> {
  public SetReactionRequest(Body body, OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor bodyInterceptor, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    throw new IllegalStateException("webservice not implemented, should not be used.");
  }

  public static class Body extends BaseBody {
    private final String id;

    public Body(String id) {
      this.id = id;
    }
  }
}