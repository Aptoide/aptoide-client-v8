package cm.aptoide.pt.dataprovider.ws.v7.post;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessDataListResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class RelatedAppResponse
    extends BaseV7EndlessDataListResponse<RelatedAppResponse.RelatedApp> {

  public static class RelatedApp {
    private long id;
    private String name;
    @JsonProperty("package") private String packageName;
    private String icon;

    public RelatedApp() {
    }

    public long getId() {
      return this.id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public String getName() {
      return this.name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPackageName() {
      return this.packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    public String getIcon() {
      return this.icon;
    }

    public void setIcon(String icon) {
      this.icon = icon;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final long $id = this.getId();
      result = result * PRIME + (int) ($id >>> 32 ^ $id);
      final Object $name = this.getName();
      result = result * PRIME + ($name == null ? 43 : $name.hashCode());
      final Object $packageName = this.getPackageName();
      result = result * PRIME + ($packageName == null ? 43 : $packageName.hashCode());
      final Object $icon = this.getIcon();
      result = result * PRIME + ($icon == null ? 43 : $icon.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof RelatedApp)) return false;
      final RelatedApp other = (RelatedApp) o;
      if (!other.canEqual(this)) return false;
      if (this.getId() != other.getId()) return false;
      final Object this$name = this.getName();
      final Object other$name = other.getName();
      if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
      final Object this$packageName = this.getPackageName();
      final Object other$packageName = other.getPackageName();
      if (this$packageName == null ? other$packageName != null
          : !this$packageName.equals(other$packageName)) {
        return false;
      }
      final Object this$icon = this.getIcon();
      final Object other$icon = other.getIcon();
      return this$icon == null ? other$icon == null : this$icon.equals(other$icon);
    }

    public String toString() {
      return "RelatedAppResponse.RelatedApp(id="
          + this.getId()
          + ", name="
          + this.getName()
          + ", packageName="
          + this.getPackageName()
          + ", icon="
          + this.getIcon()
          + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof RelatedApp;
    }
  }
}
