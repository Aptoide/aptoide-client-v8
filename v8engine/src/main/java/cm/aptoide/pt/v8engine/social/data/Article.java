package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.link.Link;
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
  private final String publisherAvatarURL;
  private final String publisherName;
  private final Link publisherLink;
  private final Link articleLink;

  public Article(String cardId, String title, String thumbnailUrl, Date date, App app,
      String abTestURL, String publisherAvatarURL, String publisherName, Link publisherLink,
      Link articleLink) {
    this.cardId = cardId;
    this.title = title;
    this.thumbnailUrl = thumbnailUrl;
    this.date = date;
    this.relatedApp = app;
    this.abTestURL = abTestURL;
    this.publisherLink = publisherLink;
    this.publisherAvatarURL = publisherAvatarURL;
    this.publisherName = publisherName;
    this.articleLink = articleLink;
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

  public String getPublisherAvatarURL() {
    return publisherAvatarURL;
  }

  public String getPublisherName() {
    return publisherName;
  }

  public Link getPublisherLink() {
    return publisherLink;
  }

  public Link getArticleLink() {
    return articleLink;
  }
}
