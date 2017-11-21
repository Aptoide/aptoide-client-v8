package cm.aptoide.pt.dataprovider.model.v7.timeline;

/**
 * Created by jdandrade on 31/01/2017.
 */
public class SocialCardStats {
  private long likes;
  private long comments;

  public SocialCardStats() {
  }

  public long getLikes() {
    return this.likes;
  }

  public void setLikes(long likes) {
    this.likes = likes;
  }

  public long getComments() {
    return this.comments;
  }

  public void setComments(long comments) {
    this.comments = comments;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final long $likes = this.getLikes();
    result = result * PRIME + (int) ($likes >>> 32 ^ $likes);
    final long $comments = this.getComments();
    result = result * PRIME + (int) ($comments >>> 32 ^ $comments);
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SocialCardStats)) return false;
    final SocialCardStats other = (SocialCardStats) o;
    if (!other.canEqual((Object) this)) return false;
    if (this.getLikes() != other.getLikes()) return false;
    if (this.getComments() != other.getComments()) return false;
    return true;
  }

  public String toString() {
    return "SocialCardStats(likes=" + this.getLikes() + ", comments=" + this.getComments() + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof SocialCardStats;
  }
}
