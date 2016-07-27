/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v7.ListReviews;
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
public class ListReviewsRequest extends V7<ListReviews,ListReviewsRequest.Body> {

	private static final String BASE_HOST = "http://ws2.aptoide.com/api/7/";

	private static final int MAX_REVIEWS = 10;
	private static final int MAX_COMMENTS = 10;

	protected ListReviewsRequest(Body body, String baseHost) {
		super(body, OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), baseHost);
	}

	public static ListReviewsRequest of(String storeName, String packageName) {
		return of(storeName, packageName, MAX_REVIEWS, MAX_COMMENTS);
	}

	public static ListReviewsRequest of(String storeName, String packageName, int maxReviews, int maxComments) {
		//
		// http://ws75.aptoide.com/api/7/listReviews/store_name/apps/package_name/com.supercell.clashofclans/limit/10
		//
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(idsRepository.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool", Api.LANG,
				Api

				.isMature(), Api.Q, storeName, packageName, maxReviews, maxComments);
		return new ListReviewsRequest(body, BASE_HOST);
	}

	public static ListReviewsRequest ofTopReviews(String storeName, String packageName, int maxReviews) {
		//
		// http://ws75.aptoide.com/api/7/listReviews/store_name/apps/package_name/com.supercell.clashofclans/sub_limit/0/limit/3
		//
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(idsRepository.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool", Api.LANG,
				Api
				.isMature(), Api.Q, storeName, packageName, maxReviews, 0);
		return new ListReviewsRequest(body, BASE_HOST);
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

		private Order order;
		private Sort sort;

		private Long reviewId;
		private String packageName;
		private String storeName;
		private Integer subLimit;

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
