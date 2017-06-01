package cm.aptoide.pt.v8engine.social;

import cm.aptoide.pt.model.v7.listapp.App;
import java.util.Date;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class Article {
  private final String cardId;
  private final String title;
  private final String thumbnailUrl;
  private final Date date;
  private final App relatedApp;
  private final String abTestURL;
  private final String publisherURL;
  private final String publisherAvatarURL;
  private final String publisherName;

  public Article(String cardId, String title, String thumbnailUrl, Date date, App app,
      String abTestURL, String publisherURL, String publisherAvatarURL, String publisherName) {

    this.cardId = cardId;
    this.title = title;
    this.thumbnailUrl = thumbnailUrl;
    this.date = date;
    this.relatedApp = app;
    this.abTestURL = abTestURL;
    this.publisherURL = publisherURL;
    this.publisherAvatarURL = publisherAvatarURL;
    this.publisherName = publisherName;
  }

  public String getCardId() {
    return cardId;
  }

  public String getTitle() {
    return title;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public Date getDate() {
    return date;
  }

  public App getRelatedApp() {
    return relatedApp;
  }

  public String getAbTestURL() {
    return abTestURL;
  }

  public String getPublisherURL() {
    return publisherURL;
  }

  public String getPublisherAvatarURL() {
    return publisherAvatarURL;
  }

  public String getPublisherName() {
    return publisherName;
  }
}
