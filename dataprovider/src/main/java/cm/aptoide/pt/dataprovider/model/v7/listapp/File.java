/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.listapp;

import cm.aptoide.pt.dataprovider.model.v7.Malware;
import lombok.Data;

/**
 * Class used on an App item TODO: Incomplete
 */
@Data public class File {

  private String vername;
  private int vercode;
  private String md5sum;
  private String path;
  private String pathAlt;
  private long filesize;
  private Malware malware;
}
