/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.store;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import java.util.List;

/**
 * Created by neuro on 22-04-2016.
 */
public class GetStoreDisplays extends BaseV7Response {

  private List<EventImage> list;

  public GetStoreDisplays() {
  }

  public List<EventImage> getList() {
    return this.list;
  }

  public void setList(List<EventImage> list) {
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
    return other instanceof GetStoreDisplays;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetStoreDisplays)) return false;
    final GetStoreDisplays other = (GetStoreDisplays) o;
    if (!other.canEqual(this)) return false;
    if (!super.equals(o)) return false;
    final Object this$list = this.getList();
    final Object other$list = other.getList();
    return this$list == null ? other$list == null : this$list.equals(other$list);
  }

  public String toString() {
    return "GetStoreDisplays(list=" + this.getList() + ")";
  }

  public static class EventImage {

    private String label;
    private String graphic;
    private Event event;

    public EventImage() {
    }

    public String getLabel() {
      return this.label;
    }

    public void setLabel(String label) {
      this.label = label;
    }

    public String getGraphic() {
      return this.graphic;
    }

    public void setGraphic(String graphic) {
      this.graphic = graphic;
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
      final Object $graphic = this.getGraphic();
      result = result * PRIME + ($graphic == null ? 43 : $graphic.hashCode());
      final Object $event = this.getEvent();
      result = result * PRIME + ($event == null ? 43 : $event.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof EventImage)) return false;
      final EventImage other = (EventImage) o;
      if (!other.canEqual(this)) return false;
      final Object this$label = this.getLabel();
      final Object other$label = other.getLabel();
      if (this$label == null ? other$label != null : !this$label.equals(other$label)) return false;
      final Object this$graphic = this.getGraphic();
      final Object other$graphic = other.getGraphic();
      if (this$graphic == null ? other$graphic != null : !this$graphic.equals(other$graphic)) {
        return false;
      }
      final Object this$event = this.getEvent();
      final Object other$event = other.getEvent();
      return this$event == null ? other$event == null : this$event.equals(other$event);
    }

    public String toString() {
      return "GetStoreDisplays.EventImage(label="
          + this.getLabel()
          + ", graphic="
          + this.getGraphic()
          + ", event="
          + this.getEvent()
          + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof EventImage;
    }
  }
}
