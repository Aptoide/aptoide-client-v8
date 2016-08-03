/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 03/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import java.util.HashMap;
import java.util.Map;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.realm.PaymentPayload;
import cm.aptoide.pt.model.v3.PaymentResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 7/28/16.
 *
 * @author SithEngineer marcelobenites
 *
 */
public class CheckProductPaymentRequest extends V3<PaymentResponse> {

	private CheckProductPaymentRequest(String baseHost, Map<String,String> args) {
		super(baseHost, args);
	}

	public static CheckProductPaymentRequest ofPayPal(PaymentPayload paymentPayload) {

		final Map<String,String> args = new HashMap<>();
		args.put("mode", "json");
		args.put("apiversion", paymentPayload.getApiVersion());
		args.put("reqtype", "apkpurchasestatus");
		args.put("paykey", paymentPayload.getPayKey());
		args.put("payreqtype", "rest");
		args.put("paytype", String.valueOf(paymentPayload.getPayType()));
		args.put("repo", paymentPayload.getStore());
		args.put("taxrate", String.valueOf(paymentPayload.getTaxRate()));
		args.put("productid", String.valueOf(paymentPayload.getProductId()));
		args.put("price", String.valueOf(paymentPayload.getPrice()));
		args.put("access_token", AptoideAccountManager.getAccessToken());
		args.put("currency", paymentPayload.getCurrency());
		args.put("simcc", paymentPayload.getSimCountryCode());

		return new CheckProductPaymentRequest(BASE_HOST, args);
	}

	@Override
	protected Observable<PaymentResponse> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.checkProductPayment(args);
	}

	public static class Constants {

		public static final String STORE = "store";
		public static final String PRICE = "price";
		public static final String CURRENCY = "currency";
		public static final String TAX_RATE = "taxRate";
	}
}
