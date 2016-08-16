/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import java.util.Locale;
import java.util.Map;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.model.v3.PaymentPayload;
import cm.aptoide.pt.model.v3.PaymentResponse;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
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

	public static CheckProductPaymentRequest ofPayPal(PaymentPayload paymentPayload) {

		final HashMapNotNull<String,String> args = new HashMapNotNull<>();
		args.put("paytype", String.valueOf(1));
		args.put("reqtype", "apkpurchasestatus");
		args.put("price", String.format(Locale.ROOT, "%.2f", paymentPayload.getPrice()));
		args.put("access_token", AptoideAccountManager.getAccessToken());
		args.put("payreqtype", "rest");
		args.put("mode", "json");
		//args.put("apiversion", null);
		args.put("currency", paymentPayload.getCurrency());
		args.put("taxrate", String.format(Locale.ROOT, "%.2f", paymentPayload.getTaxRate()));
		args.put("productid", String.valueOf(paymentPayload.getAptoidePaymentId()));
		args.put("paykey", paymentPayload.getPayKey());

		return new CheckProductPaymentRequest(BASE_HOST, args);
	}

	@Override
	protected Observable<PaymentResponse> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.checkProductPayment(args);
	}

}
