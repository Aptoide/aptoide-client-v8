/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.store;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import java.util.List;

/**
 * Created by hsousa on 17/09/15.
 */
public class GetStoreTabs extends BaseV7Response {

  private List<Tab> list;

  public GetStoreTabs() {
  }

  public List<Tab> getList() {
    return this.list;
  }

  public void setList(List<Tab> list) {
    this.list = list;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $list = this.getList();
    result = result * PRIME + ($list == null ? 43 : $list.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetStoreTabs;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetStoreTabs)) return false;
    final GetStoreTabs other = (GetStoreTabs) o;
    if (!other.canEqual(this)) return false;
    if (!super.equals(o)) return false;
    final Object this$list = this.getList();
    final Object other$list = other.getList();
    return this$list == null ? other$list == null : this$list.equals(other$list);
  }

  public String toString() {
    return "GetStoreTabs(list=" + this.getList() + ")";
  }

  public static class Tab {

    private String label;
    private String tag;
    private Event event;

    public Tab() {
    }

    public String getLabel() {
      return this.label;
    }

    public void setLabel(String label) {
      this.label = label;
    }

    public String getTag() {
      return this.tag;
    }

    public void setTag(String tag) {
      this.tag = tag;
    }

    public Event getEvent() {
      return this.event;
    }

    public void setEvent(Event event) {
      this.event = event;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $label = this.getLabel();
      result = result * PRIME + ($label == null ? 43 : $label.hashCode());
      final Object $tag = this.getTag();
      result = result * PRIME + ($tag == null ? 43 : $tag.hashCode());
      final Object $event = this.getEvent();
      result = result * PRIME + ($event == null ? 43 : $event.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Tab)) return false;
      final Tab other = (Tab) o;
      if (!other.canEqual(this)) return false;
      final Object this$label = this.getLabel();
      final Object other$label = other.getLabel();
      if (this$label == null ? other$label != null : !this$label.equals(other$label)) return false;
      final Object this$tag = this.getTag();
      final Object other$tag = other.getTag();
      if (this$tag == null ? other$tag != null : !this$tag.equals(other$tag)) return false;
      final Object this$event = this.getEvent();
      final Object other$event = other.getEvent();
      return this$event == null ? other$event == null : this$event.equals(other$event);
    }

    public String toString() {
      return "GetStoreTabs.Tab(label="
          + this.getLabel()
          + ", tag="
          + this.getTag()
          + ", event="
          + this.getEvent()
          + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof Tab;
    }
  }
}
