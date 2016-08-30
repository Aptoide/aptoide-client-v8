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
	private final double taxRate;

	public Price(double amount, String currency, double taxRate) {
		this.amount = amount;
		this.currency = currency;
		this.taxRate = taxRate;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Price price1 = (Price) o;

		if (Double.compare(price1.amount, amount) != 0) {
			return false;
		}
		if (Double.compare(price1.taxRate, taxRate) != 0) {
			return false;
		}
		if (!currency.equals(price1.currency)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = (int) (temp ^ (temp >>> 32));
		result = 31 * result + currency.hashCode();
		temp = Double.doubleToLongBits(taxRate);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
