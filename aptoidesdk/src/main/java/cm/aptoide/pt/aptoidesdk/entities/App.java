package cm.aptoide.pt.aptoidesdk.entities;

import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Data;

/**
 * Created by neuro on 21-10-2016.
 */
@Data public final class App {

  private final long id;
  private final String name;
  private final String packageName;
  private final String iconPath;
  private final String iconThumbnailPath;
  private final String featuredGraphicPath;

  private final int vercode;
  private final String vername;

  private final Media media;
  private final Developer developer;

  private final Store store;
  private final File file;
  private final Obb obb;

  public App(long id, String name, String packageName, String iconPath, String featuredGraphicPath,
      int vercode, String vername, Media media, Developer developer, Store store, File file,
      Obb obb) {
    this.id = id;
    this.name = name;
    this.packageName = packageName;
    this.iconPath = iconPath;
    this.iconThumbnailPath = AptoideUtils.IconSizeU.getNewImageUrl(iconPath);
    this.featuredGraphicPath = featuredGraphicPath;
    this.vercode = vercode;
    this.vername = vername;
    this.media = media;
    this.developer = developer;
    this.store = store;
    this.file = file;
    this.obb = obb;
  }
}
