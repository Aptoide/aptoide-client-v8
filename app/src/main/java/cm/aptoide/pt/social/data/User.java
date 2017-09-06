package cm.aptoide.pt.social.data;

/**
 * Created by trinkes on 05/09/2017.
 */

public class User {
  public static final int NO_FOLLOWERS = -1;
  public static final int NO_FOLLOWINGS = -1;
  public static final int NO_TYPE = -1;
  private final String bodyMessage;
  private final String image;
  private final String urlAction;
  private final int type;
  private final long followers;
  private final long following;
  private final CardType cardType;

  public User(String bodyMessage, String image, String urlAction, int type, CardType cardType) {
    this.bodyMessage = bodyMessage;
    this.image = image;
    this.urlAction = urlAction;
    this.type = type;
    this.cardType = cardType;
    followers = NO_FOLLOWERS;
    following = NO_FOLLOWINGS;
  }

  public User(CardType cardType) {
    this.cardType = cardType;
    bodyMessage = null;
    image = null;
    urlAction = null;
    type = NO_TYPE;
    followers = NO_FOLLOWERS;
    following = NO_FOLLOWINGS;
  }

  public User(long followers, long following, CardType cardType) {
    this.followers = followers;
    this.following = following;
    this.cardType = cardType;
    bodyMessage = null;
    image = null;
    urlAction = null;
    type = NO_TYPE;
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

  public CardType getCardType() {
    return cardType;
  }

  public int getType() {
    return type;
  }

  public boolean hasNotification() {
    return bodyMessage != null && image != null && urlAction != null;
  }
}
