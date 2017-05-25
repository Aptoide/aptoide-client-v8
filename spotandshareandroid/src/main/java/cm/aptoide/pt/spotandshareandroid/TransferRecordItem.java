package cm.aptoide.pt.spotandshareandroid;

import android.graphics.drawable.Drawable;

/**
 * Created by Filipe on 04-09-2016.
 */
public class TransferRecordItem {

  private Drawable icon;
  private String appName;
  private String packageName;
  private String filePath;
  private boolean received;
  private String versionName;
  private boolean deleted;
  private boolean needReSend;
  private boolean isSent;

  public TransferRecordItem(Drawable icon, String appName, String packageName,
      String filePath, boolean received, String versionName) {
    this.icon = icon;
    this.appName = appName;
    this.packageName = packageName;
    this.filePath = filePath;
    this.received = received;
    this.versionName = versionName;
    deleted = false;
    needReSend = false;
    isSent = false;
  }

  public Drawable getIcon() {
    return icon;
  }

  public String getAppName() {
    return appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getFilePath() {
    return filePath;
  }

  public boolean isReceived() {
    return received;
  }

  public String getVersionName() {
    return versionName;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public boolean isNeedReSend() {
    return needReSend;
  }

  public void setNeedReSend(boolean needReSend) {
    this.needReSend = needReSend;
  }

  public boolean isSent() {
    return isSent;
  }

  public void setSent(boolean sent) {
    isSent = sent;
  }

}
