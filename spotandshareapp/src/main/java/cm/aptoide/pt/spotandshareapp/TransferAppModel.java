package cm.aptoide.pt.spotandshareapp;

import android.graphics.drawable.Drawable;

/**
 * Created by filipe on 10-07-2017.
 */

public class TransferAppModel {

  private String appName;
  private String packageName;
  private String filePath;
  private Drawable appIcon;
  /**
   * Determines if the app is being sent from this device (true) or if it is being received (false)
   */
  private boolean transferenceOriginatedHere;
  private boolean finishedTransference;
  private String senderName;
  private int hashcode;

  public TransferAppModel(String appName, String packageName, String filePath, Drawable appIcon,
      boolean transferenceOriginatedHere, String senderName, int hashcode) {
    this.appName = appName;
    this.packageName = packageName;
    this.filePath = filePath;
    this.appIcon = appIcon;
    this.transferenceOriginatedHere = transferenceOriginatedHere;
    this.senderName = senderName;
    this.hashcode = hashcode;
  }

  public String getAppName() {
    return appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getFilePath() {
    return filePath;
  }

  public Drawable getAppIcon() {
    return appIcon;
  }

  public boolean isTransferenceOriginatedHere() {
    return transferenceOriginatedHere;
  }

  public String getSenderName() {
    return senderName;
  }

  public boolean isTransferenceFinished() {
    return finishedTransference;
  }

  public void setTransferenceFinished(boolean finishedTransference) {
    this.finishedTransference = finishedTransference;
  }

  public int getHashcode() {
    return hashcode;
  }
}
