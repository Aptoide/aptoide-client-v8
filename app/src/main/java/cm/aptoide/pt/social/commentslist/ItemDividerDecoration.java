package cm.aptoide.pt.social.commentslist;

import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by jdandrade on 02/10/2017.
 */
class ItemDividerDecoration extends RecyclerView.ItemDecoration {
  private PostCommentsFragment postCommentsFragment;

  public ItemDividerDecoration(PostCommentsFragment postCommentsFragment) {
    this.postCommentsFragment = postCommentsFragment;
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    int offset;
    int top = 0;
    int bottom = 0;
    int left = 0;
    int right = 0;
    FragmentActivity activity = postCommentsFragment.getActivity();
    if (activity != null) {
      offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f,
          activity.getResources()
              .getDisplayMetrics());
      top = offset;
      bottom = offset;
      left = offset;
      right = offset;
    }
    outRect.set(left, top, right, bottom);
  }
}
