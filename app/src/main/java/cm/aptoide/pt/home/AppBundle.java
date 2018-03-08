package cm.aptoide.pt.home;

import cm.aptoide.pt.view.app.Application;
import java.util.List;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class AppBundle {

  private final String title;
  private final List<Application> apps;

  public AppBundle(String title, List<Application> apps) {
    this.title = title;
    this.apps = apps;
  }

  public String getTitle() {
    return title;
  }

  public List<Application> getApps() {
    return apps;
  }
}
