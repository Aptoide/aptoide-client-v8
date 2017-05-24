package cm.aptoide.pt.v8engine.view.account.store;

import android.text.TextUtils;
import org.parceler.Parcel;

@Parcel public class ManageStoreModel {
  long storeId;
  String storeName;
  String storeDescription;
  String storeAvatarPath;
  String storeThemeName;
  boolean goToHome;
  boolean newAvatar;

  public ManageStoreModel() {
    this.storeId = -1;
    this.storeName = "";
    this.storeDescription = "";
    this.storeAvatarPath = "";
    this.storeThemeName = "";
    this.goToHome = true;
    this.newAvatar = false;
  }

  public ManageStoreModel(boolean goToHome) {
    this.storeId = -1;
    this.storeName = "";
    this.storeDescription = "";
    this.storeAvatarPath = "";
    this.storeThemeName = "";
    this.goToHome = goToHome;
    this.newAvatar = false;
  }

  public ManageStoreModel(long storeId, boolean goToHome, //String storeRemoteUrl,
      String storeThemeName, String storeName, String storeDescription, String storeAvatarPath) {
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeDescription = storeDescription;
    this.storeAvatarPath = storeAvatarPath;
    this.storeThemeName = storeThemeName;
    this.goToHome = goToHome;
    this.newAvatar = false;
  }

  public ManageStoreModel(long storeId, String storeAvatarPath, boolean goToHome,
      String storeThemeName, String storeName, String storeDescription) {
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeDescription = storeDescription;
    this.storeAvatarPath = storeAvatarPath;
    this.storeThemeName = storeThemeName;
    this.goToHome = goToHome;
    this.newAvatar = false;
  }

  public static ManageStoreModel from(ManageStoreModel otherStoreModel, String storeName,
      String storeDescription) {

    // if current store name is empty we use the old one
    if (TextUtils.isEmpty(storeName)) {
      storeName = otherStoreModel.getStoreName();
    }

    // if current store description is empty we use the old one
    if (TextUtils.isEmpty(storeDescription)) {
      storeDescription = otherStoreModel.getStoreDescription();
    }

    ManageStoreModel newStoreModel =
        new ManageStoreModel(otherStoreModel.getStoreId(), otherStoreModel.isGoToHome(),
            otherStoreModel.getStoreThemeName(), storeName, storeDescription,
            otherStoreModel.getStoreAvatarPath());

    if (otherStoreModel.hasNewAvatar()) {
      newStoreModel.setStoreAvatarPath(otherStoreModel.getStoreAvatarPath());
    }

    return newStoreModel;
  }

  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public String getStoreDescription() {
    return storeDescription;
  }

  public String getStoreAvatarPath() {
    return storeAvatarPath;
  }

  public void setStoreAvatarPath(String storeAvatarPath) {
    this.storeAvatarPath = storeAvatarPath;
    this.newAvatar = true;
  }

  public long getStoreId() {
    return storeId;
  }

  public String getStoreThemeName() {
    return storeThemeName;
  }

  public void setStoreThemeName(String storeTheme) {
    this.storeThemeName = storeTheme;
  }

  public boolean isGoToHome() {
    return goToHome;
  }

  public boolean hasNewAvatar() {
    return newAvatar;
  }

  /**
   * This method sets the required non existing store data as null to prepare this object a
   * network request.
   */
  public void prepareToSendRequest() {
    if (TextUtils.isEmpty(storeName)) {
      storeName = null;
    }

    if (TextUtils.isEmpty(storeThemeName)) {
      storeThemeName = null;
    }

    if (TextUtils.isEmpty(storeDescription)) {
      storeDescription = null;
    }
  }

  public boolean hasStoreDescription() {
    return !TextUtils.isEmpty(getStoreDescription());
  }

  public boolean hasThemeName() {
    return !TextUtils.isEmpty(getStoreThemeName());
  }

  public boolean hasStoreAvatar() {
    return !TextUtils.isEmpty(getStoreAvatarPath());
  }

  public boolean hasStoreName() {
    return !TextUtils.isEmpty(getStoreName());
  }

  public boolean storeExists() {
    return storeId >= 0L;
  }
}
