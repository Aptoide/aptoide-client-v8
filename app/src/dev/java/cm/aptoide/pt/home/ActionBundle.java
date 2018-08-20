package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v7.Event;
import java.util.Collections;
import java.util.List;

class ActionBundle implements HomeBundle {
  private final String title;
  private final BundleType type;
  private final Event event;
  private final String tag;

  ActionBundle(String title, BundleType type, Event event, String tag) {
    this.title = title;
    this.type = type;
    this.event = event;
    this.tag = tag;
  }

  @Override public String getTitle() {
    return this.title;
  }

  @Override public List<?> getContent() {
    return Collections.emptyList();
  }

  @Override public BundleType getType() {
    return this.type;
  }

  @Override public Event getEvent() {
    return this.event;
  }

  @Override public String getTag() {
    return this.tag;
  }
}
