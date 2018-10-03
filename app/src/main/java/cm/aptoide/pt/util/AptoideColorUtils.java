package cm.aptoide.pt.util;

import android.support.v4.graphics.ColorUtils;

/**
 * Created by D01 on 27/09/2018.
 */

public class AptoideColorUtils {

  public static int getChangedColorLightness(float[] hsl, float factor) {
    float luminance = hsl[2] * factor;
    luminance = luminance < 0 ? 0 : luminance;
    luminance = luminance > 1 ? 1 : luminance;
    hsl[2] = luminance;
    return ColorUtils.HSLToColor(hsl);
  }
}
