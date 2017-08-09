package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialCard;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 26/06/2017.
 */

abstract class AppPost implements Post {
  protected final String cardId;
  protected final String appName;
  protected final String appIcon;
  protected final Date timestamp;
  protected final CardType cardType;
  protected final String packageName;
  protected final String abUrl;
  protected final long appId;
  protected final float appAverageRating;
  private final Long storeId;
  private List<SocialCard.CardComment> comments;
  private boolean isLiked;
  private boolean likedFromClick;

  AppPost(String cardId, String appIcon, String appName, long appId, String packageName,
      Date timestamp, String abUrl, CardType cardType, float appAverageRating, Long storeId,
      boolean isLiked) {
    this.abUrl = abUrl;
    this.cardId = cardId;
    this.timestamp = timestamp;
    this.appName = appName;
    this.appId = appId;
    this.packageName = packageName;
    this.cardType = cardType;
    this.appIcon = appIcon;
    this.appAverageRating = appAverageRating;
    this.storeId = storeId;
    this.isLiked = isLiked;
    comments = new ArrayList<>();
  }

  public Long getStoreId() {
    return storeId;
  }

  public float getAppAverageRating() {
    return appAverageRating;
  }

  public long getAppId() {
    return appId;
  }

  public String getAppName() {
    return appName;
  }

  public String getAppIcon() {
    return appIcon;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setLikedFromClick(boolean likedFromClick) {
    this.likedFromClick = likedFromClick;
  }

  @Override public String getCardId() {
    return this.cardId;
  }

  @Override public CardType getType() {
    return this.cardType;
  }

  @Override public String getAbUrl() {
    return abUrl;
  }

  @Override public boolean isLiked() {
    return isLiked;
  }

  @Override public void setLiked(boolean liked) {
    this.isLiked = liked;
    this.likedFromClick = true;
  }

  @Override public boolean isLikeFromClick() {
    return likedFromClick;
  }

  @Override public List<SocialCard.CardComment> getComments() {
    return comments;
  }

  @Override public long getCommentsNumber() {
    return comments.size();
  }

  @Override public void addComment(SocialCard.CardComment postComment) {
    comments.add(0, postComment);
  }
}
