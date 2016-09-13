/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.text.TextUtils;
import android.util.Log;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by sithengineer on 20/07/16.
 */

/**
 * http://ws2.aptoide.com/api/7/listFullComments/info/1
 * <p>
 * http://ws2.aptoide.com/api/7/listComments/info/1
 */
public class ListCommentsRequest extends V7<ListComments,ListCommentsRequest.Body> {

	private static final String BASE_HOST = "http://ws2.aptoide.com/api/7/";
	private static String url;

	protected ListCommentsRequest(Body body, String baseHost) {
		super(body, OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), baseHost);
	}

	public static ListCommentsRequest of(String url, long reviewId, int limit, String storeName) {
		Log.d("lou", "of: A");
		ListCommentsRequest.url = url;
		return of(reviewId, limit, storeName);
	}

	public static ListCommentsRequest of(long reviewId, int offset, int limit) {
		Log.d("lou", "of: B");
		ListCommentsRequest listCommentsRequest = of(reviewId, limit);
		listCommentsRequest.getBody().setOffset(offset);
		return listCommentsRequest;
	}

	public static ListCommentsRequest of(long reviewId, int limit) {
		Log.d("lou", "of: C");
		//
		//
		//
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());


		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(limit, reviewId, ManagerPreferences.getAndResetForceServerRefresh(), Order.desc);
		return new ListCommentsRequest((Body) decorator.decorate(body), BASE_HOST);
	}

	public static ListCommentsRequest of(long reviewId, int limit, String storeName) {
		Log.d("lou", "of: D");
		//
		//
		//
		final StoreCredentialsApp storeOnRequest = getStoreOnRequest(storeName);
		String username = storeOnRequest.getUsername();
		String password = storeOnRequest.getPasswordSha1();
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());


		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(limit, reviewId, ManagerPreferences.getAndResetForceServerRefresh(), Order.desc, username, password);
		return new ListCommentsRequest((Body) decorator.decorate(body), BASE_HOST);
	}

	@Override
	protected Observable<ListComments> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		if (TextUtils.isEmpty(url)) {
			return interfaces.listComments(body, bypassCache);
		} else {
			return interfaces.listComments(url, body, bypassCache);
		}
	}

	@Data
	@Accessors(chain = false)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody implements Endless {

		@Getter private Integer limit;
		@Getter @Setter private int offset;
		//private String lang;
		//private boolean mature;
		private String q = Api.Q;
		private Order order;
		@Getter private boolean refresh;

		private long reviewId;
		private String store_user;
		private String store_pass_sha1;

		public Body(int limit, long reviewId, boolean refresh, Order order) {

			this.limit = limit;
			this.reviewId = reviewId;
			this.refresh = refresh;
			this.order = order;
		}
		public Body(int limit, long reviewId, boolean refresh, Order order, String username, String password) {

			this.limit = limit;
			this.reviewId = reviewId;
			this.refresh = refresh;
			this.order = order;
			this.store_user = username;
			this.store_pass_sha1 = password;
		}
	}
}
