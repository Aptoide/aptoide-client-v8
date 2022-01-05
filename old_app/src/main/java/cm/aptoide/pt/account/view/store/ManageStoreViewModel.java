package cm.aptoide.pt.account.view.store;

import android.text.TextUtils;
import cm.aptoide.pt.themes.StoreTheme;
import org.parceler.Parcel;

@Parcel public class ManageStoreViewModel {

  long storeId;
  String storeName;
  String storeDescription;
  String pictureUri;
  StoreTheme storeTheme;
  boolean newAvatar;

  public ManageStoreViewModel() {
    this.storeId = -1;
    this.storeName = "";
    this.storeDescription = "";
    this.pictureUri = "";
    this.storeTheme = StoreTheme.DEFAULT;
    this.newAvatar = false;
  }

  public ManageStoreViewModel(long storeId, StoreTheme storeTheme, String storeName,
      String storeDescription, String pictureUri) {
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeDescription = storeDescription;
    this.pictureUri = pictureUri;
    this.storeTheme = storeTheme;
    this.newAvatar = false;
  }

  public static ManageStoreViewModel update(ManageStoreViewModel model, String storeName,
      String storeDescription) {

    // if current store name is empty we use the old one
    if (!TextUtils.isEmpty(storeName)) {
      model.setStoreName(storeName);
    }

    // if current store description is empty we use the old one
    if (!TextUtils.isEmpty(storeDescription)) {
      model.setStoreDescription(storeDescription);
    }

    return model;
  }

  public void setNewAvatar(boolean newAvatar) {
    this.newAvatar = newAvatar;
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

  public void setStoreDescription(String storeDescription) {
    this.storeDescription = storeDescription;
  }

  public String getPictureUri() {
    return pictureUri;
  }

  public void setPictureUri(String pictureUri) {
    this.pictureUri = pictureUri;
  }

  public boolean hasNewAvatar() {
    return newAvatar;
  }

  public boolean hasPicture() {
    return (!TextUtils.isEmpty(pictureUri));
  }

  public long getStoreId() {
    return storeId;
  }

  public void setStoreId(long storeId) {
    this.storeId = storeId;
  }

  public StoreTheme getStoreTheme() {
    return storeTheme;
  }

  public void setStoreTheme(StoreTheme storeTheme) {
    this.storeTheme = storeTheme;
  }

  public boolean storeExists() {
    return storeId >= 0L;
  }
}
