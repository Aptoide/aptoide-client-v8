/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.model.v3.PaymentResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 7/28/16.
 *
 * @author SithEngineer marcelobenites
 *
 */
public class CheckProductPaymentRequest extends V3<PaymentResponse> {

	private Map<String,String> args;

	private CheckProductPaymentRequest(String baseHost, Map<String,String> args) {
		super(baseHost);
		this.args = args;
	}

	public static CheckProductPaymentRequest ofInAppBilling(String paymentConfirmationId, int paymentId, int productId, double price, double taxRate, String currency, NetworkOperatorManager operatorManager, int apiVersion, String developerPayload) {
		final Map<String,String> args = new HashMap<>();
		addDefaultValues(paymentConfirmationId, paymentId, productId, price, taxRate, currency, operatorManager, args);
		args.put("reqtype", "iabpurchasestatus");
		args.put("apiversion", String.valueOf(apiVersion));
		args.put("developerPayload", developerPayload);
		return new CheckProductPaymentRequest(BASE_HOST, args);
	}

	public static CheckProductPaymentRequest ofPaidApp(String paymentConfirmationId, int paymentId, int productId, double price, double taxRate, String
			currency, NetworkOperatorManager operatorManager, String storeName) {
		final Map<String,String> args = new HashMap<>();
		addDefaultValues(paymentConfirmationId, paymentId, productId, price, taxRate, currency, operatorManager, args);
		args.put("reqtype", "apkpurchasestatus");
		args.put("repo", storeName);
		return new CheckProductPaymentRequest(BASE_HOST, args);
	}

	private static void addDefaultValues(String paymentConfirmationId, int paymentId, int productId, double price, double taxRate, String currency, NetworkOperatorManager operatorManager, Map<String,String> args) {

		args.put("mode", "json");
		args.put("payreqtype", "rest");
		args.put("paytype", String.valueOf(paymentId));
		args.put("paykey", paymentConfirmationId);
		args.put("taxrate", String.format(Locale.ROOT, "%.2f", taxRate));
		args.put("productid", String.valueOf(productId));
		args.put("price", String.format(Locale.ROOT, "%.2f", price));
		args.put("currency", currency);
		args.put("access_token", AptoideAccountManager.getAccessToken());

		if (operatorManager.isSimStateReady()) {
			args.put("simcc", operatorManager.getSimCountryISO());
		}
	}

	@Override
	protected Observable<PaymentResponse> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.checkProductPayment(args);
	}

}
