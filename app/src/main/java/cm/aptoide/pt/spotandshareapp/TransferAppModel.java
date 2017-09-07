package cm.aptoide.pt.spotandshareapp;

import android.graphics.drawable.Drawable;
import cm.aptoide.pt.spotandshareandroid.transfermanager.Transfer;

/**
 * Created by filipe on 10-07-2017.
 */

public class TransferAppModel {

  private String appName;
  private String packageName;
  private double apkSize;
  private String filePath;
  private Drawable appIcon;
  private Transfer.State transferState;
  private boolean installed;
  private SpotAndShareUser senderFriend;
  private int hashcode;

  public TransferAppModel(String appName, String packageName, double apkSize, String filePath,
      Drawable appIcon, Transfer.State transferState, SpotAndShareUser senderFriend, int hashcode) {
    this.appName = appName;
    this.packageName = packageName;
    this.apkSize = apkSize;
    this.filePath = filePath;
    this.appIcon = appIcon;
    this.transferState = transferState;
    this.senderFriend = senderFriend;
    this.hashcode = hashcode;
  }

  public String getAppName() {
    return appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public double getApkSize() {
    return apkSize;
  }

  public String getFilePath() {
    return filePath;
  }

  public Drawable getAppIcon() {
    return appIcon;
  }

  public Transfer.State getTransferState() {
    return transferState;
  }

  public SpotAndShareUser getFriend() {
    return senderFriend;
  }

  /**
   * Returns true if the user installed this app, by pressing the install button on the transfer
   * record view.
   *
   * @return boolean
   */
  public boolean isInstalled() {
    return installed;
  }

  public void setInstalledApp(boolean installed) {
    this.installed = installed;
  }

  public int getHashcode() {
    return hashcode;
  }
}
