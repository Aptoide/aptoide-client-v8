/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import java.util.HashMap;
import java.util.Map;

import cm.aptoide.pt.model.v3.BaseV3Response;
import rx.Observable;

/**
 * Created by marcelobenites on 8/11/16.
 */
public class InAppBillingAvailableRequest extends V3<BaseV3Response> {

	private Map<String,String> args;

	public static InAppBillingAvailableRequest of(int apiVersion, String packageName) {
		final Map<String,String> args = new HashMap<>();
		args.put("mode", "json");
		args.put("apiversion", String.valueOf(apiVersion));
		args.put("reqtype", "iabavailable");
		args.put("package", packageName);
		return new InAppBillingAvailableRequest(BASE_HOST, args);
	}

	public InAppBillingAvailableRequest(String baseHost, Map<String,String> args) {
		super(baseHost);
		this.args = args;
	}

	@Override
	protected Observable<BaseV3Response> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getInAppBillingAvailable(args);
	}
}
