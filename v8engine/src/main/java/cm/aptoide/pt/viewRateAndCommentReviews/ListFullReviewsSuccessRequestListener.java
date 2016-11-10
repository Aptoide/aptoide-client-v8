package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.ListReviews;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.adapters.ReviewsAndCommentsAdapter;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class ListFullReviewsSuccessRequestListener implements SuccessRequestListener<ListReviews> {

  private static final String TAG = ListFullReviewsSuccessRequestListener.class.getName();

  private final RateAndReviewsFragment fragment;

  ListFullReviewsSuccessRequestListener(RateAndReviewsFragment fragment) {
    this.fragment = fragment;
  }

  @Override public void call(ListReviews listFullReviews) {

    List<Review> reviews = listFullReviews.getDatalist().getList();
    List<Displayable> displayables = new LinkedList<>();
    final String aptoideClientUuid = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext()).getAptoideClientUUID();

    Observable.from(reviews).flatMap(
        review ->
          ListCommentsRequest.of( // fetch the list of comments for each review
              review.getComments().getView(),
              review.getId(),
              3,
              fragment.storeName,
              StoreUtils.getStoreCredentials(fragment.storeName),
              AptoideAccountManager.getAccessToken(),
              AptoideAccountManager.getUserEmail(),
              aptoideClientUuid
          ).observe().subscribeOn(Schedulers.io()).map( // parallel I/O split point
              listComments -> {
                review.setCommentList(listComments);
                return review;
              }
          )
    ).toList().observeOn(AndroidSchedulers.mainThread()).subscribe(reviewList->{
      // parallel I/O merge point
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
          new RateAndReviewCommentDisplayable(new ReviewWithAppName(fragment.appName, review),
              new CommentAdder(count) {
                @Override public void addComment(List<Comment> comments) {
                  List<Displayable> displayableList = new ArrayList<>();
                  fragment.createDisplayableComments(comments, displayableList);
                  int reviewPosition = fragment.getAdapter().getReviewPosition(reviewIndex);
                  if (comments.size() > 2) {
                    displayableList.add(fragment.createReadMoreDisplayable(reviewPosition, review));
                  }
                  fragment.getAdapter().addDisplayables(reviewPosition + 1, displayableList);
                }

                @Override public void collapseComments() {
                  ReviewsAndCommentsAdapter adapter = fragment.getAdapter();
                  int reviewIndex = adapter.getReviewPosition(this.reviewIndex);
                  int nextReview = adapter.getReviewPosition(this.reviewIndex + 1);
                  nextReview = nextReview == -1 ? fragment.getAdapter().getItemCount() : nextReview;
                  adapter.removeDisplayables(reviewIndex + 1, nextReview
                      - 1); // the -1 because we don't want to remove the next review,only until
                  // the
                  // comment
                  // before the review
                }
              }));

      if (review.getId() == fragment.reviewId) {
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
      fragment.getLayoutManager().scrollToPosition(fragment.getAdapter().getReviewPosition(index));
    }
  }
}
