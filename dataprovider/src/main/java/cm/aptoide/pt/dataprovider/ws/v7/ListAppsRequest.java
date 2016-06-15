/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 27-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListAppsRequest extends BaseRequestWithStore<ListApps, ListAppsRequest.Body> {

	protected ListAppsRequest(V7Url v7Url, boolean bypassCache, OkHttpClient httpClient, Converter.Factory
			converterFactory) {
		super(v7Url.remove("listApps"), bypassCache, new Body(), httpClient, converterFactory);
	}

	protected ListAppsRequest(String storeName, boolean bypassCache, OkHttpClient httpClient, Converter.Factory
			converterFactory) {
		super(storeName, bypassCache, new Body(), httpClient, converterFactory);
	}

	protected ListAppsRequest(long storeId, boolean bypassCache, OkHttpClient httpClient, Converter.Factory
			converterFactory) {
		super(storeId, bypassCache, new Body(), httpClient, converterFactory);
	}

	public static ListAppsRequest ofAction(String url, boolean bypassCache) {
		return new ListAppsRequest(new V7Url(url), bypassCache, WebService.getDefaultHttpClient(), WebService.getDefaultConverter());
	}

	@Override
	protected Observable<ListApps> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.listApps(url, body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore implements OffsetInterface<Body> {

		private String lang = Api.LANG;
		private Integer limit;
		private boolean mature;
		private int offset;
		private Order order;
		private String q = Api.Q;
		private Sort sort;
		private Subgroups subgroups;

		public enum Sort {
			latest, downloads, downloads7d, downloads30d, pdownloads, pdownloads7d, pdownloads30d,
			trending7d, trending30d, rating, alpha,
		}

		public enum Subgroups {
			highlighted, normal,
		}
	}
}
