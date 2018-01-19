package cm.aptoide.pt.billing;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.analytics.analytics.Event;
import cm.aptoide.pt.billing.payment.Payment;
import cm.aptoide.pt.billing.product.InAppProduct;
import cm.aptoide.pt.billing.product.Product;
import java.util.HashMap;
import java.util.Map;

public class BillingAnalytics {

  private final String aptoidePackageName;
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;

  public BillingAnalytics(String aptoidePackageName, AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.aptoidePackageName = aptoidePackageName;
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void sendPaymentViewShowEvent() {
    analyticsManager.logEvent(getFacebookPaymentEvent("Payment_Pop_Up", "Show", new HashMap<>(),true));
  }

  public void sendPaymentViewCancelEvent(Payment payment) {
    analyticsManager.logEvent(getFacebookPaymentEvent("Payment_Pop_Up", "Cancel",
        getProductMap(payment.getProduct()),true));

  }

  public void sendPaymentViewBuyEvent(Payment payment) {
    final Map<String,Object> map = getProductMap(payment.getProduct());
    map.put("payment_method", payment.getSelectedPaymentService()
        .getType());
    analyticsManager.logEvent(getFacebookPaymentEvent("Payment_Pop_Up", "Buy", map,true));
  }

  public void sendPaymentSuccessEvent() {
    analyticsManager.logEvent(getFacebookPaymentEvent("Payment_Pop_Up", "Success", new HashMap<>(), true));
  }

  public void sendPaymentErrorEvent() {
    analyticsManager.logEvent(getFacebookPaymentEvent("Payment_Pop_Up", "Error", new HashMap<>(),true));
  }

  public void sendCustomerAuthenticatedEvent(boolean customerAuthenticated) {
    if (customerAuthenticated) {
      analyticsManager.logEvent(getFacebookPaymentEvent("Payment_Login", "Success", new HashMap<>(),true));
    }
  }

  public void sendCustomerAuthenticationResultEvent(boolean customerAuthenticated) {
    final String action;
    if (customerAuthenticated) {
      action = "Success";
    } else {
      action = "Cancel";
    }
    analyticsManager.logEvent(getFacebookPaymentEvent("Payment_Login", action, new HashMap<>(),true));
  }

  public void sendAuthorizationSuccessEvent(Payment payment) {
    final Map<String,Object> map = new HashMap<>();
    map.put("payment_method", payment.getSelectedPaymentService()
        .getType());
    analyticsManager.logEvent(getFacebookPaymentEvent("Payment_Authorization_Page", "Success", map,true));
  }

  public void sendAuthorizationCancelEvent(String serviceName) {
    final Map<String, Object> map = new HashMap<>();
    map.put("payment_method", serviceName);
    analyticsManager.logEvent(getFacebookPaymentEvent("Payment_Authorization_Page", "Cancel", map,true));
  }

  public void sendAuthorizationErrorEvent(String serviceName) {
    final Map<String, Object> map = new HashMap<>();
    map.put("payment_method", serviceName);
    analyticsManager.logEvent(getFacebookPaymentEvent("Payment_Authorization_Page", "Error", map,true));
  }

  private Event getFacebookPaymentEvent(String eventName, String action, Map<String, Object> map, boolean isCurrent) {
    map.put("action", action);
    return new Event(eventName, map, AnalyticsManager.Action.CLICK, getViewName(isCurrent));
  }

  private Map<String, Object> getProductMap(Product product) {

    final String packageName;
    if (product instanceof InAppProduct) {
      packageName = ((InAppProduct) product).getPackageName();
    } else {
      packageName = aptoidePackageName;
    }

    final Map<String, Object> map = new HashMap<>();
    map.put("purchase_value", String.valueOf(product.getPrice()
        .getAmount()));
    map.put("purchase_currency", product.getPrice()
        .getCurrency());
    map.put("package_name_seller", packageName);
    map.put("package_version_code_seller", String.valueOf(((Product) product).getPackageVersionCode()));
    return map;
  }

  private String getViewName(boolean isCurrent){
    String viewName = "";
    if(isCurrent){
      viewName = navigationTracker.getCurrentViewName();
    }
    else{
      viewName = navigationTracker.getPreviousViewName();
    }
    if(viewName.equals("")) {
      return "BillingAnalytics"; //Default value, shouldn't get here
    }
    return viewName;
  }
}
