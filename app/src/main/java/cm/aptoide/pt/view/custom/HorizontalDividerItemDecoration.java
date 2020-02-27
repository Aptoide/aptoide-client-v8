package cm.aptoide.pt.view.custom;

import android.content.Context;
import androidx.core.content.ContextCompat;
import cm.aptoide.pt.R;
import cm.aptoide.pt.themes.ThemeManager;

public class HorizontalDividerItemDecoration extends DrawableItemDecoration {

  public HorizontalDividerItemDecoration(Context context, ThemeManager themeManager) {
    this(context, 0, themeManager);
  }

  public HorizontalDividerItemDecoration(Context context, int customHorizontalPadding,
      ThemeManager themeManager) {
    super(ContextCompat.getDrawable(context,
        themeManager.getAttributeForTheme(R.attr.backgroundSecondary).resourceId),
        customHorizontalPadding);
  }
}
