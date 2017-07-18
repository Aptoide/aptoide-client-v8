package cm.aptoide.pt.v8engine.view.comments;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.configuration.FragmentProvider;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by trinkes on 8/4/16.
 */
public class CommentDisplayable extends Displayable {

  private final Comment comment;
  private FragmentNavigator fragmentNavigator;
  private FragmentProvider fragmentProvider;

  public CommentDisplayable(Comment comment, FragmentNavigator fragmentNavigator,
      FragmentProvider fragmentProvider) {
    this.comment = comment;
    this.fragmentNavigator = fragmentNavigator;
    this.fragmentProvider = fragmentProvider;
  }

  public CommentDisplayable() {
    this.comment = null;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.comment_layout;
  }

  public Comment getComment() {
    return comment;
  }

  public void itemClicked() {
    //TODO AN-1492 - revert - waiting for api change
    //fragmentNavigator.navigateTo(fragmentProvider.newStoreFragment(comment.getUser()
    //    .getId(), "DEFAULT", StoreFragment.OpenType.GetHome));
  }
}
