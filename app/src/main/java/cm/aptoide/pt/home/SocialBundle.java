package cm.aptoide.pt.home;

import android.support.annotation.DrawableRes;
import cm.aptoide.pt.R;
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
  private final int drawableId;
  private final String userIcon;
  private final String name;

  public SocialBundle(List<Application> apps, BundleType type, Event event, String tag,
      String userIcon, String name) {
    this.title = "n/a";
    this.apps = apps;
    this.type = type;
    this.event = event;
    this.tag = tag;
    this.userIcon = userIcon;
    this.name = name;
    this.drawableId = R.drawable.placeholder_square;
  }

  public SocialBundle(List<Application> apps, BundleType type, Event event, String tag,
      @DrawableRes int drawableId, String name) {
    this.title = "n/a";
    this.apps = apps;
    this.type = type;
    this.event = event;
    this.tag = tag;
    this.drawableId = drawableId;
    this.name = name;
    this.userIcon = "";
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
    return name;
  }

  public int getDrawableId() {
    return drawableId;
  }

  public boolean hasAvatar() {
    return userIcon != null && !userIcon.isEmpty();
  }
}
