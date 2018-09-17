package cm.aptoide.pt.reactions.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by KenZira on 3/18/17.
 */

public class DisplayUtil {

  public static int dpToPx(int dp) {
    DisplayMetrics metrics = Resources.getSystem()
        .getDisplayMetrics();
    return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
  }
}
