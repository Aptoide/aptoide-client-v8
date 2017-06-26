package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.link.Link;
import java.util.Date;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class Media implements Card {
  private final String cardId;
  private final String mediaTitle;
  private final String mediaThumbnailUrl;
  private final Date date;
  private final App relatedApp;
  private final String abTestURL;
  private final String publisherAvatarURL;
  private final String publisherName;
  private final Link publisherLink;
  private final Link mediaLink;
  private final CardType cardType;

  public Media(String cardId, String mediaTitle, String mediaThumbnailUrl, Date date, App app,
      String abTestURL, String publisherAvatarURL, String publisherName, Link publisherLink,
      Link mediaLink, CardType cardType) {
    this.cardId = cardId;
    this.mediaTitle = mediaTitle;
    this.mediaThumbnailUrl = mediaThumbnailUrl;
    this.date = date;
    this.relatedApp = app;
    this.abTestURL = abTestURL;
    this.publisherLink = publisherLink;
    this.publisherAvatarURL = publisherAvatarURL;
    this.publisherName = publisherName;
    this.mediaLink = mediaLink;
    this.cardType = cardType;
  }

  public String getCardId() {
    return cardId;
  }

  @Override public CardType getType() {
    return this.cardType;
  }

  public String getMediaTitle() {
    return mediaTitle;
  }

  public String getMediaThumbnailUrl() {
    return mediaThumbnailUrl;
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

  public Link getMediaLink() {
    return mediaLink;
  }
}
