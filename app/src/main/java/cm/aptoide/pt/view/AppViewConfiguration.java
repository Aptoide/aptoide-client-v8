package cm.aptoide.pt.view;

import cm.aptoide.pt.app.view.AppViewFragment.OpenType;
import cm.aptoide.pt.search.model.SearchAdResult;

/**
 * Created by D01 on 17/05/2018.
 */

public class AppViewConfiguration {

  private final long appId;
  private final String packageName;
  private final String storeName;
  private final String storeTheme;
  private final SearchAdResult minimalAd;
  private final OpenType shouldInstall;
  private final String md5;
  private final String uniqueName;
  private final double appc;
  private final String editorsChoice;
  private final String originTag;
  private final String campaignUrl;

  public AppViewConfiguration(long appId, String packageName, String storeName, String storeTheme,
      SearchAdResult minimalAd, OpenType shouldInstall, String md5, String uniqueName, double appc,
      String editorsChoice, String originTag, String campaignUrl) {
    this.appId = appId;
    this.packageName = packageName;
    this.storeName = storeName;
    this.storeTheme = storeTheme;
    this.minimalAd = minimalAd;
    this.shouldInstall = shouldInstall;
    this.md5 = md5;
    this.uniqueName = uniqueName;
    this.appc = appc;
    this.editorsChoice = editorsChoice;
    this.originTag = originTag;
    this.campaignUrl = campaignUrl;
  }

  public long getAppId() {
    return appId;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getStoreTheme() {
    return storeTheme;
  }

  public SearchAdResult getMinimalAd() {
    return minimalAd;
  }

  public OpenType shouldInstall() {
    return shouldInstall;
  }

  public String getMd5() {
    return md5;
  }

  public String getUniqueName() {
    return uniqueName;
  }

  public double getAppc() {
    return appc;
  }

  public boolean hasMd5() {
    return (md5 != null && !md5.isEmpty());
  }

  public boolean hasUniqueName() {
    return (uniqueName != null && !uniqueName.isEmpty());
  }

  public String getEditorsChoice() {
    return editorsChoice;
  }

  public String getOriginTag() {
    return originTag;
  }

  public String getCampaignUrl() {
    return campaignUrl;
  }
}
