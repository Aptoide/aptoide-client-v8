package cm.aptoide.pt.spotandshare.socket.entities;

import java.io.File;
import java.io.Serializable;
import lombok.Data;

/**
 * Created by neuro on 28-01-2017.
 */
@Data public class FileInfo implements Serializable {

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
}
