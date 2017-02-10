package cm.aptoide.pt.aptoidesdk.misc;

/**
 * Created by neuro on 22-11-2016.
 */
public enum Orientation {
  portrait, landscape;

  public static Orientation getOrientation(int height, int width) {
    return height > width ? portrait : landscape;
  }
}
