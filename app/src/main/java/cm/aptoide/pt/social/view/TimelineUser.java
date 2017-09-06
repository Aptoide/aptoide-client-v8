package cm.aptoide.pt.social.view;

import cm.aptoide.pt.social.TimelineUserProvider;
import cm.aptoide.pt.social.data.CardType;
import cm.aptoide.pt.social.data.DummyPost;

/**
 * Created by trinkes on 05/09/2017.
 */

public class TimelineUser extends DummyPost {
  private final String bodyMessage;
  private final String image;
  private final String urlAction;
  private final TimelineUserProvider.NotificationType notificationType;
  private final long followers;
  private final long following;
  private final boolean hasStats;
  private CardType cardType;
  private boolean isLogged;

  public TimelineUser(boolean isLogged, String bodyMessage, String image, String urlAction,
      TimelineUserProvider.NotificationType notificationType, long followers, long following,
      boolean hasStats) {
    this.isLogged = isLogged;
    this.bodyMessage = bodyMessage;
    this.image = image;
    this.urlAction = urlAction;
    this.notificationType = notificationType;
    this.followers = followers;
    this.following = following;
    this.hasStats = hasStats;
  }

  public TimelineUserProvider.NotificationType getNotificationType() {
    return notificationType;
  }

  public String getBodyMessage() {
    return bodyMessage;
  }

  public String getImage() {
    return image;
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

  public String getUrlAction() {
    return urlAction;
  }

  public boolean hasUserStats() {
    return hasStats;
  }

  public boolean isLoggedIn() {
    return isLogged;
  }

  public boolean hasNotification() {
    return bodyMessage != null && urlAction != null && image != null;
  }
}
