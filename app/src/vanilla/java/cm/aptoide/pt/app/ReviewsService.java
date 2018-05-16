package cm.aptoide.pt.app;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.ListReviews;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.ListReviewsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetReviewRatingRequest;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by D01 on 04/05/18.
 */

public class ReviewsService {

  private final StoreCredentialsProvider storeCredentialsProvider;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private boolean loading;

  public ReviewsService(StoreCredentialsProvider storeCredentialsProvider,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public Single<ReviewRequestResult> loadListReviews(String storeName, String packageName,
      int maxReviews, String languagesFilterSort) {
    if (loading) {
      return Single.just(new ReviewRequestResult(true));
    }
    BaseRequestWithStore.StoreCredentials storeCredentials =
        storeCredentialsProvider.get(storeName);
    return ListReviewsRequest.ofTopReviews(storeName, packageName, maxReviews, storeCredentials,
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences,
        languagesFilterSort)
        .observe()
        .doOnSubscribe(() -> loading = true)
        .doOnUnsubscribe(() -> loading = false)
        .doOnTerminate(() -> loading = false)
        .flatMap(listReviews -> mapListReviews(listReviews))
        .toSingle()
        .onErrorReturn(throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private Observable<ReviewRequestResult> mapListReviews(ListReviews listReviews) {
    if (listReviews.isOk()) {
      return Observable.just(new ReviewRequestResult(listReviews.getDataList()
          .getList()));
    } else {
      return Observable.error(new IllegalStateException("Could not obtain request from server."));
    }
  }

  public Single<BaseV7Response> doReviewRatingRequest(long reviewId, boolean helpful) {
    return SetReviewRatingRequest.of(reviewId, helpful, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle();
  }
}
