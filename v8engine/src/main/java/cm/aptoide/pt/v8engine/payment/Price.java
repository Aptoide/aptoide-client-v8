/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

/**
 * Created by marcelobenites on 8/18/16.
 */
public class Price {

  private final double amount;
  private final String currency;
  private final String currencySymbol;
  private final double taxRate;

  public Price(double amount, String currency, String currencySymbol, double taxRate) {
    this.amount = amount;
    this.currency = currency;
    this.currencySymbol = currencySymbol;
    this.taxRate = taxRate;
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

  public double getTaxRate() {
    return taxRate;
  }
}