package cm.aptoide.pt.aptoidesdk.entities;

import cm.aptoide.pt.model.v7.GetApp;

/**
 * Created by neuro on 21-10-2016.
 */

public class App {

  private String name;

  public static App fromGetApp(GetApp getApp) {
    App app = new App();

    app.name = getApp.getNodes().getMeta().getData().getName();

    return app;
  }
}
