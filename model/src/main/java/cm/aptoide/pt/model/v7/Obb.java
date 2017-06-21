/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.model.v7;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Class containing the extra Obb file. http://ws2.aptoide.com/api/7/getApp/app_id/12966861
 */
@Data public class Obb {

  private ObbItem patch;
  private ObbItem main;

  public static class ObbItem {
    private String path;
    @Getter @Setter private String md5sum;
    @Getter @Setter private long filesize;
    @Getter @Setter private String filename;

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      if (path != null) {
        this.path = path.replaceFirst("pool", "zerorating");
      } else {
        this.path = null;
      }
    }
  }
}
