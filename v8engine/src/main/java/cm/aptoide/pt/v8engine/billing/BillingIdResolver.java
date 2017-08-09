package cm.aptoide.pt.v8engine.billing;

import java.util.ArrayList;
import java.util.List;

public class BillingIdResolver {

  private final String divider;
  private final String applicationPackage;
  private final String paidAppId;
  private final String inAppId;

  public BillingIdResolver(String applicationPackage, String divider, String paidAppId,
      String inAppId) {
    this.divider = divider;
    this.applicationPackage = applicationPackage;
    this.paidAppId = paidAppId;
    this.inAppId = inAppId;
  }

  public String resolveSellerId(String packageName) {
    return packageName;
  }

  public String resolveStoreSellerId(String storeName) {
    return applicationPackage + divider + storeName;
  }

  public String resolveProductId(String sku) {
    return sku + divider + inAppId;
  }

  public String resolveProductId(long appId) {
    return appId + divider + paidAppId;
  }

  public String resolvePackageName(String sellerId) {
    return sellerId;
  }

  public boolean isPaidAppId(String productId) {
    return productId.split(divider)[1].equals(paidAppId);
  }

  public boolean isInAppId(String productId) {
    return productId.split(divider)[1].equals(inAppId);
  }

  public String resolveSku(String productId) {
    return productId.split(divider)[0];
  }

  public long resolveAppId(String productId) {
    return Long.valueOf(productId.split(divider)[0]);
  }

  public List<String> resolveProductIds(List<String> skuList) {
    final List<String> productIds = new ArrayList<>();
    for (String sku : skuList) {
      productIds.add(resolveProductId(sku));
    }
    return productIds;
  }

  public String resolveStoreName(String sellerId) {
    return sellerId.split(divider)[1];
  }
}
