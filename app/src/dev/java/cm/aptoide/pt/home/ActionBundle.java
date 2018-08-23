package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v7.Event;
import java.util.Collections;
import java.util.List;

public class ActionBundle implements HomeBundle {
  private final String title;
  private final BundleType type;
  private final Event event;
  private final String tag;
  private final ActionItem actionItem;

  public ActionBundle(String title, BundleType type, Event event, String tag, ActionItem actionItem) {
    this.title = title;
    this.type = type;
    this.event = event;
    this.tag = tag;
    this.actionItem = actionItem;
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

  public ActionItem getActionItem() {
    return actionItem;
  }
}
