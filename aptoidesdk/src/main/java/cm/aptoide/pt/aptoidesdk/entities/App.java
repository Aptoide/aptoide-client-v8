package cm.aptoide.pt.aptoidesdk.entities;

import lombok.Data;

/**
 * Created by neuro on 21-10-2016.
 */
@Data public final class App {

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
}
