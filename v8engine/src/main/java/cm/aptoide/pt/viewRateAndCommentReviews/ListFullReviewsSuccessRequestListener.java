package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.ListReviews;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class ListFullReviewsSuccessRequestListener implements SuccessRequestListener<ListReviews> {

  private static final String TAG = ListFullReviewsSuccessRequestListener.class.getName();

  private final RateAndReviewsFragment fragment;
  private final AptoideClientUUID aptoideClientUUID;

  ListFullReviewsSuccessRequestListener(RateAndReviewsFragment fragment) {
    this.fragment = fragment;

    aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext());
  }

  @Override public void call(ListReviews listFullReviews) {

    List<Review> reviews = listFullReviews.getDatalist().getList();
    List<Displayable> displayables = new LinkedList<>();
    final String aptoideClientUuid = aptoideClientUUID.getAptoideClientUUID();

    Observable.from(reviews)
        .flatMap(review -> ListCommentsRequest.of( // fetch the list of comments for each review
            review.getComments().getView(),
            review.getId(),
            3,
            StoreUtils.getStoreCredentials(fragment.getStoreName()),
            AptoideAccountManager.getAccessToken(),
            aptoideClientUuid, true
        ).observe().subscribeOn(Schedulers.io()) // parallel I/O split point
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
          CrashReports.logException(err);
          Logger.e(TAG, err);
        });
  }

  private void addRateAndReviewDisplayables(List<Review> reviews, List<Displayable> displayables) {
    int index = -1;
    int count = 0;
    for (final Review review : reviews) {
      displayables.add(
          new RateAndReviewCommentDisplayable(new ReviewWithAppName(fragment.getAppName(), review),
              new ConcreteItemCommentAdder(count, fragment, review)));

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
    fragment.checkAndRemoveProgressBarDisplayable();
    fragment.addDisplayables(displayables);
    if (index >= 0) {
      fragment.getLayoutManager().scrollToPosition(fragment.getAdapter().getItemPosition(index));
    }
  }
}
