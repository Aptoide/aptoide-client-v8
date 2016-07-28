/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 28/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.model.v3.PaymentResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 7/28/16.
 */
public class CheckProductPaymentRequest extends V3<PaymentResponse> {

	private CheckProductPaymentRequest(String baseHost, Map<String,String> args) {
		super(baseHost, args);
	}

	public static CheckProductPaymentRequest ofPayPal(String payKey, String apiVersion, int payType, String repo, double taxRate, int productId, double price, String currency, String simcc) {

		final Map<String, String> args = new HashMap<>();

		args.put("access_token", AptoideAccountManager.getAccessToken());
		args.put("mode","json");
		args.put("apiversion", apiVersion);
		args.put("reqtype", "apkpurchasestatus");
		args.put("paykey", payKey);
		args.put("payreqtype","rest");
		args.put("paytype", String.valueOf(payType));

		args.put("repo", repo);
		args.put("taxrate", String.valueOf(taxRate));
		args.put("productid", String.valueOf(productId));
		args.put("price", String.valueOf(price));
		args.put("currency", currency);
		args.put("simcc", simcc);

		return new CheckProductPaymentRequest(BASE_HOST, args);
	}

	@Override
	protected Observable<PaymentResponse> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.checkProductPayment(args);
	}
}
