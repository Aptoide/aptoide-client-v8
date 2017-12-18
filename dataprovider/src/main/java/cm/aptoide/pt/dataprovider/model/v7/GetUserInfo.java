package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by pedroribeiro on 01/06/17.
 *
 * This pojo is returned by a new request to user/get with two of it's nodes (meta and settings)
 * It is called GetUserInfo because this is replacing the old getUserInfo request and pojo and a
 * GetUserRequest already existed.
 */

public class GetUserInfo extends BaseV7Response {

  private Nodes nodes;

  public GetUserInfo() {
  }

  public Nodes getNodes() {
    return this.nodes;
  }

  public void setNodes(Nodes nodes) {
    this.nodes = nodes;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $nodes = this.getNodes();
    result = result * PRIME + ($nodes == null ? 43 : $nodes.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetUserInfo;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetUserInfo)) return false;
    final GetUserInfo other = (GetUserInfo) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$nodes = this.getNodes();
    final Object other$nodes = other.getNodes();
    if (this$nodes == null ? other$nodes != null : !this$nodes.equals(other$nodes)) return false;
    return true;
  }

  public String toString() {
    return "GetUserInfo(nodes=" + this.getNodes() + ")";
  }

  public static class Nodes {
    private GetUserMeta meta;
    private GetUserSettings settings;

    public Nodes() {
    }

    public GetUserMeta getMeta() {
      return this.meta;
    }

    public void setMeta(GetUserMeta meta) {
      this.meta = meta;
    }

    public GetUserSettings getSettings() {
      return this.settings;
    }

    public void setSettings(GetUserSettings settings) {
      this.settings = settings;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $meta = this.getMeta();
      result = result * PRIME + ($meta == null ? 43 : $meta.hashCode());
      final Object $settings = this.getSettings();
      result = result * PRIME + ($settings == null ? 43 : $settings.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Nodes)) return false;
      final Nodes other = (Nodes) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$meta = this.getMeta();
      final Object other$meta = other.getMeta();
      if (this$meta == null ? other$meta != null : !this$meta.equals(other$meta)) return false;
      final Object this$settings = this.getSettings();
      final Object other$settings = other.getSettings();
      if (this$settings == null ? other$settings != null : !this$settings.equals(other$settings)) {
        return false;
      }
      return true;
    }

    public String toString() {
      return "GetUserInfo.Nodes(meta=" + this.getMeta() + ", settings=" + this.getSettings() + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof Nodes;
    }
  }
}
