package cm.aptoide.pt.home;

import cm.aptoide.pt.view.app.Application;

/**
 * Created by jdandrade on 16/04/2018.
 */

public class AppHomeClick extends HomeClick {
  private final Application app;

  public AppHomeClick(Application app, HomeBundle bundle, int position, Type clickType) {
    super(bundle, position, clickType);
    this.app = app;
  }

  public Application getApp() {
    return app;
  }
}
