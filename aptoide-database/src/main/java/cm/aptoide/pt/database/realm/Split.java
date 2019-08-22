package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Split extends RealmObject {
  @PrimaryKey private String md5;
  private String link;
  private String type;
  private String name;
  private long fileSize;

  public Split() {
  }

  public Split(String md5, String link, String type, String name, long fileSize) {
    this.md5 = md5;
    this.link = link;
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

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
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
