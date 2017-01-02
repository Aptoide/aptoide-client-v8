package cm.aptoide.pt.v8engine.view.custom;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import cm.aptoide.pt.v8engine.R;

public class HorizontalGraySeparator extends DrawableItemDecoration {
  public HorizontalGraySeparator(Context context) {
    super(ContextCompat.getDrawable(context, R.drawable.gray_item_decorator), 35);
  }
}
