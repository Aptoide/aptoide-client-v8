package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by trinkes on 15/12/2016.
 */

public class TimelineStats extends BaseV7Response {
  private StatusData data;

  public TimelineStats() {
  }

  public StatusData getData() {
    return this.data;
  }

  public void setData(StatusData data) {
    this.data = data;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $data = this.getData();
    result = result * PRIME + ($data == null ? 43 : $data.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof TimelineStats;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof TimelineStats)) return false;
    final TimelineStats other = (TimelineStats) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$data = this.getData();
    final Object other$data = other.getData();
    if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
    return true;
  }

  public String toString() {
    return "TimelineStats(data=" + this.getData() + ")";
  }

  public static class StatusData {
    private long followers;
    private long following;

    public StatusData() {
    }

    public long getFollowers() {
      return this.followers;
    }

    public void setFollowers(long followers) {
      this.followers = followers;
    }

    public long getFollowing() {
      return this.following;
    }

    public void setFollowing(long following) {
      this.following = following;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final long $followers = this.getFollowers();
      result = result * PRIME + (int) ($followers >>> 32 ^ $followers);
      final long $following = this.getFollowing();
      result = result * PRIME + (int) ($following >>> 32 ^ $following);
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof StatusData)) return false;
      final StatusData other = (StatusData) o;
      if (!other.canEqual((Object) this)) return false;
      if (this.getFollowers() != other.getFollowers()) return false;
      if (this.getFollowing() != other.getFollowing()) return false;
      return true;
    }

    public String toString() {
      return "TimelineStats.StatusData(followers="
          + this.getFollowers()
          + ", following="
          + this.getFollowing()
          + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof StatusData;
    }
  }
}
