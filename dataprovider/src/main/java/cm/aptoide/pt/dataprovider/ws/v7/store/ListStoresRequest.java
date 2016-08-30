/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import android.text.TextUtils;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 27-04-2016.
 */
public class ListStoresRequest extends V7<ListStores, ListStoresRequest.Body> {

	public static final String STORT_BY_DOWNLOADS = "downloads7d";
	private String url;

	private ListStoresRequest(String url, OkHttpClient httpClient, Converter.Factory converterFactory, Body body, String baseHost) {
		super(body, httpClient, converterFactory, baseHost);
		this.url = url;
	}

	public ListStoresRequest(OkHttpClient httpClient, Converter.Factory converterFactory, Body body, String baseHost) {
		super(body, httpClient, converterFactory, baseHost);
	}

	public static ListStoresRequest ofTopStores(int offset, int limit) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());

		final Body baseBody = new Body();
		baseBody.setOffset(offset);
		baseBody.limit = limit;
		return new ListStoresRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), (Body) decorator.decorate(baseBody),
				BASE_HOST);
	}

	public static ListStoresRequest ofAction(String url) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());

		return new ListStoresRequest(url.replace("listStores", ""), OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), (Body)
				decorator.decorate(new
				Body
				()),
				BASE_HOST);
	}

	@Override
	protected Observable<ListStores> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		if (TextUtils.isEmpty(url)) {
			return interfaces.listTopStores(STORT_BY_DOWNLOADS, 10, body, bypassCache);
		} else {
			return interfaces.listStores(url, body, bypassCache);
		}
	}

	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody implements Endless {

		@Getter private Integer limit;
		@Getter @Setter private int offset;

		public Body() {
		}
	}
}
