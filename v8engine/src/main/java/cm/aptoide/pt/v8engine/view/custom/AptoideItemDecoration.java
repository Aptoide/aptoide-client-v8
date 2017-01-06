package cm.aptoide.pt.v8engine.view.custom;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import cm.aptoide.pt.v8engine.R;

public class AptoideItemDecoration extends RecyclerView.ItemDecoration {

  private final Context context;

  public AptoideItemDecoration(Context context) {
    this.context = context;
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    int offset = 0;
    int top = 0;
    int bottom = 0;
    int left = 0;
    int right = 0;
    if (context != null) {
      offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
          context.getResources().getDisplayMetrics());
      top = offset;
      bottom = offset;
      left = offset;
      right = offset;
    }

    if (view.getId() == R.id.brick_app_item) {
      top = offset / 2;
      bottom = offset / 2;
      left = offset / 2;
      right = offset / 2;
    }

    if (view.getId() == R.id.timeline_stats_layout) {
      top = 0;
      left = 0;
      right = 0;
      bottom = offset;
    }

    if (view.getId() == R.id.timeline_login_layout) {
      top = 0;
      left = 0;
      right = 0;
      bottom = offset;
    }

    if (view.getId() == R.id.message_white_bg) {
      top = 0;
      left = offset;
      right = offset;
      bottom = offset;
    }

    outRect.set(left, top, right, bottom);
  }
}
