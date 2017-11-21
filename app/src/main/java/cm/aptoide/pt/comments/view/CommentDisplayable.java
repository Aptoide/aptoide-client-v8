package cm.aptoide.pt.comments.view;

import android.support.design.widget.Snackbar;
import android.view.View;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.store.view.StoreFragment;
import cm.aptoide.pt.view.FragmentProvider;
import cm.aptoide.pt.view.recycler.displayable.Displayable;

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
          .getId(), "DEFAULT", StoreFragment.OpenType.GetHome), true);
    } else {
      Snackbar.make(view, R.string.stores_message_private_user, Snackbar.LENGTH_SHORT)
          .show();
    }
  }
}
