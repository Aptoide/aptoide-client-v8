package cm.aptoide.pt.timeline.post;

public class PostPreview {
  private final String url;
  private final String image;
  private final String title;

  PostPreview(String image, String title, String url) {
    this.url = url;
    this.image = image;
    this.title = title;
  }

  public String getUrl() {
    return url;
  }

  public String getImage() {
    return image;
  }

  public String getTitle() {
    return title;
  }
}
