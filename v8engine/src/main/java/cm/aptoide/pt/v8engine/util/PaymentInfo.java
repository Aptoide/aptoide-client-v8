/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/07/2016.
 */

package cm.aptoide.pt.v8engine.util;

import java.math.BigDecimal;

import lombok.Getter;

/**
 * Created by sithengineer on 29/07/16.
 */
public final class PaymentInfo {

	@Getter private final String symbol;
	@Getter private final String threeLetterName;
	@Getter private final BigDecimal value;
	@Getter private final long appId;

	private PaymentInfo(String symbol, String threeLetterName, BigDecimal value, long appId) {
		this.symbol = symbol;
		this.threeLetterName = threeLetterName;
		this.value = value;
		this.appId = appId;
	}

	public static PaymentInfo inEuros(long appId, BigDecimal value) {
		return new PaymentInfo("€", "EUR", value, appId);
	}

	public static PaymentInfo inAmericanDollar(long appId, BigDecimal value) {
		return new PaymentInfo("$", "USD", value, appId);
	}
}
