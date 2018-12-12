package cm.aptoide.pt.promotions;

import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
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

  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;

  public PromotionsAnalytics(AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
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
