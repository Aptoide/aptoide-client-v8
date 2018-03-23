package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.view.app.Application;
import java.util.List;

/**
 * Created by jdandrade on 23/03/2018.
 */

public class TimelineBundle implements HomeBundle {
  private final String title;
  private final List<Application> apps;
  private final BundleType type;
  private final Event event;
  private final String tag;

  public TimelineBundle(String title, List<Application> apps, BundleType type, Event event,
      String tag) {
    this.title = title;
    this.apps = apps;
    this.type = type;
    this.event = event;
    this.tag = tag;
  }

  @Override public String getTitle() {
    return this.title;
  }

  @Override public List<?> getContent() {
    return this.apps;
  }

  @Override public BundleType getType() {
    return BundleType.TIMELINE;
  }

  @Override public Event getEvent() {
    return this.event;
  }

  @Override public String getTag() {
    return this.tag;
  }
}
