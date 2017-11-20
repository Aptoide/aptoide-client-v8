package cm.aptoide.pt.dataprovider.model.v7.timeline;

/**
 * Created by jdandrade on 01/02/2017.
 */
public class My {
  private boolean liked;

  public My() {
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + (this.isLiked() ? 79 : 97);
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof My)) return false;
    final My other = (My) o;
    if (!other.canEqual((Object) this)) return false;
    if (this.isLiked() != other.isLiked()) return false;
    return true;
  }

  public String toString() {
    return "My(liked=" + this.isLiked() + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof My;
  }

  public boolean isLiked() {
    return this.liked;
  }

  public void setLiked(boolean liked) {
    this.liked = liked;
  }
}
