package cm.aptoide.pt.comment;

import android.os.Bundle;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.commentdetail.CommentDetailFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.store.view.StoreFragment;

public class CommentsNavigator {
  private final FragmentNavigator fragmentNavigator;

  public CommentsNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToCommentView(Comment comment, long storeId) {
    CommentDetailFragment fragment = new CommentDetailFragment();
    Bundle args = new Bundle();
    args.putLong("comment_id", comment.getId());
    args.putString("comment_message", comment.getMessage());
    args.putLong("comment_user_id", comment.getUser()
        .getId());
    args.putString("comment_user_avatar", comment.getUser()
        .getAvatar());
    args.putString("comment_user_name", comment.getUser()
        .getName());
    args.putInt("comment_replies_number", comment.getReplies());
    args.putSerializable("comment_timestamp", comment.getDate());
    args.putLong("store_id", storeId);
    fragment.setArguments(args);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateToStore(long id) {
    fragmentNavigator.navigateTo(
        StoreFragment.newInstance(id, null, StoreFragment.OpenType.GetHome), true);
  }
}
