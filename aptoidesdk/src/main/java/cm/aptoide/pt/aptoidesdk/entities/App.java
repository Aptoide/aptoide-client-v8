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

  private final Media media;
  private final Developer developer;

  private final Store store;
  private final File file;
  private final Obb obb;

  public static App fromGetApp(GetApp getApp) {

    long id = getApp.getNodes().getMeta().getData().getId();
    String name = getApp.getNodes().getMeta().getData().getName();
    String packageName = getApp.getNodes().getMeta().getData().getPackageName();
    String iconPath = getApp.getNodes().getMeta().getData().getIcon();
    String featuredGraphicPath = getApp.getNodes().getMeta().getData().getGraphic();

    int vercode = getApp.getNodes().getMeta().getData().getFile().getVercode();
    String vername = getApp.getNodes().getMeta().getData().getFile().getVername();

    Media media = Media.fromGetAppMetaMedia(getApp.getNodes().getMeta().getData().getMedia());
    Developer developer =
        Developer.fromGetAppDeveloper(getApp.getNodes().getMeta().getData().getDeveloper());

    Store store = Store.from(getApp.getNodes().getMeta().getData().getStore());
    File file = File.from(getApp.getNodes().getMeta().getData().getFile());
    Obb obb = Obb.from(getApp.getNodes().getMeta().getData().getObb());

    return new App(id, name, packageName, iconPath, featuredGraphicPath, vercode, vername, media,
        developer, store, file, obb);
  }
}
