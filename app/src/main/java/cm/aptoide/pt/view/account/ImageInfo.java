package cm.aptoide.pt.view.account;

public class ImageInfo {
  private final int height;
  private final int width;
  private final long size;

  public ImageInfo(int height, int width, long size) {
    this.height = height;
    this.width = width;
    this.size = size;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public long getSize() {
    return size;
  }
}
