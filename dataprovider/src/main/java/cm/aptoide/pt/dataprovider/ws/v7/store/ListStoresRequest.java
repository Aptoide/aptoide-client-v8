/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.OffsetInterface;
import cm.aptoide.pt.dataprovider.ws.v7.Order;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 27-04-2016.
 */
public class ListStoresRequest extends V7<ListStores, ListStoresRequest.Body> {

	private final String url;

	private ListStoresRequest(OkHttpClient httpClient, Converter.Factory converterFactory, String aptoideId, String accessToken, int versionCode, String cdn) {
		this("", httpClient, converterFactory, aptoideId, accessToken, versionCode, cdn);
	}

	private ListStoresRequest(String url, OkHttpClient httpClient, Converter.Factory converterFactory, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, BASE_HOST);
		this.url = url.replace("listStores", "");
	}

	public static ListStoresRequest of() {
		return new ListStoresRequest(OkHttpClientFactory.getSingletoneClient(), WebService
				.getDefaultConverter(), SecurePreferences
				.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");
	}

	public static ListStoresRequest ofAction(String url) {
		return new ListStoresRequest(url, OkHttpClientFactory.getSingletoneClient(), WebService
				.getDefaultConverter(), SecurePreferences.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");
	}

	@Override
	protected Observable<ListStores> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.listStores(url, body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody implements OffsetInterface<Body> {

		private Group group;
		private Integer limit;
		private int offset;
		private Order order;
		private Sort sort;

		public enum Group {
			featured
		}

		public enum Sort {
			latest, downloads, downloads7d, downloads30d, trending7d, trending30d,
		}

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn) {
			super(aptoideId, accessToken, aptoideVercode, cdn);
		}
	}
}
