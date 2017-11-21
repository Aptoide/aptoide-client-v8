package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by trinkes on 11/30/16.
 */

public class MyStore extends BaseV7Response {

  GetStoreWidgets widgets;

  public MyStore() {
  }

  public GetStoreWidgets getWidgets() {
    return this.widgets;
  }

  public void setWidgets(GetStoreWidgets widgets) {
    this.widgets = widgets;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof MyStore)) return false;
    final MyStore other = (MyStore) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$widgets = this.getWidgets();
    final Object other$widgets = other.getWidgets();
    if (this$widgets == null ? other$widgets != null : !this$widgets.equals(other$widgets)) {
      return false;
    }
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $widgets = this.getWidgets();
    result = result * PRIME + ($widgets == null ? 43 : $widgets.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof MyStore;
  }

  public String toString() {
    return "MyStore(widgets=" + this.getWidgets() + ")";
  }
}
