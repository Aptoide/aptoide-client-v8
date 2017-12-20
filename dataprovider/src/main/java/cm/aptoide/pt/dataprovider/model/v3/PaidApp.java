/*
 * Copyright (c) 2016.
 * Modified on 17/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Created by marcelobenites on 7/27/16.
 */
public class PaidApp extends BaseV3Response {

  @JsonProperty("apk") public Path path;
  @JsonProperty("payment") private Payment payment;
  @JsonProperty("meta") private App app;

  public PaidApp() {
  }

  public boolean isPaid() {
    return (payment != null
        && payment.getAmount() != null
        && payment.getAmount()
        .floatValue() > 0.0f);
  }

  public Path getPath() {
    return this.path;
  }

  public void setPath(Path path) {
    this.path = path;
  }

  public Payment getPayment() {
    return this.payment;
  }

  public void setPayment(Payment payment) {
    this.payment = payment;
  }

  public App getApp() {
    return this.app;
  }

  public void setApp(App app) {
    this.app = app;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $path = this.getPath();
    result = result * PRIME + ($path == null ? 43 : $path.hashCode());
    final Object $payment = this.getPayment();
    result = result * PRIME + ($payment == null ? 43 : $payment.hashCode());
    final Object $app = this.getApp();
    result = result * PRIME + ($app == null ? 43 : $app.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof PaidApp)) return false;
    final PaidApp other = (PaidApp) o;
    if (!other.canEqual(this)) return false;
    if (!super.equals(o)) return false;
    final Object this$path = this.getPath();
    final Object other$path = other.getPath();
    if (this$path == null ? other$path != null : !this$path.equals(other$path)) return false;
    final Object this$payment = this.getPayment();
    final Object other$payment = other.getPayment();
    if (this$payment == null ? other$payment != null : !this$payment.equals(other$payment)) {
      return false;
    }
    final Object this$app = this.getApp();
    final Object other$app = other.getApp();
    return this$app == null ? other$app == null : this$app.equals(other$app);
  }

  public String toString() {
    return "PaidApp(path="
        + this.getPath()
        + ", payment="
        + this.getPayment()
        + ", app="
        + this.getApp()
        + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof PaidApp;
  }

  public static class Payment {

    @JsonProperty("amount") private Double amount;

    @JsonProperty("currency_symbol") private String symbol;

    @JsonProperty("metadata") private Metadata metadata;

    @JsonProperty("payment_services") private List<PaymentServiceResponse> paymentServices;

    @JsonProperty("status") private String status;

    public Payment() {
    }

    public boolean isPaid() {
      return status.equalsIgnoreCase("OK");
    }

    public Double getAmount() {
      return this.amount;
    }

    public void setAmount(Double amount) {
      this.amount = amount;
    }

    public String getSymbol() {
      return this.symbol;
    }

    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }

    public Metadata getMetadata() {
      return this.metadata;
    }

    public void setMetadata(Metadata metadata) {
      this.metadata = metadata;
    }

    public List<PaymentServiceResponse> getPaymentServices() {
      return this.paymentServices;
    }

    public void setPaymentServices(List<PaymentServiceResponse> paymentServices) {
      this.paymentServices = paymentServices;
    }

    public String getStatus() {
      return this.status;
    }

    public void setStatus(String status) {
      this.status = status;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $amount = this.getAmount();
      result = result * PRIME + ($amount == null ? 43 : $amount.hashCode());
      final Object $symbol = this.getSymbol();
      result = result * PRIME + ($symbol == null ? 43 : $symbol.hashCode());
      final Object $metadata = this.getMetadata();
      result = result * PRIME + ($metadata == null ? 43 : $metadata.hashCode());
      final Object $paymentServices = this.getPaymentServices();
      result = result * PRIME + ($paymentServices == null ? 43 : $paymentServices.hashCode());
      final Object $status = this.getStatus();
      result = result * PRIME + ($status == null ? 43 : $status.hashCode());
      return result;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Payment;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Payment)) return false;
      final Payment other = (Payment) o;
      if (!other.canEqual(this)) return false;
      final Object this$amount = this.getAmount();
      final Object other$amount = other.getAmount();
      if (this$amount == null ? other$amount != null : !this$amount.equals(other$amount)) {
        return false;
      }
      final Object this$symbol = this.getSymbol();
      final Object other$symbol = other.getSymbol();
      if (this$symbol == null ? other$symbol != null : !this$symbol.equals(other$symbol)) {
        return false;
      }
      final Object this$metadata = this.getMetadata();
      final Object other$metadata = other.getMetadata();
      if (this$metadata == null ? other$metadata != null : !this$metadata.equals(other$metadata)) {
        return false;
      }
      final Object this$paymentServices = this.getPaymentServices();
      final Object other$paymentServices = other.getPaymentServices();
      if (this$paymentServices == null ? other$paymentServices != null
          : !this$paymentServices.equals(other$paymentServices)) {
        return false;
      }
      final Object this$status = this.getStatus();
      final Object other$status = other.getStatus();
      return this$status == null ? other$status == null : this$status.equals(other$status);
    }

    public String toString() {
      return "PaidApp.Payment(amount="
          + this.getAmount()
          + ", symbol="
          + this.getSymbol()
          + ", metadata="
          + this.getMetadata()
          + ", paymentServices="
          + this.getPaymentServices()
          + ", status="
          + this.getStatus()
          + ")";
    }
  }

  public static class Metadata {

    @JsonProperty("id") private int productId;

    public Metadata() {
    }

    public int getProductId() {
      return this.productId;
    }

    public void setProductId(int productId) {
      this.productId = productId;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Metadata;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Metadata)) return false;
      final Metadata other = (Metadata) o;
      if (!other.canEqual(this)) return false;
      return this.getProductId() == other.getProductId();
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + this.getProductId();
      return result;
    }

    public String toString() {
      return "PaidApp.Metadata(productId=" + this.getProductId() + ")";
    }
  }

  public static class Path {

    @JsonProperty("path") private String stringPath;
    @JsonProperty("id") private int appId;
    @JsonProperty("repo") private String storeName;
    @JsonProperty("icon_hd") private String icon;
    @JsonProperty("vercode") private int versionCode;
    @JsonProperty("icon") private String alternativeIcon;

    public Path() {
    }

    public String getStringPath() {
      return this.stringPath;
    }

    public void setStringPath(String stringPath) {
      this.stringPath = stringPath;
    }

    public int getAppId() {
      return this.appId;
    }

    public void setAppId(int appId) {
      this.appId = appId;
    }

    public String getStoreName() {
      return this.storeName;
    }

    public void setStoreName(String storeName) {
      this.storeName = storeName;
    }

    public String getIcon() {
      return this.icon;
    }

    public void setIcon(String icon) {
      this.icon = icon;
    }

    public int getVersionCode() {
      return this.versionCode;
    }

    public void setVersionCode(int versionCode) {
      this.versionCode = versionCode;
    }

    public String getAlternativeIcon() {
      return this.alternativeIcon;
    }

    public void setAlternativeIcon(String alternativeIcon) {
      this.alternativeIcon = alternativeIcon;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Path;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Path)) return false;
      final Path other = (Path) o;
      if (!other.canEqual(this)) return false;
      final Object this$stringPath = this.getStringPath();
      final Object other$stringPath = other.getStringPath();
      if (this$stringPath == null ? other$stringPath != null
          : !this$stringPath.equals(other$stringPath)) {
        return false;
      }
      if (this.getAppId() != other.getAppId()) return false;
      final Object this$storeName = this.getStoreName();
      final Object other$storeName = other.getStoreName();
      if (this$storeName == null ? other$storeName != null
          : !this$storeName.equals(other$storeName)) {
        return false;
      }
      final Object this$icon = this.getIcon();
      final Object other$icon = other.getIcon();
      if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) return false;
      if (this.getVersionCode() != other.getVersionCode()) return false;
      final Object this$alternativeIcon = this.getAlternativeIcon();
      final Object other$alternativeIcon = other.getAlternativeIcon();
      return this$alternativeIcon == null ? other$alternativeIcon == null
          : this$alternativeIcon.equals(other$alternativeIcon);
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $stringPath = this.getStringPath();
      result = result * PRIME + ($stringPath == null ? 43 : $stringPath.hashCode());
      result = result * PRIME + this.getAppId();
      final Object $storeName = this.getStoreName();
      result = result * PRIME + ($storeName == null ? 43 : $storeName.hashCode());
      final Object $icon = this.getIcon();
      result = result * PRIME + ($icon == null ? 43 : $icon.hashCode());
      result = result * PRIME + this.getVersionCode();
      final Object $alternativeIcon = this.getAlternativeIcon();
      result = result * PRIME + ($alternativeIcon == null ? 43 : $alternativeIcon.hashCode());
      return result;
    }

    public String toString() {
      return "PaidApp.Path(stringPath="
          + this.getStringPath()
          + ", appId="
          + this.getAppId()
          + ", storeName="
          + this.getStoreName()
          + ", icon="
          + this.getIcon()
          + ", versionCode="
          + this.getVersionCode()
          + ", alternativeIcon="
          + this.getAlternativeIcon()
          + ")";
    }
  }

  public static class App {

    @JsonProperty("title") private String name;
    @JsonProperty("description") private String description;

    public App() {
    }

    public String getName() {
      return this.name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getDescription() {
      return this.description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    protected boolean canEqual(Object other) {
      return other instanceof App;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof App)) return false;
      final App other = (App) o;
      if (!other.canEqual(this)) return false;
      final Object this$name = this.getName();
      final Object other$name = other.getName();
      if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
      final Object this$description = this.getDescription();
      final Object other$description = other.getDescription();
      return this$description == null ? other$description == null
          : this$description.equals(other$description);
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $name = this.getName();
      result = result * PRIME + ($name == null ? 43 : $name.hashCode());
      final Object $description = this.getDescription();
      result = result * PRIME + ($description == null ? 43 : $description.hashCode());
      return result;
    }

    public String toString() {
      return "PaidApp.App(name=" + this.getName() + ", description=" + this.getDescription() + ")";
    }
  }
}
