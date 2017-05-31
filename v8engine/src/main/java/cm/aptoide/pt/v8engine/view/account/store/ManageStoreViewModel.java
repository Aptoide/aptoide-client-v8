package cm.aptoide.pt.v8engine.view.account.store;

import android.text.TextUtils;
import org.parceler.Parcel;

@Parcel public class ManageStoreViewModel {
  long storeId;
  String storeName;
  String storeDescription;
  String storeImagePath;
  String storeThemeName;
  private boolean newAvatar;

  public ManageStoreViewModel() {
    this.storeId = -1;
    this.storeName = "";
    this.storeDescription = "";
    this.storeImagePath = "";
    this.storeThemeName = "";
    this.newAvatar = false;
  }

  public ManageStoreViewModel(long storeId, String storeThemeName, String storeName,
      String storeDescription, String storeImagePath) {
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeDescription = storeDescription;
    this.storeImagePath = storeImagePath;
    this.storeThemeName = storeThemeName;
    this.newAvatar = false;
  }

  public static ManageStoreViewModel from(ManageStoreViewModel otherStoreModel, String storeName,
      String storeDescription) {

    // if current store name is empty we use the old one
    if (TextUtils.isEmpty(storeName)) {
      storeName = otherStoreModel.getStoreName();
    }

    // if current store description is empty we use the old one
    if (TextUtils.isEmpty(storeDescription)) {
      storeDescription = otherStoreModel.getStoreDescription();
    }

    ManageStoreViewModel newModel =
        new ManageStoreViewModel(otherStoreModel.getStoreId(), otherStoreModel.getStoreThemeName(),
            storeName, storeDescription, otherStoreModel.getStoreImagePath());

    // if previous model had a new image, set it in new model
    if (otherStoreModel.hasNewAvatar()) {
      newModel.setStoreImagePath(otherStoreModel.getStoreImagePath());
    }

    return newModel;
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

  public String getStoreImagePath() {
    return storeImagePath;
  }

  public void setStoreImagePath(String storeAvatarPath) {
    this.storeImagePath = storeAvatarPath;
    this.newAvatar = true;
  }

  public boolean hasNewAvatar() {
    return newAvatar;
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
    return !TextUtils.isEmpty(getStoreImagePath());
  }

  public boolean hasStoreName() {
    return !TextUtils.isEmpty(getStoreName());
  }

  public boolean storeExists() {
    return storeId >= 0L;
  }
}
