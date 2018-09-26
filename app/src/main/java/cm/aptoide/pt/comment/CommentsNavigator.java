package cm.aptoide.pt.comment;

import android.widget.Toast;
import cm.aptoide.pt.navigator.FragmentNavigator;

public class CommentsNavigator {
  private final FragmentNavigator fragmentNavigator;

  public CommentsNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToCommentView(Long commentId) {
    // TODO: 26/09/2018 navigate to comment view
    Toast.makeText(fragmentNavigator.getFragment()
        .getContext(), "COMMENT CLICKED " + commentId, Toast.LENGTH_SHORT)
        .show();
  }
}
