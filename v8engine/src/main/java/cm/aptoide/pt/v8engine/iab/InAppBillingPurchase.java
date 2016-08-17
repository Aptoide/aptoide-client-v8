/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.iab;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by marcelobenites on 8/12/16.
 */
@AllArgsConstructor
public class InAppBillingPurchase {

	@Getter
	private int orderId;
	@Getter
	private long purchaseTime;
	@Getter
	private String purchaseState;
	@Getter
	private String productId;
	@Getter
	private String packageName;
	@Getter
	private String token;
	@Getter
	private String purchaseToken;
	@Getter
	private String developerPayload;
	@Getter
	private String signature;
	@Getter
	private String item;

}
