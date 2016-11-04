package cm.aptoide.pt.aptoidesdk.entities;

import cm.aptoide.pt.model.v7.GetApp;
import lombok.Data;

/**
 * Created by neuro on 21-10-2016.
 */
@Data
public class App {

  private long id;
  private String name;
  private String packageName;
  private String iconPath;
  private String featuredGraphicPath;

  private int vername;
  private String vercode;

  private Store store;
  private File file;

  public static App fromGetApp(GetApp getApp) {
    App app = new App();

    // TODO: 04-11-2016 neuro 
    app.name = getApp.getNodes().getMeta().getData().getName();

    return app;
  }
}
