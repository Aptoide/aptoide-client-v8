/*
 * Copyright (c) 2016.
 * Modified on 09/08/2016.
 */

package cm.aptoide.pt.v8engine.view.comments;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.comments.CommentAdder;
import cm.aptoide.pt.v8engine.comments.ReviewWithAppName;
import cm.aptoide.pt.v8engine.view.configuration.FragmentProvider;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Getter;

public class RateAndReviewCommentDisplayable extends DisplayablePojo<ReviewWithAppName> {

  @Getter private CommentAdder commentAdder;
  @Getter private int numberComments;
  private FragmentNavigator fragmentNavigator;
  private FragmentProvider fragmentProvider;

  public RateAndReviewCommentDisplayable() {
  }

  public RateAndReviewCommentDisplayable(ReviewWithAppName pojo) {
    super(pojo);
  }

  public RateAndReviewCommentDisplayable(ReviewWithAppName pojo, CommentAdder commentAdder,
      int numberComments, FragmentNavigator fragmentNavigator, FragmentProvider fragmentProvider) {
    super(pojo);
    this.commentAdder = commentAdder;
    this.numberComments = numberComments;
    this.fragmentNavigator = fragmentNavigator;
    this.fragmentProvider = fragmentProvider;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_rate_and_review;
  }

  public void itemClicked() {
    //TODO AN-1492 - revert - waiting for api change
    //fragmentNavigator.navigateTo(fragmentProvider.newStoreFragment(getPojo().getReview()
    //    .getUser()
    //    .getId(), "DEFAULT", StoreFragment.OpenType.GetHome));
  }
}
