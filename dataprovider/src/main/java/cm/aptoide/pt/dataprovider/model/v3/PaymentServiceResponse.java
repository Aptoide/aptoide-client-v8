/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentServiceResponse {

  @JsonProperty("id") private int id;

  @JsonProperty("name") private String name;

  @JsonProperty("description") private String description;

  @JsonProperty("price") private double price;

  @JsonProperty("currency") private String currency;

  @JsonProperty("tax_rate") private double taxRate;

  @JsonProperty("sign") private String sign;

  @JsonProperty("needsauth") private boolean authorizationRequired;

  public PaymentServiceResponse() {
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
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

  public double getPrice() {
    return this.price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getCurrency() {
    return this.currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public double getTaxRate() {
    return this.taxRate;
  }

  public void setTaxRate(double taxRate) {
    this.taxRate = taxRate;
  }

  public String getSign() {
    return this.sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public boolean isAuthorizationRequired() {
    return this.authorizationRequired;
  }

  public void setAuthorizationRequired(boolean authorizationRequired) {
    this.authorizationRequired = authorizationRequired;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + this.getId();
    final Object $name = this.getName();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    final Object $description = this.getDescription();
    result = result * PRIME + ($description == null ? 43 : $description.hashCode());
    final long $price = Double.doubleToLongBits(this.getPrice());
    result = result * PRIME + (int) ($price >>> 32 ^ $price);
    final Object $currency = this.getCurrency();
    result = result * PRIME + ($currency == null ? 43 : $currency.hashCode());
    final long $taxRate = Double.doubleToLongBits(this.getTaxRate());
    result = result * PRIME + (int) ($taxRate >>> 32 ^ $taxRate);
    final Object $sign = this.getSign();
    result = result * PRIME + ($sign == null ? 43 : $sign.hashCode());
    result = result * PRIME + (this.isAuthorizationRequired() ? 79 : 97);
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof PaymentServiceResponse)) return false;
    final PaymentServiceResponse other = (PaymentServiceResponse) o;
    if (!other.canEqual(this)) return false;
    if (this.getId() != other.getId()) return false;
    final Object this$name = this.getName();
    final Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    final Object this$description = this.getDescription();
    final Object other$description = other.getDescription();
    if (this$description == null ? other$description != null
        : !this$description.equals(other$description)) {
      return false;
    }
    if (Double.compare(this.getPrice(), other.getPrice()) != 0) return false;
    final Object this$currency = this.getCurrency();
    final Object other$currency = other.getCurrency();
    if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
      return false;
    }
    if (Double.compare(this.getTaxRate(), other.getTaxRate()) != 0) return false;
    final Object this$sign = this.getSign();
    final Object other$sign = other.getSign();
    if (this$sign == null ? other$sign != null : !this$sign.equals(other$sign)) return false;
    return this.isAuthorizationRequired() == other.isAuthorizationRequired();
  }

  public String toString() {
    return "PaymentServiceResponse(id="
        + this.getId()
        + ", name="
        + this.getName()
        + ", description="
        + this.getDescription()
        + ", price="
        + this.getPrice()
        + ", currency="
        + this.getCurrency()
        + ", taxRate="
        + this.getTaxRate()
        + ", sign="
        + this.getSign()
        + ", authorizationRequired="
        + this.isAuthorizationRequired()
        + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof PaymentServiceResponse;
  }
}
