/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.ListReviews;
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
public class ListReviewsRequest extends V7<ListReviews,ListReviewsRequest.Body> {

	private static final String BASE_HOST = "http://ws2.aptoide.com/api/7/";

	private static final int MAX_REVIEWS = 10;
	private static final int MAX_COMMENTS = 10;

	protected ListReviewsRequest(Body body, String baseHost) {
		super(body, OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), baseHost);
	}

	public static ListReviewsRequest of(long storeId, int limit, int offset) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());
		//IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(storeId, offset, limit, ManagerPreferences.getAndResetForceServerRefresh());
		return new ListReviewsRequest((Body) decorator.decorate(body), BASE_HOST);
	}

	public static ListReviewsRequest of(String storeName, String packageName) {
		return of(storeName, packageName, MAX_REVIEWS, MAX_COMMENTS);
	}

	/**
	 * example call: http://ws75.aptoide.com/api/7/listReviews/store_name/apps/package_name/com.supercell.clashofclans/limit/10
	 *
	 * @param storeName
	 * @param packageName
	 * @param maxReviews
	 * @param maxComments
	 *
	 * @return
	 */
	public static ListReviewsRequest of(String storeName, String packageName, int maxReviews, int maxComments) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());
		//IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(storeName, packageName, maxReviews, maxComments, ManagerPreferences.getAndResetForceServerRefresh());
		return new ListReviewsRequest((Body) decorator.decorate(body), BASE_HOST);
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
	public static ListReviewsRequest ofTopReviews(String storeName, String packageName, int maxReviews) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());
		//IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(storeName, packageName, maxReviews, 0, ManagerPreferences.getAndResetForceServerRefresh());
		return new ListReviewsRequest((Body) decorator.decorate(body), BASE_HOST);
	}

	@Override
	protected Observable<ListReviews> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.listReviews(body, bypassCache);
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

		public Body(long storeId, int limit, int subLimit, boolean refresh) {

			this.storeId = storeId;
			this.limit = limit;
			this.subLimit = subLimit;
			this.refresh = refresh;
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
