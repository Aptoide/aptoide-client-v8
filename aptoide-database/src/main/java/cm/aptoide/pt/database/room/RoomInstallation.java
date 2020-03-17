package cm.aptoide.pt.database.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "installation") public class RoomInstallation {

  public static final String PACKAGE_NAME = "packageName";

  @PrimaryKey @NonNull private String packageName;
  private String icon;
  private String name;
  private int versionCode;
  private String versionName;

  public RoomInstallation(String packageName, String name, String icon, int versionCode,
      String versionName) {
    this.packageName = packageName;
    this.name = name;
    this.icon = icon;
    this.versionCode = versionCode;
    this.versionName = versionName;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }

  public String getVersionName() {
    return versionName;
  }

  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }
}
