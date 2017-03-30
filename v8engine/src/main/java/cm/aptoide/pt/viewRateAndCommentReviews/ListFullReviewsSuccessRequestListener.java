package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.model.v7.ListReviews;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class ListFullReviewsSuccessRequestListener implements SuccessRequestListener<ListReviews> {

  private final RateAndReviewsFragment fragment;
  private StoreCredentialsProvider storeCredentialsProvider;
  private BodyInterceptor<BaseBody> bodyBodyInterceptor;

  ListFullReviewsSuccessRequestListener(RateAndReviewsFragment fragment,
      StoreCredentialsProvider storeCredentialsProvider,
      BodyInterceptor<BaseBody> baseBodyInterceptor) {
    this.fragment = fragment;
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyBodyInterceptor = baseBodyInterceptor;
  }

  @Override public void call(ListReviews listFullReviews) {

    List<Review> reviews = listFullReviews.getDatalist().getList();
    List<Displayable> displayables = new LinkedList<>();

    Observable.from(reviews)
        .flatMap(review -> ListCommentsRequest.of( // fetch the list of comments for each review
            review.getComments().getView(), review.getId(), 3,
            StoreUtils.getStoreCredentials(fragment.getStoreName(), storeCredentialsProvider), true,
            bodyBodyInterceptor).observe().subscribeOn(Schedulers.io()) // parallel I/O split point
            .map(listComments -> {
              review.setCommentList(listComments);
              return review;
            }))
        .toList() // parallel I/O merge point
        .observeOn(AndroidSchedulers.mainThread())
        .compose(fragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(reviewList -> {
          addRateAndReviewDisplayables(reviews, displayables);
        }, err -> {
          CrashReport.getInstance().log(err);
        });
  }

  private void addRateAndReviewDisplayables(List<Review> reviews, List<Displayable> displayables) {
    int index = -1;
    int count = 0;
    for (final Review review : reviews) {
      displayables.add(
          new RateAndReviewCommentDisplayable(new ReviewWithAppName(fragment.getAppName(), review),
              new ConcreteItemCommentAdder(count, fragment, review),
              review.getCommentList().getTotal()));

      if (review.getId() == fragment.getReviewId()) {
        index = count;
      }
      if (review.getCommentList() != null
          && review.getCommentList().getDatalist() != null
          && review.getCommentList().getDatalist().getLimit() != null) {
        fragment.createDisplayableComments(review.getCommentList().getDatalist().getList(),
            displayables);
        if (review.getCommentList().getDatalist().getList().size() > 2) {
          displayables.add(fragment.createReadMoreDisplayable(count, review));
        }
      }
      count++;
    }

    // Hammered to fix layout not visible on first call.
    if (fragment.getAdapter().getItemCount() == 0) {
      index = 0;
    }

    fragment.checkAndRemoveProgressBarDisplayable();
    fragment.addDisplayables(displayables);
    if (index >= 0) {
      fragment.getLayoutManager().scrollToPosition(fragment.getAdapter().getItemPosition(index));
    }
  }
}
