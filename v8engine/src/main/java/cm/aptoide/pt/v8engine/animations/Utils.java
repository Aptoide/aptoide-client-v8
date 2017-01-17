package cm.aptoide.pt.v8engine.animations;

public class Utils {
  public static double mapValueFromRangeToRange(double value, double fromLow, double fromHigh,
      double toLow, double toHigh) {
    return toLow + ((value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow));
  }

  public static double clamp(double value, double low, double high) {
    return Math.min(Math.max(value, low), high);
  }
}
