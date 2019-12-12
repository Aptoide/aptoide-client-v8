package cm.aptoide.pt.ads.data;

import java.util.Arrays;
import org.parceler.Parcel;

@Parcel public class Payout {
  double appcAmount;
  double fiatAmount;
  String fiatCurrency;
  String fiatSymbol;

  public Payout() {
  }

  public Payout(double appcAmount, double fiatAmount, String fiatCurrency, String fiatSymbol) {
    this.appcAmount = appcAmount;
    this.fiatAmount = fiatAmount;
    this.fiatCurrency = fiatCurrency;
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

  public String getFiatCurrency() {
    return fiatCurrency;
  }

  public void setFiatCurrency(String fiatCurrency) {
    this.fiatCurrency = fiatCurrency;
  }

  public String getFiatSymbol() {
    return fiatSymbol;
  }

  public void setFiatSymbol(String fiatSymbol) {
    this.fiatSymbol = fiatSymbol;
  }

  @Override public int hashCode() {
    return Arrays.deepHashCode(new Object[] { appcAmount, fiatAmount, fiatCurrency, fiatSymbol });
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Payout payout = (Payout) o;
    return Double.compare(payout.appcAmount, appcAmount) == 0
        && Double.compare(payout.fiatAmount, fiatAmount) == 0
        && fiatCurrency.equals(payout.fiatCurrency)
        && fiatSymbol.equals(payout.fiatSymbol);
  }
}
