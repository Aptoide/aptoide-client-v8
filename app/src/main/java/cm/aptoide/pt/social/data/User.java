package cm.aptoide.pt.social.data;

/**
 * Created by trinkes on 05/09/2017.
 */

public class User {
  public static final int NO_FOLLOWERS = -1;
  public static final int NO_FOLLOWINGS = -1;
  public static final int NO_NOTIFICATION_ID = -1;
  private final String bodyMessage;
  private final String image;
  private final String urlAction;
  private final int notificationId;
  private final long followers;
  private final long following;
  private final String analyticsUrl;
  private boolean isLogged;

  public User(String bodyMessage, String image, String urlAction, int notificationId,
      boolean isLogged, String analyticsUrl) {
    this.bodyMessage = bodyMessage;
    this.image = image;
    this.urlAction = urlAction;
    this.notificationId = notificationId;
    this.isLogged = isLogged;
    this.analyticsUrl = analyticsUrl;
    followers = NO_FOLLOWERS;
    following = NO_FOLLOWINGS;
  }

  public User(boolean isLogged) {
    this.isLogged = isLogged;
    bodyMessage = null;
    image = null;
    urlAction = null;
    notificationId = NO_NOTIFICATION_ID;
    followers = NO_FOLLOWERS;
    following = NO_FOLLOWINGS;
    analyticsUrl = null;
  }

  public User(long followers, long following, boolean isLogged) {
    this.followers = followers;
    this.following = following;
    this.isLogged = isLogged;
    bodyMessage = null;
    image = null;
    urlAction = null;
    notificationId = NO_NOTIFICATION_ID;
    analyticsUrl = null;
  }

  public long getFollowers() {
    return followers;
  }

  public long getFollowing() {
    return following;
  }

  public String getBodyMessage() {
    return bodyMessage;
  }

  public String getImage() {
    return image;
  }

  public String getUrlAction() {
    return urlAction;
  }

  public int getNotificationId() {
    return notificationId;
  }

  public boolean isLogged() {
    return isLogged;
  }

  public boolean hasNotification() {
    return bodyMessage != null
        && image != null
        && urlAction != null
        && notificationId != NO_NOTIFICATION_ID;
  }

  public boolean hasStats() {
    return getFollowers() != User.NO_FOLLOWERS && getFollowing() != User.NO_FOLLOWINGS;
  }

  public String getAnalyticsUrl() {
    return analyticsUrl;
  }
}
