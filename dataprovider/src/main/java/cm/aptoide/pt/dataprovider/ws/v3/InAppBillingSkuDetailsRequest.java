/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 8/11/16.
 */
public class InAppBillingSkuDetailsRequest extends V3<InAppBillingSkuDetailsResponse> {

	private final Map<String,String> args;

	public static InAppBillingSkuDetailsRequest of(int apiVersion, String packageName, List<String> skuList, NetworkOperatorManager operatorManager,
	                                               String type) {
		HashMap<String, String> args = new HashMap<>();
		args.put("mode","json");
		args.put("package", packageName);
		args.put("apiversion", String.valueOf(apiVersion));
		args.put("reqtype", "iabskudetails");
		args.put("purchasetype", type);
		args.put("access_token", AptoideAccountManager.getAccessToken());

		if (!skuList.isEmpty()) {
			final StringBuilder stringBuilder = new StringBuilder();
			for (String sku : skuList) {
				stringBuilder.append(sku);
				stringBuilder.append(",");
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			args.put("skulist", stringBuilder.toString());
		}

		if (operatorManager.isSimStateReady()) {
			args.put("mcc", operatorManager.getMobileCountryCode());
			args.put("mnc", operatorManager.getMobileNetworkCode());
			args.put("simcc", operatorManager.getSimCountryISO());
		}

		return new InAppBillingSkuDetailsRequest(BASE_HOST, args);
	}

	public InAppBillingSkuDetailsRequest(String baseHost, Map<String,String> args) {
		super(baseHost);
		this.args = args;
	}

	@Override
	protected Observable<InAppBillingSkuDetailsResponse> loadDataFromNetwork(V3.Interfaces interfaces, boolean bypassCache) {
		return interfaces.getInAppBillingSkuDetails(args);
	}

}
