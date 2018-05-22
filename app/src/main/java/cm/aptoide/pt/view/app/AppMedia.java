package cm.aptoide.pt.view.app;

import java.util.List;

/**
 * Created by D01 on 21/05/2018.
 */

public class AppMedia {
  private final String description;
  private final List<String> keywords;
  private final String news;
  private final List<AppScreenshot> appScreenshots;
  private final List<AppVideo> appVideos;

  public AppMedia(String description, List<String> keywords, String news,
      List<AppScreenshot> screenshots, List<AppVideo> videos) {
    this.description = description;
    this.keywords = keywords;
    this.news = news;
    this.appScreenshots = screenshots;
    this.appVideos = videos;
  }

  public List<AppVideo> getVideos() {
    return appVideos;
  }

  public List<AppScreenshot> getScreenshots() {
    return appScreenshots;
  }

  public String getNews() {
    return news;
  }

  public List<String> getKeywords() {
    return keywords;
  }

  public String getDescription() {
    return description;
  }
}
