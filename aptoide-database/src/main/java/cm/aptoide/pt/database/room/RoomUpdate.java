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

  public RoomUpdate() {
  }

  public RoomUpdate(long id, String name, String icon, String packageName, String md5sum,
      String path, long size, String versionName, String pathAlt, int versionCode, String rankName,
      String mainObbFileName, String mainObbPath, String mainObbMd5, String patchObbFileName,
      String patchObbPath, String patchObbMd5, boolean appcUpgrade, boolean hasAppc,
      List<RoomSplit> roomSplits, List<String> requiredSplits, String storeName) {
    this.appId = id;
    this.label = name;
    this.icon = icon;
    this.packageName = packageName;
    this.md5 = md5sum;
    this.apkPath = path;
    this.size = size;
    this.updateVersionName = versionName;
    this.alternativeApkPath = pathAlt;
    this.updateVersionCode = versionCode;
    this.trustedBadge = rankName;
    this.mainObbName = mainObbFileName;
    this.mainObbPath = mainObbPath;
    this.mainObbMd5 = mainObbMd5;
    this.patchObbName = patchObbFileName;
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

  public void setAppId(long appId) {
    this.appId = appId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
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

  public void setPackageName(@NonNull String packageName) {
    this.packageName = packageName;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public String getApkPath() {
    return apkPath;
  }

  public void setApkPath(String apkPath) {
    this.apkPath = apkPath;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public String getUpdateVersionName() {
    return updateVersionName;
  }

  public void setUpdateVersionName(String updateVersionName) {
    this.updateVersionName = updateVersionName;
  }

  public String getAlternativeApkPath() {
    return alternativeApkPath;
  }

  public void setAlternativeApkPath(String alternativeApkPath) {
    this.alternativeApkPath = alternativeApkPath;
  }

  public int getUpdateVersionCode() {
    return updateVersionCode;
  }

  public void setUpdateVersionCode(int updateVersionCode) {
    this.updateVersionCode = updateVersionCode;
  }

  public String getMainObbPath() {
    return mainObbPath;
  }

  public void setMainObbPath(String mainObbPath) {
    this.mainObbPath = mainObbPath;
  }

  public String getMainObbMd5() {
    return mainObbMd5;
  }

  public void setMainObbMd5(String mainObbMd5) {
    this.mainObbMd5 = mainObbMd5;
  }

  public String getPatchObbPath() {
    return patchObbPath;
  }

  public void setPatchObbPath(String patchObbPath) {
    this.patchObbPath = patchObbPath;
  }

  public String getPatchObbMd5() {
    return patchObbMd5;
  }

  public void setPatchObbMd5(String patchObbMd5) {
    this.patchObbMd5 = patchObbMd5;
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

  public void setMainObbName(String mainObbName) {
    this.mainObbName = mainObbName;
  }

  public String getPatchObbName() {
    return patchObbName;
  }

  public void setPatchObbName(String patchObbName) {
    this.patchObbName = patchObbName;
  }

  public String getTrustedBadge() {
    return trustedBadge;
  }

  public void setTrustedBadge(String trustedBadge) {
    this.trustedBadge = trustedBadge;
  }

  public boolean isAppcUpgrade() {
    return appcUpgrade;
  }

  public void setAppcUpgrade(boolean appcUpgrade) {
    this.appcUpgrade = appcUpgrade;
  }

  public boolean hasAppc() {
    return hasAppc;
  }

  public List<RoomSplit> getRoomSplits() {
    return roomSplits;
  }

  public void setRoomSplits(List<RoomSplit> roomSplits) {
    this.roomSplits = roomSplits;
  }

  public List<String> getRequiredSplits() {
    return requiredSplits;
  }

  public void setRequiredSplits(List<String> requiredSplits) {
    this.requiredSplits = requiredSplits;
  }

  public boolean hasSplits() {
    return roomSplits != null && !roomSplits.isEmpty();
  }

  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public void setHasAppc(boolean hasAppc) {
    this.hasAppc = hasAppc;
  }
}
