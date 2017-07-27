package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.link.Link;
import cm.aptoide.pt.v8engine.social.data.publisher.Publisher;
import java.util.Date;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class Media implements Post {
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
  private boolean isLiked;
  private boolean likeFromClick;

  public Media(String cardId, String mediaTitle, String mediaThumbnailUrl, Date date, App app,
      String abTestURL, Publisher publisher, Link publisherLink, Link mediaLink, boolean isLiked,
      CardType cardType) {
    this.cardId = cardId;
    this.mediaTitle = mediaTitle;
    this.mediaThumbnailUrl = mediaThumbnailUrl;
    this.date = date;
    this.relatedApp = app;
    this.abTestURL = abTestURL;
    this.publisherLink = publisherLink;
    this.publisherAvatarURL = publisher.getPublisherAvatar()
        .getAvatarUrl();
    this.publisherName = publisher.getPublisherName();
    this.mediaLink = mediaLink;
    this.isLiked = isLiked;
    this.cardType = cardType;
  }

  public String getCardId() {
    return cardId;
  }

  @Override public CardType getType() {
    return this.cardType;
  }

  public String getAbUrl() {
    return abTestURL;
  }

  @Override public boolean isLiked() {
    return isLiked;
  }

  @Override public void setLiked(boolean liked) {
    this.isLiked = liked;
    this.likeFromClick = true;
  }

  @Override public boolean isLikeFromClick() {
    return likeFromClick;
  }

  public void setLikedFromClick(boolean likeFromClick) {
    this.likeFromClick = likeFromClick;
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
