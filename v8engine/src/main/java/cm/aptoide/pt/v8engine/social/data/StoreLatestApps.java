package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.model.v7.listapp.App;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 21/06/2017.
 */

public class StoreLatestApps implements Card {
  private final String cardId;
  private final String storeName;
  private final String storeAvatar;
  private final int subscribers;
  private final int appsNumber;
  private final Date latestUpdate;
  private final List<App> apps;
  private final String abUrl;
  private final CardType cardType;

  public StoreLatestApps(String cardId, String storeName, String storeAvatar, int subscribers, int appsNumber,
      Date latestUpdate, List<App> apps, String abUrl, CardType cardType) {
    this.cardId = cardId;
    this.storeName = storeName;
    this.storeAvatar = storeAvatar;
    this.subscribers = subscribers;
    this.appsNumber = appsNumber;
    this.latestUpdate = latestUpdate;
    this.apps = apps;
    this.abUrl = abUrl;
    this.cardType = cardType;
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

  public String getAbUrl() {
    return abUrl;
  }

  @Override public String getCardId() {
    return this.cardId;
  }

  @Override public CardType getType() {
    return this.cardType;
  }
}
