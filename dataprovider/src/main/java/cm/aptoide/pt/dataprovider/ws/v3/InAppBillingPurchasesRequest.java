/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.support.annotation.NonNull;

import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import java.util.HashMap;
import java.util.Map;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 8/12/16.
 */
public class InAppBillingPurchasesRequest extends V3<InAppBillingPurchasesResponse> {

	private HashMapNotNull<String,String> args;

	public static InAppBillingPurchasesRequest of(int apiVersion, String packageName, String type) {
		HashMapNotNull<String,String> args = getBaseArgs(apiVersion, packageName, type);
		return new InAppBillingPurchasesRequest(BASE_HOST, args);
	}

	@NonNull
	private static HashMapNotNull<String,String> getBaseArgs(int apiVersion, String packageName, String type) {
		HashMapNotNull<String, String> args = new HashMapNotNull<String, String>();
		args.put("mode","json");
		args.put("package", packageName);
		args.put("apiversion", String.valueOf(apiVersion));
		args.put("reqtype", "iabpurchases");
		args.put("access_token", AptoideAccountManager.getAccessToken());
		args.put("purchasetype",type);
		return args;
	}

	private InAppBillingPurchasesRequest(String baseHost, HashMapNotNull<String,String> args) {
		super(baseHost);
		this.args = args;
	}

	@Override
	protected Observable<InAppBillingPurchasesResponse> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getInAppBillingPurchases(args);
	}

}
