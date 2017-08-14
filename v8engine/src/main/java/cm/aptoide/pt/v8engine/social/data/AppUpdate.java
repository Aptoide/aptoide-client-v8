package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.listapp.File;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialCard;
import cm.aptoide.pt.v8engine.Install;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 21/06/2017.
 */

public class AppUpdate implements Post {

  private final String cardId;
  private final String storeName;
  private final String storeAvatar;
  private final String storeTheme;
  private final Long storeId;
  private final String appUpdateIcon;
  private final String appUpdateName;
  private final long appUpdateId;
  private final String packageName;
  private final float appUpdateAverageRating;
  private final Date updateAddedDate;
  private final String abUrl;
  private final CardType cardType;
  private final File file;
  private final Obb obb;
  private boolean isLiked;
  private Install.InstallationStatus installationStatus;
  private boolean likedFromClick;
  private List<SocialCard.CardComment> comments;

  public AppUpdate(String cardId, String storeName, String storeAvatar, String storeTheme,
      Long storeId, String appUpdateIcon, String appUpdateName, long appUpdateId,
      String packageName, float appUpdateAverageRating, Date updateAddedDate, String abUrl,
      boolean isLiked, CardType cardType, File file, Obb obb,
      Install.InstallationStatus installationStatus) {
    this.cardId = cardId;
    this.storeName = storeName;
    this.storeAvatar = storeAvatar;
    this.storeTheme = storeTheme;
    this.storeId = storeId;
    this.appUpdateIcon = appUpdateIcon;
    this.appUpdateName = appUpdateName;
    this.appUpdateId = appUpdateId;
    this.packageName = packageName;
    this.appUpdateAverageRating = appUpdateAverageRating;
    this.updateAddedDate = updateAddedDate;
    this.abUrl = abUrl;
    this.isLiked = isLiked;
    this.cardType = cardType;
    this.file = file;
    this.obb = obb;
    this.installationStatus = installationStatus;
    this.comments = new ArrayList<>();
  }

  public Long getStoreId() {
    return storeId;
  }

  public String getStoreTheme() {
    return storeTheme;
  }

  public Install.InstallationStatus getInstallationStatus() {
    return installationStatus;
  }

  public void setInstallationStatus(Install.InstallationStatus installationStatus) {
    this.installationStatus = installationStatus;
  }

  public long getAppUpdateId() {
    return appUpdateId;
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
    isLiked = liked;
    likedFromClick = true;
  }

  @Override public boolean isLikeFromClick() {
    return likedFromClick;
  }

  @Override public List<SocialCard.CardComment> getComments() {
    return comments;
  }

  @Override public long getCommentsNumber() {
    return comments.size();
  }

  @Override public void addComment(SocialCard.CardComment postComment) {
    comments.add(0, postComment);
  }

  public float getAppUpdateAverageRating() {
    return appUpdateAverageRating;
  }

  public void setLikedFromClick(boolean likedFromClick) {
    this.likedFromClick = likedFromClick;
  }
}
