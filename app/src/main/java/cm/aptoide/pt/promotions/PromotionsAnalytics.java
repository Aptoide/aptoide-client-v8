package cm.aptoide.pt.promotions;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.DownloadAnalytics;
import java.util.HashMap;
import java.util.Map;

public class PromotionsAnalytics {
  public static final String PROMOTION_DIALOG = "Promotion_Dialog";
  final String NEXT = "next";
  final String CANCEL = "cancel";
  final String OPEN_WALLET = "open wallet";
  final String CLAIM = "claim";
  final String WALLET_DIALOG = "wallet dialog";
  final String CAPTCHA_DIALOG = "captcha dialog";

  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;
  private final DownloadAnalytics downloadAnalytics;


  public PromotionsAnalytics(AnalyticsManager analyticsManager, NavigationTracker navigationTracker,
      DownloadAnalytics downloadAnalytics) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
    this.downloadAnalytics = downloadAnalytics;
  }

  public void setupDownloadEvents(Download download, int campaignId, String abTestGroup,
      AnalyticsManager.Action action) {
    downloadAnalytics.downloadStartEvent(download, campaignId, abTestGroup,
        DownloadAnalytics.AppContext.PROMOTIONS, action);
  }

  public void sendClickOnWalletDialogNext(String packageName) {
    final Map<String, Object> data = new HashMap<>();
    data.put("action", NEXT);
    data.put("package", packageName);
    data.put("view", WALLET_DIALOG);

    analyticsManager.logEvent(data, PROMOTION_DIALOG, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendClickOnWalletDialogCancel(String packageName) {
    final Map<String, Object> data = new HashMap<>();
    data.put("action", CANCEL);
    data.put("package", packageName);
    data.put("view", WALLET_DIALOG);

    analyticsManager.logEvent(data, PROMOTION_DIALOG, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendClickOnWalletDialogFindWallet(String packageName) {
    final Map<String, Object> data = new HashMap<>();
    data.put("action", OPEN_WALLET);
    data.put("package", packageName);
    data.put("view", WALLET_DIALOG);

    analyticsManager.logEvent(data, PROMOTION_DIALOG, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendClickOnCaptchaDialogClaim(String packageName) {
    final Map<String, Object> data = new HashMap<>();
    data.put("action", CLAIM);
    data.put("package", packageName);
    data.put("view", CAPTCHA_DIALOG);

    analyticsManager.logEvent(data, PROMOTION_DIALOG, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }

  public void sendClickOnCaptchaDialogCancel(String packageName) {
    final Map<String, Object> data = new HashMap<>();
    data.put("action", CANCEL);
    data.put("package", packageName);
    data.put("view", CAPTCHA_DIALOG);

    analyticsManager.logEvent(data, PROMOTION_DIALOG, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }
}
