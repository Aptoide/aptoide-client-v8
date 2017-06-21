/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.model.v7.listapp;

import cm.aptoide.pt.model.v7.Malware;
import lombok.Getter;
import lombok.Setter;

/**
 * Class used on an App item TODO: Incomplete
 */
public class File {

  @Getter @Setter private String vername;
  @Getter @Setter private int vercode;
  @Getter @Setter private String md5sum;
  private String path;
  private String pathAlt;
  @Getter @Setter private long filesize;
  @Getter @Setter private Malware malware;

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

  public String getPathAlt() {
    return pathAlt;
  }

  public void setPathAlt(String pathAlt) {
    if (pathAlt != null) {
      this.pathAlt = pathAlt.replaceFirst("pool", "zerorating");
    } else {
      this.pathAlt = null;
    }
  }
}
