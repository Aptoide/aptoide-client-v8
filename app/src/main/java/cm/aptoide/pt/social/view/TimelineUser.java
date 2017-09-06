package cm.aptoide.pt.social.view;

import cm.aptoide.pt.social.data.CardType;
import cm.aptoide.pt.social.data.DummyPost;

/**
 * Created by trinkes on 05/09/2017.
 */

public class TimelineUser extends DummyPost {
  private final String bodyMessage;
  private final String image;
  private final String urlAction;
  private final int notificationType;
  private final long followers;
  private final long following;
  private CardType cardType;

  public TimelineUser(CardType cardType, String bodyMessage, String image, String urlAction,
      int notificationType, long followers, long following) {
    this.cardType = cardType;
    this.bodyMessage = bodyMessage;
    this.image = image;
    this.urlAction = urlAction;
    this.notificationType = notificationType;
    this.followers = followers;
    this.following = following;
  }

  public Integer[] getNotificationType() {
    return new Integer[] { notificationType };
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
}
