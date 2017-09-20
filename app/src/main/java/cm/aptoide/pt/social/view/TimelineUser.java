package cm.aptoide.pt.social.view;

import android.support.annotation.Nullable;
import cm.aptoide.pt.social.data.CardType;
import cm.aptoide.pt.social.data.DummyPost;

/**
 * Created by trinkes on 05/09/2017.
 */

public class TimelineUser extends DummyPost {
  private final String notificationBody;
  private final String notificationImage;
  private final String notificationUrlAction;
  private final int notificationId;
  private final long followers;
  private final long following;
  private final boolean hasStats;
  private final boolean isLogged;
  private final boolean hasNotification;
  @Nullable private final String analyticsUrl;
  private CardType cardType;
  public TimelineUser(boolean isLogged, boolean hasNotification, String notificationBody,
      String notificationImage, String notificationUrlAction, int notificationId, boolean hasStats,
      long followers, long following, @Nullable String analyticsUrl) {
    this.isLogged = isLogged;
    this.notificationBody = notificationBody;
    this.notificationImage = notificationImage;
    this.notificationUrlAction = notificationUrlAction;
    this.notificationId = notificationId;
    this.followers = followers;
    this.following = following;
    this.hasStats = hasStats;
    this.hasNotification = hasNotification;
    this.analyticsUrl = analyticsUrl;
  }

  @Nullable public String getAnalyticsUrl() {
    return analyticsUrl;
  }

  public int getNotificationId() {
    return notificationId;
  }

  public String getNotificationBody() {
    return notificationBody;
  }

  public String getNotificationImage() {
    return notificationImage;
  }

  public long getFollowers() {
    return followers;
  }

  public long getFollowing() {
    return following;
  }

  public CardType getCardType() {
    return cardType;
  }

  public void setCardType(CardType cardType) {
    this.cardType = cardType;
  }

  @Override public String getCardId() {
    throw new RuntimeException(this.getClass()
        .getSimpleName() + "  card have NO card id");
  }

  @Override public CardType getType() {
    return cardType;
  }

  public String getNotificationUrlAction() {
    return notificationUrlAction;
  }

  public boolean hasUserStats() {
    return hasStats;
  }

  public boolean isLoggedIn() {
    return isLogged;
  }

  public boolean hasNotification() {
    return hasNotification;
  }
}
