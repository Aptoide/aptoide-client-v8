/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import lombok.Data;

/**
 * Class containing the extra Obb file. http://ws2.aptoide.com/api/7/getApp/app_id/12966861
 */
@Data public class Obb {

  private ObbItem patch;
  private ObbItem main;

  @Data public static class ObbItem {

    private String path;
    private String md5sum;
    private long filesize;
    private String filename;
  }
}
