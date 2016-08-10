/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 08/08/2016.
 */

package cm.aptoide.pt.model.v3;

import lombok.Data;

/**
 * Created by sithengineer on 03/08/16.
 */
@Data
public class PaymentPayload {

	private String payKey;
	private String apiVersion;
	private int payType;
	private String store;
	private double taxRate;
	private long aptoidePaymentId;
	private double price;
	private String currency;
	private String simCountryCode;

	// intenal var
	private int attempts;

	public void incrementAttempts() {
		attempts++;
	}
}
