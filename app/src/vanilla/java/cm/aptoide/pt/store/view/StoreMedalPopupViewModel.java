package cm.aptoide.pt.store.view;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

/**
 * Created by trinkes on 30/10/2017.
 */

class StoreMedalPopupViewModel {

  private final GridStoreMetaWidget.HomeMeta.Badge storeBadge;
  private final int mainColor;
  private final int storeMedalIcon;
  private final String medalText;
  private final String congratulationsMessage;
  private final String uploadedAppsMessage;
  private final String downloadsMessage;
  private final String followersMessage;
  private final String reviewsMessage;
  private final boolean tinBadgeSelected;
  private final boolean bronzeBadgeSelected;
  private final boolean silverBadgeSelected;
  private final boolean goldBadgeSelected;
  private final boolean platinumBadgeSelected;
  private final @ColorRes int tinBadgeColor;
  private final @ColorRes int bronzeBadgeColor;
  private final @ColorRes int silverBadgeColor;
  private final @ColorRes int goldBadgeColor;
  private final @ColorRes int platinumBadgeColor;
  private final @ColorRes int progress1;
  private final @ColorRes int progress2;
  private final @ColorRes int progress3;
  private final @ColorRes int progress4;

  public StoreMedalPopupViewModel(GridStoreMetaWidget.HomeMeta.Badge storeBadge,
      @ColorRes int mainColor, @DrawableRes int storeMedalIcon, String medalText,
      String congratulationsMessage, String uploadedAppsMessage, String downloadsMessage,
      String followersMessage, String reviewsMessage, boolean tinBadgeSelected,
      boolean bronzeBadgeSelected, boolean silverBadgeSelected, boolean goldBadgeSelected,
      boolean platinumBadgeSelected, int tinBadgeColor, int bronzeBadgeColor, int silverBadgeColor,
      int goldBadgeColor, int platinumBadgeColor, int progress1, int progress2, int progress3,
      int progress4) {
    this.storeBadge = storeBadge;
    this.mainColor = mainColor;
    this.storeMedalIcon = storeMedalIcon;
    this.medalText = medalText;
    this.congratulationsMessage = congratulationsMessage;
    this.uploadedAppsMessage = uploadedAppsMessage;
    this.downloadsMessage = downloadsMessage;
    this.followersMessage = followersMessage;
    this.reviewsMessage = reviewsMessage;
    this.tinBadgeSelected = tinBadgeSelected;
    this.bronzeBadgeSelected = bronzeBadgeSelected;
    this.silverBadgeSelected = silverBadgeSelected;
    this.goldBadgeSelected = goldBadgeSelected;
    this.platinumBadgeSelected = platinumBadgeSelected;
    this.tinBadgeColor = tinBadgeColor;
    this.bronzeBadgeColor = bronzeBadgeColor;
    this.silverBadgeColor = silverBadgeColor;
    this.goldBadgeColor = goldBadgeColor;
    this.platinumBadgeColor = platinumBadgeColor;
    this.progress1 = progress1;
    this.progress2 = progress2;
    this.progress3 = progress3;
    this.progress4 = progress4;
  }

  public GridStoreMetaWidget.HomeMeta.Badge getStoreBadge() {
    return storeBadge;
  }

  public int getProgress1() {
    return progress1;
  }

  public int getProgress2() {
    return progress2;
  }

  public int getProgress3() {
    return progress3;
  }

  public int getProgress4() {
    return progress4;
  }

  public int getTinBadgeColor() {
    return tinBadgeColor;
  }

  public int getBronzeBadgeColor() {
    return bronzeBadgeColor;
  }

  public int getSilverBadgeColor() {
    return silverBadgeColor;
  }

  public int getGoldBadgeColor() {
    return goldBadgeColor;
  }

  public int getPlatinumBadgeColor() {
    return platinumBadgeColor;
  }

  public boolean isTinBadgeSelected() {
    return tinBadgeSelected;
  }

  public boolean isBronzeBadgeSelected() {
    return bronzeBadgeSelected;
  }

  public boolean isSilverBadgeSelected() {
    return silverBadgeSelected;
  }

  public boolean isGoldBadgeSelected() {
    return goldBadgeSelected;
  }

  public boolean isPlatinumBadgeSelected() {
    return platinumBadgeSelected;
  }

  public @ColorRes int getMainColor() {
    return mainColor;
  }

  public String getReviewsMessage() {
    return reviewsMessage;
  }

  public String getMedalText() {
    return medalText;
  }

  public String getCongratulationsMessage() {
    return congratulationsMessage;
  }

  public String getUploadedAppsMessage() {
    return uploadedAppsMessage;
  }

  public String getDownloadsMessage() {
    return downloadsMessage;
  }

  public String getFollowersMessage() {
    return followersMessage;
  }

  public @DrawableRes int getStoreMedalIcon() {
    return storeMedalIcon;
  }
}
