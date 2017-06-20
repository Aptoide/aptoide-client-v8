package cm.aptoide.pt.v8engine.comments;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.model.v7.ListReviews;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.dataprovider.interfaces.SuccessRequestListener;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.store.StoreUtils;
import cm.aptoide.pt.v8engine.view.comments.ConcreteItemCommentAdder;
import cm.aptoide.pt.v8engine.view.comments.RateAndReviewCommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.reviews.RateAndReviewsFragment;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ListFullReviewsSuccessRequestListener implements SuccessRequestListener<ListReviews> {

  private final RateAndReviewsFragment fragment;
  private final Converter.Factory converterFactory;
  private final BodyInterceptor<BaseBody> bodyBodyInterceptor;
  private final OkHttpClient httpClient;
  private final StoreCredentialsProvider storeCredentialsProvider;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public ListFullReviewsSuccessRequestListener(RateAndReviewsFragment fragment,
      StoreCredentialsProvider storeCredentialsProvider,
      BodyInterceptor<BaseBody> baseBodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.fragment = fragment;
    this.httpClient = httpClient;
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyBodyInterceptor = baseBodyInterceptor;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public void call(ListReviews listFullReviews) {

    List<Review> reviews = listFullReviews.getDatalist()
        .getList();
    List<Displayable> displayables = new LinkedList<>();

    Observable.from(reviews)
        .flatMap(review -> {
          return ListCommentsRequest.of( // fetch the list of comments for each review
              review.getComments()
                  .getView(), review.getId(), 3,
              StoreUtils.getStoreCredentials(fragment.getStoreName(), storeCredentialsProvider),
              true, bodyBodyInterceptor, httpClient, converterFactory, tokenInvalidator,
              sharedPreferences)
              .observe()
              .subscribeOn(Schedulers.io()) // parallel I/O split point
              .map(listComments -> {
                review.setCommentList(listComments);
                return review;
              });
        })
        .toList() // parallel I/O merge point
        .observeOn(AndroidSchedulers.mainThread())
        .compose(fragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(reviewList -> {
          addRateAndReviewDisplayables(reviews, displayables);
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        });
  }

  private void addRateAndReviewDisplayables(List<Review> reviews, List<Displayable> displayables) {
    int index = -1;
    int count = 0;
    for (final Review review : reviews) {
      displayables.add(
          new RateAndReviewCommentDisplayable(new ReviewWithAppName(fragment.getAppName(), review),
              new ConcreteItemCommentAdder(count, fragment, review), review.getCommentList()
              .getTotal()));

      if (review.getId() == fragment.getReviewId()) {
        index = count;
      }
      if (review.getCommentList() != null
          && review.getCommentList()
          .getDatalist() != null
          && review.getCommentList()
          .getDatalist()
          .getLimit() != null) {
        fragment.createDisplayableComments(review.getCommentList()
            .getDatalist()
            .getList(), displayables);
        if (review.getCommentList()
            .getDatalist()
            .getList()
            .size() > 2) {
          displayables.add(fragment.createReadMoreDisplayable(count, review));
        }
      }
      count++;
    }

    // Hammered to fix layout not visible on first call.
    if (fragment.getAdapter()
        .getItemCount() == 0) {
      index = 0;
    }

    fragment.checkAndRemoveProgressBarDisplayable();
    fragment.addDisplayables(displayables);
    if (index >= 0) {
      fragment.getLayoutManager()
          .scrollToPosition(fragment.getAdapter()
              .getItemPosition(index));
    }
  }
}
