package cm.aptoide.pt.v8engine.social.data;

import java.util.Date;

/**
 * Created by jdandrade on 21/06/2017.
 */

public class AppUpdate implements Card {

  private final String cardId;
  private final String storeName;
  private final String storeAvatar;
  private final String appUpdateIcon;
  private final String appUpdateName;
  private final String packageName;
  private final Date updateAddedDate;
  private final String abUrl;
  private final CardType cardType;

  public AppUpdate(String cardId, String storeName, String storeAvatar, String appUpdateIcon,
      String appUpdateName, String packageName, Date updateAddedDate, String abUrl, CardType cardType) {
    this.cardId = cardId;
    this.storeName = storeName;
    this.storeAvatar = storeAvatar;
    this.appUpdateIcon = appUpdateIcon;
    this.appUpdateName = appUpdateName;
    this.packageName = packageName;
    this.updateAddedDate = updateAddedDate;
    this.abUrl = abUrl;
    this.cardType = cardType;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getStoreAvatar() {
    return storeAvatar;
  }

  public String getAppUpdateIcon() {
    return appUpdateIcon;
  }

  public Date getUpdateAddedDate() {
    return updateAddedDate;
  }

  public String getAppUpdateName() {
    return appUpdateName;
  }

  public String getPackageName() {
    return packageName;
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
