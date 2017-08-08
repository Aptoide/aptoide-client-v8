package cm.aptoide.pt.v8engine.billing;

import java.util.ArrayList;
import java.util.List;

public class BillingIdResolver {

  private final String divider;
  private final String applicationPackage;
  private final int billingApiVersion;

  public BillingIdResolver(int billingApiVersion, String applicationPackage) {
    this.divider = "/";
    this.applicationPackage = applicationPackage;
    this.billingApiVersion = billingApiVersion;
  }

  public String resolveApplicationId(String packageName, int apiVersion) {
    return apiVersion + divider + packageName;
  }

  public String resolveApplicationId() {
    return billingApiVersion + divider + applicationPackage;
  }

  public String resolveProductId(String sku) {
    return sku;
  }

  public String resolveProductId(long appId, String storeName, boolean sponsored) {
    return appId + divider + storeName + divider + sponsored;
  }

  public String resolvePackageName(String applicationId) {
    return applicationId.split(divider)[1];
  }

  public boolean isPaidAppId(String productId) {
    final String[] values = productId.split(divider);

    if (values.length == 3) {
      return true;
    }

    return false;
  }

  public boolean isInAppId(String productId) {
    final String[] values = productId.split(divider);

    if (values.length == 1) {
      return true;
    }

    return false;
  }

  public String resolveSku(String productId) {
    return productId.split(divider)[0];
  }

  public long resolveAppId(String productId) {
    return Long.valueOf(productId.split(divider)[0]);
  }

  public String resolveStoreName(String productId) {
    return productId.split(divider)[1];
  }

  public boolean resolveSponsored(String productId) {
    return Boolean.valueOf(productId.split(divider)[2]);
  }

  public int resolveApiVersion(String applicationId) {
    return Integer.valueOf(applicationId.split(divider)[0]);
  }

  public List<String> resolveProductIds(List<String> skuList) {
    final List<String> productIds = new ArrayList<>();
    for (String sku : skuList) {
      productIds.add(resolveProductId(sku));
    }
    return productIds;
  }
}
