package cm.aptoide.pt.account.view.store;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.store.StoreTheme;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.parceler.Parcel;

@Parcel public class ManageStoreViewModel {

  public static final String FACEBOOK_BASE_URL = "https://www.facebook.com/";
  public static final String TWITCH_BASE_URL = "https://go.twitch.tv/";
  public static final String TWITTER_BASE_URL = "https://twitter.com/";
  public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/";

  long storeId;
  String storeName;
  String storeDescription;
  String pictureUri;
  StoreTheme storeTheme;
  boolean newAvatar;
  List<SimpleSetStoreRequest.StoreLinks> storeLinks;

  public ManageStoreViewModel() {
    this.storeId = -1;
    this.storeName = "";
    this.storeDescription = "";
    this.pictureUri = "";
    this.storeTheme = StoreTheme.DEFAULT;
    this.newAvatar = false;
    this.storeLinks = Collections.emptyList();
  }

  public ManageStoreViewModel(long storeId, StoreTheme storeTheme, String storeName,
      String storeDescription, String pictureUri, List<Store.SocialChannel> storeLinks) {
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeDescription = storeDescription;
    this.pictureUri = pictureUri;
    this.storeTheme = storeTheme;
    this.storeLinks = buildStoreLinksList(storeLinks);
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

  private List<SimpleSetStoreRequest.StoreLinks> buildStoreLinksList(
      List<Store.SocialChannel> socialChannels) {
    List<SimpleSetStoreRequest.StoreLinks> storeLinks = new ArrayList<>();
    for (Store.SocialChannel socialChannel : socialChannels) {
      storeLinks.add(
          new SimpleSetStoreRequest.StoreLinks(socialChannel.getType(), socialChannel.getUrl()));
    }
    return storeLinks;
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

  public List<SimpleSetStoreRequest.StoreLinks> getStoreLinks() {
    return storeLinks;
  }

  public void setStoreLinks(List<SimpleSetStoreRequest.StoreLinks> storeLinks) {
    this.storeLinks = storeLinks;
  }
}
