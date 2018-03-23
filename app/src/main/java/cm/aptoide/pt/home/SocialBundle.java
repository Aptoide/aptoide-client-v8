package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.view.app.Application;
import java.util.List;

/**
 * Created by jdandrade on 23/03/2018.
 */

public class SocialBundle implements HomeBundle {
  private final String title;
  private final List<Application> apps;
  private final BundleType type;
  private final Event event;
  private final String tag;
  private final String userIcon;
  private final String userName;

  public SocialBundle(String title, List<Application> apps, BundleType type, Event event,
      String tag, String userIcon, String userName) {
    this.title = title;
    this.apps = apps;
    this.type = type;
    this.event = event;
    this.tag = tag;
    this.userIcon = userIcon;
    this.userName = userName;
  }

  @Override public String getTitle() {
    return this.title;
  }

  @Override public List<?> getContent() {
    return this.apps;
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

  public String getUserIcon() {
    return userIcon;
  }

  public String getUserName() {
    return userName;
  }
}
