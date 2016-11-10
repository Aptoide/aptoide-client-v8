package cm.aptoide.pt.aptoidesdk.entities;

import cm.aptoide.pt.model.v7.GetApp;
import lombok.Data;

/**
 * Created by neuro on 21-10-2016.
 */
@Data
public class App {

  private final long id;
  private final String name;
  private final String packageName;
  private final String iconPath;
  private final String featuredGraphicPath;

  private final int vercode;
  private final String vername;

  private final Store store;
  private final File file;

  public static App fromGetApp(GetApp getApp) {

    long id = getApp.getNodes().getMeta().getData().getId();
    String name = getApp.getNodes().getMeta().getData().getName();
    String packageName = getApp.getNodes().getMeta().getData().getPackageName();
    String iconPath = getApp.getNodes().getMeta().getData().getIcon();
    String featuredGraphicPath = getApp.getNodes().getMeta().getData().getGraphic();

    int vercode = getApp.getNodes().getMeta().getData().getFile().getVercode();
    String vername = getApp.getNodes().getMeta().getData().getFile().getVername();

    Store store = Store.from(getApp.getNodes().getMeta().getData().getStore());
    File file = File.from(getApp.getNodes().getMeta().getData().getFile());

    return new App(id, name, packageName, iconPath, featuredGraphicPath, vercode, vername, store,
        file);
  }
}
