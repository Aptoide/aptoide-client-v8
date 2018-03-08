package cm.aptoide.pt.home;

import cm.aptoide.pt.view.app.Application;
import java.util.List;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class AppBundle {

  private final String title;
  private final List<Application> apps;
  private final BundleType type;

  public AppBundle(String title, List<Application> apps, BundleType type) {
    this.title = title;
    this.apps = apps;
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public List<Application> getApps() {
    return apps;
  }

  public BundleType getType() {
    return type;
  }

  public enum BundleType {
    EDITORS, APPS, STORE
  }
}
