/*
 * Copyright (c) 2016.
 * Modified on 24/08/2016.
 */
package cm.aptoide.pt.database.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.List;

/**
 * Created on 12/05/16.
 */

@Entity(tableName = "update") public class RoomUpdate {

  @NonNull @PrimaryKey private String packageName;
  private long appId;
  private String label;
  private String icon;
  private String md5;
  private String apkPath;
  private long size;
  private String updateVersionName;
  private int updateVersionCode;
  private boolean excluded;
  private String trustedBadge;
  private String alternativeApkPath;
  private String storeName;
  private boolean appcUpgrade;
  // Obb
  private String mainObbName;
  private String mainObbPath;
  private String mainObbMd5;
  private String patchObbName;
  private String patchObbPath;
  private String patchObbMd5;
  // Splits
  private List<RoomSplit> roomSplits;
  private List<String> requiredSplits;
  //appc
  private boolean hasAppc;

  public RoomUpdate(long appId, String label, String icon, String packageName, String md5,
      String apkPath, long size, String updateVersionName, String alternativeApkPath,
      int updateVersionCode, String trustedBadge, String mainObbName, String mainObbPath,
      String mainObbMd5, String patchObbName, String patchObbPath, String patchObbMd5,
      boolean appcUpgrade, boolean hasAppc, List<RoomSplit> roomSplits, List<String> requiredSplits,
      String storeName) {
    this.appId = appId;
    this.label = label;
    this.icon = icon;
    this.packageName = packageName;
    this.md5 = md5;
    this.apkPath = apkPath;
    this.size = size;
    this.updateVersionName = updateVersionName;
    this.alternativeApkPath = alternativeApkPath;
    this.updateVersionCode = updateVersionCode;
    this.trustedBadge = trustedBadge;
    this.mainObbName = mainObbName;
    this.mainObbPath = mainObbPath;
    this.mainObbMd5 = mainObbMd5;
    this.patchObbName = patchObbName;
    this.patchObbPath = patchObbPath;
    this.patchObbMd5 = patchObbMd5;
    this.hasAppc = hasAppc;
    this.appcUpgrade = appcUpgrade;
    this.roomSplits = roomSplits;
    this.requiredSplits = requiredSplits;
    this.storeName = storeName;
  }

  public long getAppId() {
    return appId;
  }

  public String getLabel() {
    return label;
  }

  public String getIcon() {
    return icon;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getMd5() {
    return md5;
  }

  public String getApkPath() {
    return apkPath;
  }

  public long getSize() {
    return size;
  }

  public String getUpdateVersionName() {
    return updateVersionName;
  }

  public String getAlternativeApkPath() {
    return alternativeApkPath;
  }

  public int getUpdateVersionCode() {
    return updateVersionCode;
  }

  public String getMainObbPath() {
    return mainObbPath;
  }

  public String getMainObbMd5() {
    return mainObbMd5;
  }

  public String getPatchObbPath() {
    return patchObbPath;
  }

  public String getPatchObbMd5() {
    return patchObbMd5;
  }

  public boolean isExcluded() {
    return excluded;
  }

  public void setExcluded(boolean excluded) {
    this.excluded = excluded;
  }

  public String getMainObbName() {
    return mainObbName;
  }

  public String getPatchObbName() {
    return patchObbName;
  }

  public String getTrustedBadge() {
    return trustedBadge;
  }

  public boolean isAppcUpgrade() {
    return appcUpgrade;
  }

  public boolean hasAppc() {
    return hasAppc;
  }

  public List<RoomSplit> getRoomSplits() {
    return roomSplits;
  }

  public List<String> getRequiredSplits() {
    return requiredSplits;
  }

  public boolean hasSplits() {
    return roomSplits != null && !roomSplits.isEmpty();
  }

  public String getStoreName() {
    return storeName;
  }
}