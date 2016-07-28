/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import rx.Observable;

/**
 * Created by marcelobenites on 7/27/16.
 */
public class AdRepository {

	public Observable<MinimalAd> getAdFromAppView(String packageName, String storeName) {
		return GetAdsRequest.ofAppview(packageName, storeName).observe()
				.map(response -> response.getAds())
				.flatMap(ads -> {
					if (ads == null || ads.isEmpty() || ads.get(0) == null) {
						return Observable.error(new IllegalStateException("Invalid ads returned from server"));
					}
					return Observable.just(ads.get(0));
				})
				.map(ad -> MinimalAd.from(ad));
	}
}
