package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 21/06/2017.
 */

public class StoreLatestApps implements Post {
  private final String cardId;
  private final String storeName;
  private final String storeAvatar;
  private final String storeTheme;
  private final int subscribers;
  private final int appsNumber;
  private final Date latestUpdate;
  private final List<App> apps;
  private final String abUrl;
  private final CardType cardType;
  private final Long storeId;
  private boolean isLiked;
  private boolean likedFromClick;

  public StoreLatestApps(String cardId, Long storeId, String storeName, String storeAvatar,
      String storeTheme, int subscribers, int appsNumber, Date latestUpdate, List<App> apps,
      String abUrl, boolean isLiked, CardType cardType) {
    this.cardId = cardId;
    this.storeId = storeId;
    this.storeName = storeName;
    this.storeAvatar = storeAvatar;
    this.storeTheme = storeTheme;
    this.subscribers = subscribers;
    this.appsNumber = appsNumber;
    this.latestUpdate = latestUpdate;
    this.apps = apps;
    this.abUrl = abUrl;
    this.isLiked = isLiked;
    this.cardType = cardType;
  }

  public String getStoreTheme() {
    return storeTheme;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getStoreAvatar() {
    return storeAvatar;
  }

  public int getSubscribers() {
    return subscribers;
  }

  public int getAppsNumber() {
    return appsNumber;
  }

  public Date getLatestUpdate() {
    return latestUpdate;
  }

  public List<App> getApps() {
    return apps;
  }

  @Override public String getCardId() {
    return this.cardId;
  }

  @Override public CardType getType() {
    return this.cardType;
  }

  @Override public String getAbUrl() {
    return abUrl;
  }

  public boolean isLiked() {
    return isLiked;
  }

  @Override public void setLiked(boolean liked) {
    this.isLiked = liked;
    likedFromClick = true;
  }

  @Override public boolean isLikeFromClick() {
    return likedFromClick;
  }

  public Long getStoreId() {
    return storeId;
  }

  public void setLikedFromClick(boolean likedFromClick) {
    this.likedFromClick = likedFromClick;
  }
}
