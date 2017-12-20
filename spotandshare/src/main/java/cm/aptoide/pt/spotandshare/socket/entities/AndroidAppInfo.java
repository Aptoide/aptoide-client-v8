package cm.aptoide.pt.spotandshare.socket.entities;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 27-01-2017.
 */
public class AndroidAppInfo implements Serializable {

  private String appName;
  private String packageName;
  private List<FileInfo> fileInfos;
  private FileInfo apk, mainObb, patchObb;
  private byte[] icon;
  private Friend friend;

  public AndroidAppInfo(String appName, String packageName, File apk, File mainObb, File patchObb,
      byte[] icon, String senderName) {
    this.appName = appName;
    this.packageName = packageName;
    this.apk = new FileInfo(apk);

    if (mainObb != null) {
      this.mainObb = new FileInfo(mainObb);
    }

    if (patchObb != null) {
      this.patchObb = new FileInfo(patchObb);
    }

    this.icon = icon;

    fileInfos = buildFileInfos();
  }

  public AndroidAppInfo(String appName, String packageName, File apk, File mainObb) {
    this(appName, packageName, apk, mainObb, null, null, null);
  }

  public AndroidAppInfo(String appName, String packageName, File apk) {
    this(appName, packageName, apk, null, null, null, null);
  }

  @Deprecated public AndroidAppInfo(String appName, String packageName, List<FileInfo> fileInfos) {
    this.fileInfos = fileInfos;
    this.packageName = packageName;
    this.appName = appName;
    this.apk = extractApk();
  }

  private FileInfo extractApk() {
    for (int i = 0; i < fileInfos.size(); i++) {
      if (fileInfos.get(i)
          .getFilePath()
          .endsWith(".apk")) {
        return fileInfos.get(i);
      }
    }
    return null;
  }

  private List<FileInfo> buildFileInfos() {
    List<FileInfo> fileInfos = new LinkedList<>();

    fileInfos.add(apk);
    if (mainObb != null) {
      fileInfos.add(mainObb);
    }
    if (patchObb != null) {
      fileInfos.add(patchObb);
    }

    return Collections.unmodifiableList(fileInfos);
  }

  public long getFilesSize() {
    long total = 0;
    if (fileInfos != null) {
      for (int i = 0; i < fileInfos.size(); i++) {
        total += fileInfos.get(i)
            .getSize();
      }
    }
    return total;
  }

  public FileInfo getApk() {
    return this.apk;
  }

  public void setApk(FileInfo apk) {
    this.apk = apk;
  }

  public FileInfo getMainObb() {
    return this.mainObb;
  }

  public void setMainObb(FileInfo mainObb) {
    this.mainObb = mainObb;
  }

  public FileInfo getPatchObb() {
    return this.patchObb;
  }

  public void setPatchObb(FileInfo patchObb) {
    this.patchObb = patchObb;
  }

  public String getAppName() {
    return this.appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getPackageName() {
    return this.packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public List<FileInfo> getFileInfos() {
    return this.fileInfos;
  }

  public void setFileInfos(List<FileInfo> fileInfos) {
    this.fileInfos = fileInfos;
  }

  public byte[] getIcon() {
    return icon;
  }

  public void setIcon(byte[] icon) {
    this.icon = icon;
  }

  public Friend getFriend() {
    return friend;
  }

  public void setFriend(Friend friend) {
    this.friend = friend;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $apk = this.getApk();
    result = result * PRIME + ($apk == null ? 43 : $apk.hashCode());
    final Object $mainObb = this.getMainObb();
    result = result * PRIME + ($mainObb == null ? 43 : $mainObb.hashCode());
    final Object $patchObb = this.getPatchObb();
    result = result * PRIME + ($patchObb == null ? 43 : $patchObb.hashCode());
    final Object $appName = this.getAppName();
    result = result * PRIME + ($appName == null ? 43 : $appName.hashCode());
    final Object $packageName = this.getPackageName();
    result = result * PRIME + ($packageName == null ? 43 : $packageName.hashCode());
    final Object $fileInfos = this.getFileInfos();
    result = result * PRIME + ($fileInfos == null ? 43 : $fileInfos.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof AndroidAppInfo)) return false;
    final AndroidAppInfo other = (AndroidAppInfo) o;
    if (!other.canEqual(this)) return false;
    final Object this$apk = this.getApk();
    final Object other$apk = other.getApk();
    if (this$apk == null ? other$apk != null : !this$apk.equals(other$apk)) return false;
    final Object this$mainObb = this.getMainObb();
    final Object other$mainObb = other.getMainObb();
    if (this$mainObb == null ? other$mainObb != null : !this$mainObb.equals(other$mainObb)) {
      return false;
    }
    final Object this$patchObb = this.getPatchObb();
    final Object other$patchObb = other.getPatchObb();
    if (this$patchObb == null ? other$patchObb != null : !this$patchObb.equals(other$patchObb)) {
      return false;
    }
    final Object this$appName = this.getAppName();
    final Object other$appName = other.getAppName();
    if (this$appName == null ? other$appName != null : !this$appName.equals(other$appName)) {
      return false;
    }
    final Object this$packageName = this.getPackageName();
    final Object other$packageName = other.getPackageName();
    if (this$packageName == null ? other$packageName != null
        : !this$packageName.equals(other$packageName)) {
      return false;
    }
    final Object this$fileInfos = this.getFileInfos();
    final Object other$fileInfos = other.getFileInfos();
    return this$fileInfos == null ? other$fileInfos == null
        : this$fileInfos.equals(other$fileInfos);
  }

  public String toString() {
    return "AndroidAppInfo(apk="
        + this.getApk()
        + ", mainObb="
        + this.getMainObb()
        + ", patchObb="
        + this.getPatchObb()
        + ", appName="
        + this.getAppName()
        + ", packageName="
        + this.getPackageName()
        + ", fileInfos="
        + this.getFileInfos()
        + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof AndroidAppInfo;
  }
}
