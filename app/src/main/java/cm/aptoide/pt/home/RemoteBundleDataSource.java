package cm.aptoide.pt.home;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class RemoteBundleDataSource implements BundleDataSource {
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient okHttpClient;
  private final Converter.Factory converterFactory;
  private final BundlesResponseMapper mapper;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public RemoteBundleDataSource(BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient okHttpClient, Converter.Factory converterFactory, BundlesResponseMapper mapper,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    this.bodyInterceptor = bodyInterceptor;
    this.okHttpClient = okHttpClient;
    this.converterFactory = converterFactory;
    this.mapper = mapper;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public Single<List<AppBundle>> getBundles() {
    return GetHomeBundlesRequest.of(okHttpClient, converterFactory, bodyInterceptor,
        tokenInvalidator, sharedPreferences)
        .observe()
        .map(mapper.map())
        .toSingle();
  }
}
