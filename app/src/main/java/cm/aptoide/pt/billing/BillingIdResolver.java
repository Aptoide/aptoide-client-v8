package cm.aptoide.pt.billing;

public class BillingIdResolver {

  private final String divider;
  private final String applicationPackage;
  private final String paidAppId;

  public BillingIdResolver(String applicationPackage, String divider, String paidAppId) {
    this.divider = divider;
    this.applicationPackage = applicationPackage;
    this.paidAppId = paidAppId;
  }

  public String resolveMerchantName(String storeName) {
    return applicationPackage + divider + storeName;
  }

  public String resolveProductId(long appId) {
    return appId + divider + paidAppId;
  }

  public String resolveStoreName(String merchantName) {
    return merchantName.split(divider)[1];
  }
}