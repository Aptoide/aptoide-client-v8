/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v2.aptwords;

import android.os.Build;

import java.util.HashMap;
import java.util.Map;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import lombok.Data;
import rx.Observable;

/**
 * Created by rmateus on 29-07-2014.
 */
public class RegisterAdRefererRequest extends Aptwords<RegisterAdRefererRequest.DefaultResponse> {

	private long adId;
	private long appId;
	private String tracker;
	private String success;

	private RegisterAdRefererRequest(long adId, long appId, String clickUrl, boolean success) {
		super(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()));
		this.adId = adId;
		this.appId = appId;
		this.success = (success ? "1" : "0");

		extractAndSetTracker(clickUrl);
	}

	public static RegisterAdRefererRequest of(long adId, long appId, String clickUrl, boolean success) {
		return new RegisterAdRefererRequest(adId, appId, clickUrl, success);
	}

	public static RegisterAdRefererRequest of(GetAdsResponse.Ad ad, boolean success) {
		long appId = ad.getData().getId();
		long adId = ad.getInfo().getAdId();
		String clickUrl = DataproviderUtils.AdNetworksUtils.parseMacros(ad.getPartner().getData().getClickUrl());

		return of(adId, appId, clickUrl, success);
	}

	public void execute() {
		super.execute(defaultResponse -> {
			// Does nothing
		}, e -> {
			// As well :)
		});
	}

	private void extractAndSetTracker(String clickUrl) {
		int i = clickUrl.indexOf("//");

		int last = clickUrl.indexOf("/", i + 2);

		tracker = clickUrl.substring(0, last);
	}

	@Override
	protected Observable<DefaultResponse> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {

		Map<String,String> map = new HashMap<>();

		map.put("success", success);
		map.put("adid", Long.toString(adId));
		map.put("appid", Long.toString(appId));
		map.put("q", Api.Q);
		map.put("androidversion", Build.VERSION.RELEASE);
		map.put("tracker", tracker);

		return interfaces.load(map);
	}

	@Data
	public static class DefaultResponse {

		String status;
	}
}
