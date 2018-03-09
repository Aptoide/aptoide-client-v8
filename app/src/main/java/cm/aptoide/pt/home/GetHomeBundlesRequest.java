package cm.aptoide.pt.home;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessDataListResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class GetHomeBundlesRequest
    extends V7<BaseV7EndlessDataListResponse, GetHomeBundlesRequest.Body> {
  protected GetHomeBundlesRequest(Body body, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static GetHomeBundlesRequest of(OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    return new GetHomeBundlesRequest(new Body(), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator, sharedPreferences);
  }

  @Override
  protected Observable<BaseV7EndlessDataListResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getHomeBundles();
  }

  public static class Body extends BaseBody implements Endless {
    private int offset;

    @Override public int getOffset() {
      return this.offset;
    }

    @Override public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return 3;
    }
  }
}
