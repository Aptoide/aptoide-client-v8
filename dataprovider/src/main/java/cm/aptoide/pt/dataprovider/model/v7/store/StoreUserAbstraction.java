package cm.aptoide.pt.dataprovider.model.v7.store;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;

/**
 * Created by neuro on 21-09-2017.
 */

public abstract class StoreUserAbstraction<T extends BaseV7Response> extends BaseV7Response {

  private Nodes<T> nodes;

  public StoreUserAbstraction() {
  }

  public Nodes<T> getNodes() {
    return this.nodes;
  }

  public void setNodes(Nodes<T> nodes) {
    this.nodes = nodes;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $nodes = this.getNodes();
    result = result * PRIME + ($nodes == null ? 43 : $nodes.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof StoreUserAbstraction;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof StoreUserAbstraction)) return false;
    final StoreUserAbstraction other = (StoreUserAbstraction) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$nodes = this.getNodes();
    final Object other$nodes = other.getNodes();
    if (this$nodes == null ? other$nodes != null : !this$nodes.equals(other$nodes)) return false;
    return true;
  }

  public String toString() {
    return "StoreUserAbstraction(nodes=" + this.getNodes() + ")";
  }

  public static class Nodes<T extends BaseV7Response> {
    private T meta;
    private GetStoreTabs tabs;
    private GetStoreWidgets widgets;

    public Nodes() {
    }

    public T getMeta() {
      return this.meta;
    }

    public void setMeta(T meta) {
      this.meta = meta;
    }

    public GetStoreTabs getTabs() {
      return this.tabs;
    }

    public void setTabs(GetStoreTabs tabs) {
      this.tabs = tabs;
    }

    public GetStoreWidgets getWidgets() {
      return this.widgets;
    }

    public void setWidgets(GetStoreWidgets widgets) {
      this.widgets = widgets;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $meta = this.getMeta();
      result = result * PRIME + ($meta == null ? 43 : $meta.hashCode());
      final Object $tabs = this.getTabs();
      result = result * PRIME + ($tabs == null ? 43 : $tabs.hashCode());
      final Object $widgets = this.getWidgets();
      result = result * PRIME + ($widgets == null ? 43 : $widgets.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof StoreUserAbstraction.Nodes)) return false;
      final Nodes other = (Nodes) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$meta = this.getMeta();
      final Object other$meta = other.getMeta();
      if (this$meta == null ? other$meta != null : !this$meta.equals(other$meta)) return false;
      final Object this$tabs = this.getTabs();
      final Object other$tabs = other.getTabs();
      if (this$tabs == null ? other$tabs != null : !this$tabs.equals(other$tabs)) return false;
      final Object this$widgets = this.getWidgets();
      final Object other$widgets = other.getWidgets();
      if (this$widgets == null ? other$widgets != null : !this$widgets.equals(other$widgets)) {
        return false;
      }
      return true;
    }

    public String toString() {
      return "StoreUserAbstraction.Nodes(meta="
          + this.getMeta()
          + ", tabs="
          + this.getTabs()
          + ", widgets="
          + this.getWidgets()
          + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof StoreUserAbstraction.Nodes;
    }
  }
}
