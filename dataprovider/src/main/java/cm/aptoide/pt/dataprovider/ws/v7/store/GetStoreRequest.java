/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.OffsetInterface;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 19-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetStoreRequest extends BaseRequestWithStore<GetStore, GetStoreRequest.Body> {

	private boolean recursive = false;

	private GetStoreRequest(V7Url v7Url, boolean bypassCache, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(v7Url.remove("getStore"), bypassCache, new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, baseHost);
	}

	private GetStoreRequest(String storeName, boolean bypassCache, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(storeName, bypassCache, new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, baseHost);
	}

	private GetStoreRequest(long storeId, boolean bypassCache, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(storeId, bypassCache, new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, baseHost);
	}

	public static GetStoreRequest of(String storeName, boolean bypassCache) {
		return new GetStoreRequest(storeName, bypassCache, OkHttpClientFactory.getSingletoneClient(),
				WebService.getDefaultConverter(), BASE_HOST, SecurePreferences.getAptoideClientUUID(),
				AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");
	}

	public static GetStoreRequest of(String storeName, StoreContext storeContext, boolean bypassCache) {
		GetStoreRequest getStoreRequest = new GetStoreRequest(storeName, bypassCache,
				OkHttpClientFactory.getSingletoneClient(),
				WebService.getDefaultConverter(), BASE_HOST, SecurePreferences.getAptoideClientUUID(),
				AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");

		getStoreRequest.body.setContext(storeContext);

		return getStoreRequest;
	}

	public static GetStoreRequest ofAction(String url, boolean bypassCache) {
		return new GetStoreRequest(new V7Url(url), bypassCache, OkHttpClientFactory.getSingletoneClient(),
				WebService.getDefaultConverter(), BASE_HOST, SecurePreferences.getAptoideClientUUID(),
				AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");
	}

	@Override
	protected Observable<GetStore> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.getStore(url, body, bypassCache);
	}

	@Override
	public Observable<GetStore> observe() {
		// Todo: deprecated parece-me o recursive

		if (recursive) {
			return super.observe().observeOn(Schedulers.io()).doOnNext(getStore -> {

				List<GetStoreWidgets.WSWidget> list = getStore.getNodes().getWidgets().getDatalist().getList();
				CountDownLatch countDownLatch = new CountDownLatch(list.size());

				Observable.from(list)
						.forEach(wsWidget -> WSWidgetsUtils.loadInnerNodes(wsWidget, countDownLatch, bypassCache,
								Logger::printException));

				try {
					countDownLatch.await();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}).observeOn(AndroidSchedulers.mainThread());
		} else {
			return super.observe().observeOn(AndroidSchedulers.mainThread());
		}
	}

	public void execute(SuccessRequestListener<GetStore> successRequestListener, boolean recursive) {
		this.recursive = recursive;
		execute(successRequestListener);
	}

	public void execute(SuccessRequestListener<GetStore> successRequestListener, ErrorRequestListener
			errorRequestListener, boolean recursive) {
		this.recursive = recursive;
		super.execute(successRequestListener, errorRequestListener);
	}

	public enum StoreNodes {
		meta, tabs, widgets;

		public static List<StoreNodes> list() {
			return Arrays.asList(values());
		}
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore implements OffsetInterface<Body> {

		private StoreContext context;
		private String lang = Api.LANG;
		private Integer limit;
		private Boolean mature = Api.MATURE;
		private List<StoreNodes> nodes;
		private int offset;
		private String q = Api.Q;
		private String widget;
		private WidgetsArgs widgetsArgs = WidgetsArgs.createDefault();

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn) {
			super(aptoideId, accessToken, aptoideVercode, cdn);
		}
	}
}
