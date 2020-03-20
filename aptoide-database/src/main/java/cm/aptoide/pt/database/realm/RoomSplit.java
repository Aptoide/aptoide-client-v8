package cm.aptoide.pt.database.realm;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "split") public class RoomSplit {
  @PrimaryKey @NonNull private String md5;
  private String path;
  private String type;
  private String name;
  private long fileSize;

  public RoomSplit(String md5, String path, String type, String name, long fileSize) {
    this.md5 = md5;
    this.path = path;
    this.type = type;
    this.name = name;
    this.fileSize = fileSize;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }
}
