package cm.aptoide.pt.timeline;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ShareInstallCardRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.repository.exception.RepositoryIllegalArgumentException;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;

/**
 * Created by jdandrade on 21/11/2016.
 */
public class SocialRepository {

  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final Converter.Factory converterFactory;
  private final OkHttpClient httpClient;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public SocialRepository(BodyInterceptor<BaseBody> bodyInterceptor,
      Converter.Factory converterFactory, OkHttpClient httpClient,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    this.bodyInterceptor = bodyInterceptor;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public void share(String packageName, Long storeId, String shareType) {
    //todo(pribeiro): check if timelineSocialActionData is null
    ShareInstallCardRequest.of(packageName, storeId, shareType, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            return Completable.complete();
          }
          return Completable.error(
              new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        })
        .subscribe(() -> {
        }, throwable -> throwable.printStackTrace());
  }

  public Completable asyncShare(String packageName, Long storeId, String shareType) {
    return ShareInstallCardRequest.of(packageName, storeId, shareType, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response.isOk()) {
            return Completable.complete();
          }
          return Completable.error(
              new RepositoryIllegalArgumentException(V7.getErrorMessage(response)));
        });
  }
}

