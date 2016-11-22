/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.aptoidesdk.entities;

import android.support.annotation.Nullable;
import lombok.Data;

@Data public class Obb {

  private final ObbFile main;
  private final ObbFile patch;

  public static Obb from(cm.aptoide.pt.model.v7.Obb obb) {

    if (!containsObb(obb)) {
      return null;
    }

    return new Obb(ObbFile.from(obb.getMain()), ObbFile.from(obb.getPatch()));
  }

  private static boolean containsObb(cm.aptoide.pt.model.v7.Obb obb) {

    return obb != null && (obb.getMain() != null || obb.getPatch() != null);
  }

  @Data public static class ObbFile {

    private final String path;
    private final String md5sum;
    private final String filename;
    private final long filesize;

    public static ObbFile from(@Nullable cm.aptoide.pt.model.v7.Obb.ObbItem obbItem) {

      if (obbItem == null) {
        return null;
      }

      String path = obbItem.getPath();
      String md5sum = obbItem.getMd5sum();
      String filename = obbItem.getFilename();
      long filesize = obbItem.getFilesize();

      return new ObbFile(path, md5sum, filename, filesize);
    }
  }
}
