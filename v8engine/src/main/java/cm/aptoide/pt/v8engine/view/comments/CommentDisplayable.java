package cm.aptoide.pt.v8engine.view.comments;

import android.support.design.widget.Snackbar;
import android.view.View;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.configuration.FragmentProvider;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.store.StoreFragment;

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

  public void itemClicked(View view) {
    if (comment.getUser()
        .getAccess() == Comment.Access.PUBLIC) {
      fragmentNavigator.navigateTo(fragmentProvider.newStoreFragment(comment.getUser()
          .getId(), "DEFAULT", StoreFragment.OpenType.GetHome));
    } else {
      Snackbar.make(view, R.string.stores_message_private_user, Snackbar.LENGTH_SHORT)
          .show();
    }
  }
}
