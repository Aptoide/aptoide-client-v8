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
import cm.aptoide.pt.model.v7.ListFullReviews;
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
 * Created by neuro on 04-07-2016.
 */

/**
 * http://ws2.aptoide.com/api/7/listFullReviews/info/1
 * <p>
 * http://ws2.aptoide.com/api/7/listReviews/info/1
 */
public class ListFullReviewsRequest extends V7<ListFullReviews,ListFullReviewsRequest.Body> {

	private static final String BASE_HOST = "http://ws2.aptoide.com/api/7/";

	private static final int MAX_REVIEWS = 10;
	private static final int MAX_COMMENTS = 10;
	private String url;

	protected ListFullReviewsRequest(Body body, String baseHost) {
		super(body, OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), baseHost);
	}

	public ListFullReviewsRequest(String url, Body body, String baseHost) {
		super(body, OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), baseHost);
		this.url = url;
	}

	public static ListFullReviewsRequest of(long storeId, int limit, int offset) {
		final StoreCredentialsApp storeOnRequest = getStoreOnRequest(storeId);
		String username = storeOnRequest.getUsername();
		String password = storeOnRequest.getPasswordSha1();

		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(storeId, limit, offset, ManagerPreferences.getAndResetForceServerRefresh(), username, password);
		return new ListFullReviewsRequest((Body) decorator.decorate(body), BASE_HOST);
	}

	public static ListFullReviewsRequest ofAction(String url, boolean refresh) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),
				SecurePreferencesImplementation
				.getInstance());
		return new ListFullReviewsRequest(url.replace("listFullReviews", ""), (Body) decorator.decorate(new Body(refresh)), BASE_HOST);
	}


	public static ListFullReviewsRequest of(String storeName, String packageName) {
		return of(storeName, packageName, MAX_REVIEWS, MAX_COMMENTS);
	}

	/**
	 * example call: http://ws75.aptoide.com/api/7/listFullReviews/store_name/apps/package_name/com.supercell.clashofclans/limit/10
	 *
	 * @param storeName
	 * @param packageName
	 * @param maxReviews
	 * @param maxComments
	 *
	 * @return
	 */
	public static ListFullReviewsRequest of(String storeName, String packageName, int maxReviews, int maxComments) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());

		Body body = new Body(storeName, packageName, maxReviews, maxComments, ManagerPreferences.getAndResetForceServerRefresh());
		return new ListFullReviewsRequest((Body) decorator.decorate(body), BASE_HOST);
	}

	/**
	 * example call: http://ws75.aptoide.com/api/7/listReviews/store_name/apps/package_name/com.supercell.clashofclans/sub_limit/0/limit/3
	 *
	 * @param storeName
	 * @param packageName
	 * @param maxReviews
	 *
	 * @return
	 */
	public static ListFullReviewsRequest ofTopReviews(String storeName, String packageName, int maxReviews) {

		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());

		Body body = new Body(storeName, packageName, maxReviews, 0, ManagerPreferences.getAndResetForceServerRefresh());
		return new ListFullReviewsRequest((Body) decorator.decorate(body), BASE_HOST);
	}

	@Override
	protected Observable<ListFullReviews> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		if (TextUtils.isEmpty(url)) {
			return interfaces.listFullReviews(body, bypassCache);
		} else {
			return interfaces.listFullReviews(url, body, bypassCache);
		}
	}

	@Data
	@Accessors(chain = false)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody implements Endless {

		@Getter private Integer limit;
		@Getter @Setter private int offset;
		private String lang;
		private boolean mature;
		private String q = Api.Q;
		@Getter private boolean refresh;

		private Order order;
		private Sort sort;

		private Long storeId;
		private Long reviewId;
		private String packageName;
		private String storeName;
		private Integer subLimit;

		private String store_user;
		private String store_pass_sha1;

		public Body(boolean refresh) {
			this.refresh = refresh;
		}

		public Body(long storeId, int limit, int offset, boolean refresh, String username, String password) {

			this.storeId = storeId;
			this.limit = limit;
			this.offset = offset;
			this.refresh = refresh;
			this.store_user = username;
			this.store_pass_sha1 = password;
		}

		public Body(String storeName, String packageName, int limit, int subLimit, boolean refresh) {

			this.packageName = packageName;
			this.storeName = storeName;
			this.limit = limit;
			this.subLimit = subLimit;
			this.refresh = refresh;
		}

		public enum Sort {
			latest, points
		}
	}
}
