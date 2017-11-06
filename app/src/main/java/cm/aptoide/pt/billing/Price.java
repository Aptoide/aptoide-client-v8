/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/08/2016.
 */

package cm.aptoide.pt.billing;

public class Price {

  private final double amount;
  private final String currency;
  private final String currencySymbol;

  public Price(double amount, String currency, String currencySymbol) {
    this.amount = amount;
    this.currency = currency;
    this.currencySymbol = currencySymbol;
  }

  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public double getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }
}