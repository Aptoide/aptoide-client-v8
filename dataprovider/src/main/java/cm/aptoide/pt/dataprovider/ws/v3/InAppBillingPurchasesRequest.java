/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 8/12/16.
 */
public class InAppBillingPurchasesRequest extends V3<InAppBillingPurchasesResponse> {

	private Map<String,String> args;

	public static InAppBillingPurchasesRequest of(int apiVersion, String packageName, String type) {
		Map<String,String> args = getBaseArgs(apiVersion, packageName, type);
		return new InAppBillingPurchasesRequest(BASE_HOST, args);
	}

	@NonNull
	private static Map<String,String> getBaseArgs(int apiVersion, String packageName, String type) {
		Map<String, String> args = new HashMap<String, String>();
		args.put("mode","json");
		args.put("package", packageName);
		args.put("apiversion", String.valueOf(apiVersion));
		args.put("reqtype", "iabpurchases");
		args.put("access_token", AptoideAccountManager.getAccessToken());
		args.put("purchasetype",type);
		return args;
	}

	private InAppBillingPurchasesRequest(String baseHost, Map<String,String> args) {
		super(baseHost);
		this.args = args;
	}

	@Override
	protected Observable<InAppBillingPurchasesResponse> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getInAppBillingPurchases(args);
	}

}
