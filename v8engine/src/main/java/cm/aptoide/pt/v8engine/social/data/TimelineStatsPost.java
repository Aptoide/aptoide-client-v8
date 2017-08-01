package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 05/07/2017.
 */

public class TimelineStatsPost implements Post {
  private final long followers;
  private final long following;
  private final CardType cardType;

  public TimelineStatsPost(long followers, long following, CardType cardType) {
    this.followers = followers;
    this.following = following;
    this.cardType = cardType;
  }

  public long getFollowers() {
    return followers;
  }

  public long getFollowing() {
    return following;
  }

  @Override public String getCardId() {
    throw new RuntimeException("Aggregated cards have NO card id");
  }

  @Override public CardType getType() {
    return cardType;
  }

  @Override public String getAbUrl() {
    //supposed to be null
    return null;
  }

  @Override public boolean isLiked() {
    return false;
  }

  @Override public void setLiked(boolean liked) {
    // do nothing
  }

  @Override public boolean isLikeFromClick() {
    return false;
  }
}
