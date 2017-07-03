package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.listapp.File;
import java.util.Date;

/**
 * Created by jdandrade on 21/06/2017.
 */

public class AppUpdate implements Post {

  private final String cardId;
  private final String storeName;
  private final String storeAvatar;
  private final String storeTheme;
  private final String appUpdateIcon;
  private final String appUpdateName;
  private final String packageName;
  private final Date updateAddedDate;
  private final String abUrl;
  private final CardType cardType;
  private final File file;
  private final Obb obb;
  private int progress;

  public AppUpdate(String cardId, String storeName, String storeAvatar, String storeTheme,
      String appUpdateIcon, String appUpdateName, String packageName, Date updateAddedDate,
      String abUrl, CardType cardType, File file, Obb obb, int progress) {
    this.cardId = cardId;
    this.storeName = storeName;
    this.storeAvatar = storeAvatar;
    this.storeTheme = storeTheme;
    this.appUpdateIcon = appUpdateIcon;
    this.appUpdateName = appUpdateName;
    this.packageName = packageName;
    this.updateAddedDate = updateAddedDate;
    this.abUrl = abUrl;
    this.cardType = cardType;
    this.file = file;
    this.obb = obb;
    this.progress = progress;
  }

  public String getStoreTheme() {
    return storeTheme;
  }

  public int getProgress() {
    return progress;
  }

  public void setProgress(int progress) {
    this.progress = progress;
  }

  public Obb getObb() {
    return obb;
  }

  public File getFile() {
    return file;
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
