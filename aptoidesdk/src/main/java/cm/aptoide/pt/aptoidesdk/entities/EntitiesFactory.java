package cm.aptoide.pt.aptoidesdk.entities;

import android.support.annotation.Nullable;
import android.util.Base64;
import cm.aptoide.pt.aptoidesdk.misc.SdkUtils;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.ListSearchApps;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 25-11-2016.
 */
public class EntitiesFactory {

  public static App createApp(GetApp getApp) {

    long id = getApp.getNodes().getMeta().getData().getId();
    String name = getApp.getNodes().getMeta().getData().getName();
    String packageName = getApp.getNodes().getMeta().getData().getPackageName();
    String iconPath = getApp.getNodes().getMeta().getData().getIcon();
    String featuredGraphicPath = getApp.getNodes().getMeta().getData().getGraphic();

    int vercode = getApp.getNodes().getMeta().getData().getFile().getVercode();
    String vername = getApp.getNodes().getMeta().getData().getFile().getVername();

    Media media = createMedia(getApp.getNodes().getMeta().getData().getMedia());
    Developer developer = createDeveloper(getApp.getNodes().getMeta().getData().getDeveloper());

    Store store = createStore(getApp.getNodes().getMeta().getData().getStore());
    File file = createFile(getApp.getNodes().getMeta().getData().getFile());
    Obb obb = createObb(getApp.getNodes().getMeta().getData().getObb());

    return new App(id, name, packageName, iconPath, featuredGraphicPath, vercode, vername, media,
        developer, store, file, obb);
  }

  public static Developer createDeveloper(GetAppMeta.Developer developer) {

    String name = developer.getName();
    String website = developer.getWebsite();
    String email = developer.getEmail();

    return new Developer(name, website, email);
  }

  public static File createFile(GetAppMeta.GetAppMetaFile file) {

    String path = file.getPath() + getQueryString();
    String alternativePath = file.getPathAlt() + getQueryString();
    long size = file.getFilesize();
    String md5sum = file.getMd5sum();

    return new File(path, alternativePath, size, md5sum);
  }

  private static String getQueryString() {
    return "?" + Base64.encodeToString(SdkUtils.FileParameters.getDownloadQueryString().getBytes(),
        0).replace("=", "").replace("/", "*").replace("+", "_").replace("\n", "");
  }

  public static Media createMedia(GetAppMeta.Media getAppMetaMedia) {

    List<Screenshot> screenshots = new LinkedList<>();
    String description = getAppMetaMedia.getDescription();

    if (getAppMetaMedia.getScreenshots() != null) {
      for (GetAppMeta.Media.Screenshot screenshot : getAppMetaMedia.getScreenshots()) {
        screenshots.add(createScreenShot(screenshot));
      }
    }

    return new Media(description, screenshots);
  }

  public @Nullable
  static Screenshot createScreenShot(GetAppMeta.Media.Screenshot getAppMetaMediaScreenshot) {

    if (getAppMetaMediaScreenshot != null) {

      String url = getAppMetaMediaScreenshot.getUrl();
      int height = getAppMetaMediaScreenshot.getHeight();
      int width = getAppMetaMediaScreenshot.getWidth();

      return new Screenshot(url, height, width);
    }

    return null;
  }

  public static Obb createObb(cm.aptoide.pt.model.v7.Obb obb) {

    if (!containsObb(obb)) {
      return null;
    }

    return new Obb(createObbFile(obb.getMain()), createObbFile(obb.getPatch()));
  }

  private static boolean containsObb(cm.aptoide.pt.model.v7.Obb obb) {

    return obb != null && (obb.getMain() != null || obb.getPatch() != null);
  }

  public static ObbFile createObbFile(@Nullable cm.aptoide.pt.model.v7.Obb.ObbItem obbItem) {

    if (obbItem == null) {
      return null;
    }

    String path = obbItem.getPath();
    String md5sum = obbItem.getMd5sum();
    String filename = obbItem.getFilename();
    long filesize = obbItem.getFilesize();

    return new ObbFile(path, md5sum, filename, filesize);
  }

  public static SearchResult createSearchResult(ListSearchApps.SearchAppsApp searchAppsApp) {

    long id = searchAppsApp.getId();
    String name = searchAppsApp.getName();
    String packageName = searchAppsApp.getPackageName();
    long size = searchAppsApp.getSize();
    String iconPath = searchAppsApp.getIcon();
    String storeName = searchAppsApp.getStore().getName();
    long downloads = searchAppsApp.getStats().getDownloads();

    return new SearchResult(id, name, packageName, size, iconPath, storeName, downloads);
  }

  public static Store createStore(cm.aptoide.pt.model.v7.store.Store store) {

    long id = store.getId();
    String name = store.getName();
    String avatarPath = store.getAvatar();

    return new Store(id, name, avatarPath);
  }
}
