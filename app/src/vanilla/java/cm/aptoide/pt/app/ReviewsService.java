package cm.aptoide.pt.app;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.Review;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.ListReviewsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetReviewRatingRequest;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
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

  public Single<List<Review>> loadListReviews(String storeName, String packageName, int maxReviews,
      String languagesFilterSort) {
    BaseRequestWithStore.StoreCredentials storeCredentials =
        storeCredentialsProvider.get(storeName);
    return ListReviewsRequest.ofTopReviews(storeName, packageName, maxReviews, storeCredentials,
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences,
        languagesFilterSort)
        .observe()
        .map(listReviews -> listReviews.getDataList()
            .getList())
        .toSingle()
        .onErrorReturn(throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  public Single<BaseV7Response> doReviewRatingRequest(long reviewId, boolean helpful) {
    return SetReviewRatingRequest.of(reviewId, helpful, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle();
  }
}
