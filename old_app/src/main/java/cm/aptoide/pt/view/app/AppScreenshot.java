package cm.aptoide.pt.view.app;

/**
 * Created by D01 on 21/05/2018.
 */

public class AppScreenshot {
  private final int height;
  private final int width;
  private final String orientation;
  private final String url;

  public AppScreenshot(int height, int width, String orientation, String url) {
    this.height = height;
    this.width = width;
    this.orientation = orientation;
    this.url = url;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public String getOrientation() {
    return orientation;
  }

  public String getUrl() {
    return url;
  }
}
