package cm.aptoide.pt.spotandshare.socket.entities;

import java.io.File;
import java.io.Serializable;

/**
 * Created by neuro on 28-01-2017.
 */
public class FileInfo implements Serializable {

  protected String filePath;
  protected String fileName;
  protected long size;

  public FileInfo(File file) {
    this(file.getPath(), file.length());
  }

  public FileInfo(String filePath, long size) {
    setFilePath(filePath);
    this.size = size;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
    this.fileName = extractFileName(filePath);
  }

  private String extractFileName(String filePath) {
    return filePath.substring(filePath.lastIndexOf(File.separatorChar), filePath.length());
  }

  public FileInfo setParentDirectory(String parentDirectory) {
    if (parentDirectory.charAt(parentDirectory.length() - 1) != File.separatorChar) {
      filePath = parentDirectory + File.separatorChar + fileName;
    } else {
      filePath = parentDirectory + fileName;
    }

    return this;
  }

  public String getFilePath() {
    return this.filePath;
  }

  public String getFileName() {
    return this.fileName;
  }

  public long getSize() {
    return this.size;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof FileInfo)) return false;
    final FileInfo other = (FileInfo) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$filePath = this.getFilePath();
    final Object other$filePath = other.getFilePath();
    if (this$filePath == null ? other$filePath != null : !this$filePath.equals(other$filePath)) {
      return false;
    }
    final Object this$fileName = this.getFileName();
    final Object other$fileName = other.getFileName();
    if (this$fileName == null ? other$fileName != null : !this$fileName.equals(other$fileName)) {
      return false;
    }
    if (this.getSize() != other.getSize()) return false;
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $filePath = this.getFilePath();
    result = result * PRIME + ($filePath == null ? 43 : $filePath.hashCode());
    final Object $fileName = this.getFileName();
    result = result * PRIME + ($fileName == null ? 43 : $fileName.hashCode());
    final long $size = this.getSize();
    result = result * PRIME + (int) ($size >>> 32 ^ $size);
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof FileInfo;
  }

  public String toString() {
    return "FileInfo(filePath="
        + this.getFilePath()
        + ", fileName="
        + this.getFileName()
        + ", size="
        + this.getSize()
        + ")";
  }
}
