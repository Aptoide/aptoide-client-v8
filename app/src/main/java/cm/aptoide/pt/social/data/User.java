package cm.aptoide.pt.social.data;

import cm.aptoide.pt.social.TimelineUserProvider;

/**
 * Created by trinkes on 05/09/2017.
 */

public class User {
  public static final int NO_FOLLOWERS = -1;
  public static final int NO_FOLLOWINGS = -1;
  private final String bodyMessage;
  private final String image;
  private final String urlAction;
  private final TimelineUserProvider.NotificationType type;
  private final long followers;
  private final long following;
  private boolean isLogged;

  public User(String bodyMessage, String image, String urlAction,
      TimelineUserProvider.NotificationType type, boolean isLogged) {
    this.bodyMessage = bodyMessage;
    this.image = image;
    this.urlAction = urlAction;
    this.type = type;
    this.isLogged = isLogged;
    followers = NO_FOLLOWERS;
    following = NO_FOLLOWINGS;
  }

  public User(boolean isLogged) {
    this.isLogged = isLogged;
    bodyMessage = null;
    image = null;
    urlAction = null;
    type = null;
    followers = NO_FOLLOWERS;
    following = NO_FOLLOWINGS;
  }

  public User(long followers, long following, boolean isLogged) {
    this.followers = followers;
    this.following = following;
    this.isLogged = isLogged;
    bodyMessage = null;
    image = null;
    urlAction = null;
    type = null;
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

  public TimelineUserProvider.NotificationType getType() {
    return type;
  }

  public boolean isLogged() {
    return isLogged;
  }
}
