/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Class containing the extra Obb file. http://ws2.aptoide.com/api/7/getApp/app_id/12966861
 */
public class Obb {

  private ObbItem patch;
  private ObbItem main;

  public Obb() {
  }

  public ObbItem getPatch() {
    return this.patch;
  }

  public void setPatch(ObbItem patch) {
    this.patch = patch;
  }

  public ObbItem getMain() {
    return this.main;
  }

  public void setMain(ObbItem main) {
    this.main = main;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $patch = this.getPatch();
    result = result * PRIME + ($patch == null ? 43 : $patch.hashCode());
    final Object $main = this.getMain();
    result = result * PRIME + ($main == null ? 43 : $main.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof Obb;
  }

  public static class ObbItem {

    private String path;
    private String md5sum;
    private long filesize;
    private String filename;

    public ObbItem() {
    }

    public String getPath() {
      return this.path;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public String getMd5sum() {
      return this.md5sum;
    }

    public void setMd5sum(String md5sum) {
      this.md5sum = md5sum;
    }

    public long getFilesize() {
      return this.filesize;
    }

    public void setFilesize(long filesize) {
      this.filesize = filesize;
    }

    public String getFilename() {
      return this.filename;
    }

    public void setFilename(String filename) {
      this.filename = filename;
    }

    protected boolean canEqual(Object other) {
      return other instanceof ObbItem;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof ObbItem)) return false;
      final ObbItem other = (ObbItem) o;
      if (!other.canEqual(this)) return false;
      final Object this$path = this.getPath();
      final Object other$path = other.getPath();
      if (this$path == null ? other$path != null : !this$path.equals(other$path)) return false;
      final Object this$md5sum = this.getMd5sum();
      final Object other$md5sum = other.getMd5sum();
      if (this$md5sum == null ? other$md5sum != null : !this$md5sum.equals(other$md5sum)) {
        return false;
      }
      if (this.getFilesize() != other.getFilesize()) return false;
      final Object this$filename = this.getFilename();
      final Object other$filename = other.getFilename();
      return this$filename == null ? other$filename == null : this$filename.equals(other$filename);
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $path = this.getPath();
      result = result * PRIME + ($path == null ? 43 : $path.hashCode());
      final Object $md5sum = this.getMd5sum();
      result = result * PRIME + ($md5sum == null ? 43 : $md5sum.hashCode());
      final long $filesize = this.getFilesize();
      result = result * PRIME + (int) ($filesize >>> 32 ^ $filesize);
      final Object $filename = this.getFilename();
      result = result * PRIME + ($filename == null ? 43 : $filename.hashCode());
      return result;
    }

    public String toString() {
      return "Obb.ObbItem(path="
          + this.getPath()
          + ", md5sum="
          + this.getMd5sum()
          + ", filesize="
          + this.getFilesize()
          + ", filename="
          + this.getFilename()
          + ")";
    }
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Obb)) return false;
    final Obb other = (Obb) o;
    if (!other.canEqual(this)) return false;
    final Object this$patch = this.getPatch();
    final Object other$patch = other.getPatch();
    if (this$patch == null ? other$patch != null : !this$patch.equals(other$patch)) return false;
    final Object this$main = this.getMain();
    final Object other$main = other.getMain();
    return this$main == null ? other$main == null : this$main.equals(other$main);
  }

  public String toString() {
    return "Obb(patch=" + this.getPatch() + ", main=" + this.getMain() + ")";
  }
}
