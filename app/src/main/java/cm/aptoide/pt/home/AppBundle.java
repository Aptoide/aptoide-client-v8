package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.view.app.Application;
import java.util.List;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class AppBundle implements HomeBundle {

  private final String title;
  private final List<Application> apps;
  private final BundleType type;
  private final Event event;
  private final String tag;

  public AppBundle(String title, List<Application> apps, BundleType type, Event event, String tag) {
    this.title = title;
    this.apps = apps;
    this.type = type;
    this.event = event;
    this.tag = tag;
  }

  public String getTitle() {
    return title;
  }

  @Override public List<?> getContent() {
    return apps;
  }

  @Override public BundleType getType() {
    return type;
  }

  @Override public Event getEvent() {
    return event;
  }

  @Override public String getTag() {
    return tag;
  }

  public List<Application> getApps() {
    return apps;
  }
}
