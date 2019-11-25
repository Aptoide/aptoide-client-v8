package cm.aptoide.pt.ads.data;

import java.util.Arrays;

public class Payout {
  private double appcAmount;
  private double fiatAmount;
  private String fatCurrency;
  private String fiatSymbol;

  public Payout(double appcAmount, double fiatAmount, String fatCurrency, String fiatSymbol) {
    this.appcAmount = appcAmount;
    this.fiatAmount = fiatAmount;
    this.fatCurrency = fatCurrency;
    this.fiatSymbol = fiatSymbol;
  }

  public double getAppcAmount() {
    return appcAmount;
  }

  public void setAppcAmount(double appcAmount) {
    this.appcAmount = appcAmount;
  }

  public double getFiatAmount() {
    return fiatAmount;
  }

  public void setFiatAmount(double fiatAmount) {
    this.fiatAmount = fiatAmount;
  }

  public String getFatCurrency() {
    return fatCurrency;
  }

  public void setFatCurrency(String fatCurrency) {
    this.fatCurrency = fatCurrency;
  }

  public String getFiatSymbol() {
    return fiatSymbol;
  }

  public void setFiatSymbol(String fiatSymbol) {
    this.fiatSymbol = fiatSymbol;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Payout payout = (Payout) o;
    return Double.compare(payout.appcAmount, appcAmount) == 0
        && Double.compare(payout.fiatAmount, fiatAmount) == 0
        && fatCurrency.equals(payout.fatCurrency)
        && fiatSymbol.equals(payout.fiatSymbol);
  }

  @Override public int hashCode() {
    return Arrays.deepHashCode(new Object[] { appcAmount, fiatAmount, fatCurrency, fiatSymbol });
  }
}
