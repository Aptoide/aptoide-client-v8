package cm.aptoide.pt.view;

import android.app.Activity;
import android.content.res.TypedArray;

/**
 * Created by pedroribeiro on 23/06/16.
 */
public class ThemeUtils {

  private static int getStatusColor(Activity activity, int resId) {
    TypedArray a =
        activity.obtainStyledAttributes(resId, new int[] { android.R.attr.statusBarColor });
    int color = a.getColor(0, 0);
    a.recycle();
    return color;
  }
}
