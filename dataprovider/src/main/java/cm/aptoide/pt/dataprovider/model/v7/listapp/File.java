/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.listapp;

import cm.aptoide.pt.dataprovider.model.v7.Malware;

/**
 * Class used on an App item TODO: Incomplete
 */
public class File {

  private String vername;
  private int vercode;
  private String md5sum;
  private String path;
  private String pathAlt;
  private long filesize;
  private Malware malware;

  public File() {
  }

  public String getVername() {
    return this.vername;
  }

  public void setVername(String vername) {
    this.vername = vername;
  }

  public int getVercode() {
    return this.vercode;
  }

  public void setVercode(int vercode) {
    this.vercode = vercode;
  }

  public String getMd5sum() {
    return this.md5sum;
  }

  public void setMd5sum(String md5sum) {
    this.md5sum = md5sum;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPathAlt() {
    return this.pathAlt;
  }

  public void setPathAlt(String pathAlt) {
    this.pathAlt = pathAlt;
  }

  public long getFilesize() {
    return this.filesize;
  }

  public void setFilesize(long filesize) {
    this.filesize = filesize;
  }

  public Malware getMalware() {
    return this.malware;
  }

  public void setMalware(Malware malware) {
    this.malware = malware;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $vername = this.getVername();
    result = result * PRIME + ($vername == null ? 43 : $vername.hashCode());
    result = result * PRIME + this.getVercode();
    final Object $md5sum = this.getMd5sum();
    result = result * PRIME + ($md5sum == null ? 43 : $md5sum.hashCode());
    final Object $path = this.getPath();
    result = result * PRIME + ($path == null ? 43 : $path.hashCode());
    final Object $pathAlt = this.getPathAlt();
    result = result * PRIME + ($pathAlt == null ? 43 : $pathAlt.hashCode());
    final long $filesize = this.getFilesize();
    result = result * PRIME + (int) ($filesize >>> 32 ^ $filesize);
    final Object $malware = this.getMalware();
    result = result * PRIME + ($malware == null ? 43 : $malware.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof File)) return false;
    final File other = (File) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$vername = this.getVername();
    final Object other$vername = other.getVername();
    if (this$vername == null ? other$vername != null : !this$vername.equals(other$vername)) {
      return false;
    }
    if (this.getVercode() != other.getVercode()) return false;
    final Object this$md5sum = this.getMd5sum();
    final Object other$md5sum = other.getMd5sum();
    if (this$md5sum == null ? other$md5sum != null : !this$md5sum.equals(other$md5sum)) {
      return false;
    }
    final Object this$path = this.getPath();
    final Object other$path = other.getPath();
    if (this$path == null ? other$path != null : !this$path.equals(other$path)) return false;
    final Object this$pathAlt = this.getPathAlt();
    final Object other$pathAlt = other.getPathAlt();
    if (this$pathAlt == null ? other$pathAlt != null : !this$pathAlt.equals(other$pathAlt)) {
      return false;
    }
    if (this.getFilesize() != other.getFilesize()) return false;
    final Object this$malware = this.getMalware();
    final Object other$malware = other.getMalware();
    if (this$malware == null ? other$malware != null : !this$malware.equals(other$malware)) {
      return false;
    }
    return true;
  }

  public String toString() {
    return "File(vername="
        + this.getVername()
        + ", vercode="
        + this.getVercode()
        + ", md5sum="
        + this.getMd5sum()
        + ", path="
        + this.getPath()
        + ", pathAlt="
        + this.getPathAlt()
        + ", filesize="
        + this.getFilesize()
        + ", malware="
        + this.getMalware()
        + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof File;
  }
}
