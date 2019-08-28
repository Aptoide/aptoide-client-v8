package cm.aptoide.pt.view.custom;

import android.content.Context;
import androidx.core.content.ContextCompat;
import cm.aptoide.pt.R;

public class HorizontalDividerItemDecoration extends DrawableItemDecoration {

  public HorizontalDividerItemDecoration(Context context) {
    this(context, 0);
  }

  public HorizontalDividerItemDecoration(Context context, int customHorizontalPadding) {
    super(ContextCompat.getDrawable(context, R.drawable.gray_item_decorator),
        customHorizontalPadding);
  }
}
