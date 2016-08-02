/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v7.ListFullReviews;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
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

	protected ListFullReviewsRequest(Body body, String baseHost) {
		super(body, OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), baseHost);
	}

	public static ListFullReviewsRequest of(long storeId, int limit, int offset) {
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(idsRepository.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool", Api.LANG,
				Api
				.isMature(), Api.Q, storeId, offset, limit);
		return new ListFullReviewsRequest(body, BASE_HOST);
	}

	public static ListFullReviewsRequest of(String storeName, String packageName) {
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
	public static ListFullReviewsRequest of(String storeName, String packageName, int maxReviews, int maxComments) {
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(idsRepository.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool", Api.LANG,
				Api
				.isMature(), Api.Q, storeName, packageName, maxReviews, maxComments);
		return new ListFullReviewsRequest(body, BASE_HOST);
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
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(idsRepository.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool", Api.LANG,
				Api
				.isMature(), Api.Q, storeName, packageName, maxReviews, 0);
		return new ListFullReviewsRequest(body, BASE_HOST);
	}

	@Override
	protected Observable<ListFullReviews> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.listFullReviews(body, bypassCache);
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

		private Order order;
		private Sort sort;

		private Long storeId;
		private Long reviewId;
		private String packageName;
		private String storeName;
		private Integer subLimit;

		public Body(String aptoideId, String accessToken, int aptoideVersionCode, String cdn, String lang, boolean mature, String q, long storeId, int limit,
		            int subLimit) {
			super(aptoideId, accessToken, aptoideVersionCode, cdn, lang, mature, q);

			this.storeId = storeId;
			this.limit = limit;
			this.subLimit = subLimit;
		}

		public Body(String aptoideId, String accessToken, int aptoideVersionCode, String cdn, String lang, boolean mature, String q, String storeName, String
				packageName, int limit, int subLimit) {
			super(aptoideId, accessToken, aptoideVersionCode, cdn, lang, mature, q);

			this.packageName = packageName;
			this.storeName = storeName;
			this.limit = limit;
			this.subLimit = subLimit;
		}

		public enum Sort {
			latest, points
		}
	}
}
