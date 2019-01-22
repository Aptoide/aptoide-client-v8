package cm.aptoide.pt.app;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.ListReviews;
import cm.aptoide.pt.dataprovider.model.v7.Review;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.ListReviewsRequest;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

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

  public Single<ReviewRequestResult> loadReviews(String storeName, String packageName,
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
        .onErrorReturn(throwable -> createReviewRequestResultError(throwable));
  }

  @NonNull private ReviewRequestResult createReviewRequestResultError(Throwable throwable) {
    if (throwable instanceof NoNetworkConnectionException) {
      return new ReviewRequestResult(ReviewRequestResult.Error.NETWORK);
    } else {
      return new ReviewRequestResult(ReviewRequestResult.Error.GENERIC);
    }
  }

  private Observable<ReviewRequestResult> mapListReviews(ListReviews listReviews) {
    if (listReviews.isOk()) {
      return Observable.just(new ReviewRequestResult(map(listReviews.getDataList()
          .getList())));
    } else {
      return Observable.error(new IllegalStateException("Could not obtain request from server."));
    }
  }

  private List<AppReview> map(List<Review> reviews) {
    List<AppReview> appReviews = new ArrayList<>();
    if (reviews != null) {
      for (Review review : reviews) {
        Review.Stats stats = review.getStats();
        Review.User user = review.getUser();
        ReviewStats reviewStats =
            new ReviewStats(stats.getComments(), stats.getLikes(), stats.getPoints(),
                stats.getRating());
        ReviewComment reviewComment = new ReviewComment(review.getComments()
            .getView(), review.getComments()
            .getTotal());
        ReviewUser reviewUser = new ReviewUser(user.getId(), user.getAvatar(), user.getName());
        appReviews.add(
            new AppReview(review.getId(), review.getTitle(), review.getBody(), review.getAdded(),
                review.getModified(), reviewStats, reviewComment, reviewUser));
      }
    }
    return appReviews;
  }
}
