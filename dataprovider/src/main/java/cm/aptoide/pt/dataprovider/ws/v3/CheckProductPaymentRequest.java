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

	public static CheckProductPaymentRequest of(String paymentConfirmationId, int productId, String packageName, int apiVersion, String currency, String developerPayload, double taxRate, double price, NetworkOperatorManager operatorManager) {
		final Map<String,String> args = new HashMap<>();
		addDefaultValues(paymentConfirmationId, productId, currency, taxRate, price, operatorManager, args);
		args.put("paytype", "iab");
		args.put("apiversion", String.valueOf(apiVersion));
		return new CheckProductPaymentRequest(BASE_HOST, args);
	}

	public static CheckProductPaymentRequest of(String paymentConfirmationId, int productId, String storeName, String currency, double taxRate, double price, NetworkOperatorManager operatorManager) {
		final Map<String,String> args = new HashMap<>();
		addDefaultValues(paymentConfirmationId, productId, currency, taxRate, price, operatorManager, args);
		args.put("paytype", String.valueOf("paidapp"));
		args.put("repo", storeName);
		return new CheckProductPaymentRequest(BASE_HOST, args);
	}

	//
	// sample WS payload
	//
//	paytype 1
//	reqtype apkpurchasestatus
//	price 0.87
//	access_token 6920a21f32ab3aba7029a32b85e1527159237864
//	payreqtype rest
//	mode json
//	apiversion null
//	currency EUR
//	taxrate 0.0
//	productid 827
//	paykey PAY-80505244U6383235AK6W3NVQ

	private static void addDefaultValues(String paymentConfirmationId, int productId, String currency, double taxRate, double price, NetworkOperatorManager
			operatorManager, Map<String,String> args) {

		args.put("mode", "json");
		args.put("payreqtype", "rest");
		args.put("reqtype", "apkpurchasestatus");
		args.put("paytype", String.valueOf(1));
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
