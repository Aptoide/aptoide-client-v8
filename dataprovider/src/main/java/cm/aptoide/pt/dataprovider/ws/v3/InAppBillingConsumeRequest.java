/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import java.util.HashMap;
import java.util.Map;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.model.v3.BaseV3Response;
import rx.Observable;

/**
 * Created by marcelobenites on 8/12/16.
 */
public class InAppBillingConsumeRequest extends V3<BaseV3Response> {

	private Map<String,String> args;

	public static InAppBillingConsumeRequest of(int apiVersion, String packageName, String purchaseToken) {
		Map<String, String> args = new HashMap<String, String>();
		args.put("mode","json");
		args.put("package", packageName);
		args.put("apiversion", String.valueOf(apiVersion));
		args.put("reqtype", "iabconsume");
		args.put("purchasetoken", purchaseToken);
		args.put("access_token", AptoideAccountManager.getAccessToken());
		return new InAppBillingConsumeRequest(BASE_HOST, args);
	}

	private InAppBillingConsumeRequest(String baseHost, Map<String,String> args) {
		super(baseHost);
		this.args = args;
	}

	@Override
	protected Observable<BaseV3Response> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.deleteInAppBillingPurchase(args);
	}
}
