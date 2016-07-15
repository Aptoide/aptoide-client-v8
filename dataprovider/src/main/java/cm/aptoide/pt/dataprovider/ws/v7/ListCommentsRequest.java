/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 15/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v7.ListComments;
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
public class ListCommentsRequest extends V7<ListComments,ListCommentsRequest.Body> {

	private static final int MAX_REVIEWS = 10;
	private static final int MAX_COMMENTS = 10;

	protected ListCommentsRequest(Body body, String baseHost) {
		super(
				body,
				OkHttpClientFactory.getSingletonClient(),
				WebService.getDefaultConverter(),
				baseHost
		);
	}

	public static ListCommentsRequest of(String storeName, String packageName) {
		//
		// http://ws75.aptoide.com/api/7/listReviews/store_name/apps/package_name/com.supercell.clashofclans/limit/10
		//
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(
				idsRepository.getAptoideClientUUID(),
				AptoideAccountManager.getAccessToken(),
				AptoideUtils.Core.getVerCode(),
				"pool", Api.LANG, Api.isMature(), Api.Q, storeName, packageName, MAX_REVIEWS, MAX_COMMENTS
		);
		return new ListCommentsRequest(body, BASE_HOST);
	}

	public static ListCommentsRequest ofTopReviews(String storeName, String packageName, int maxReviews) {
		//
		// http://ws75.aptoide.com/api/7/listReviews/store_name/apps/package_name/com.supercell.clashofclans/sub_limit/0/limit/3
		//
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(idsRepository.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool", Api.LANG,
				Api
				.isMature(), Api.Q, storeName, packageName, maxReviews, 0);
		return new ListCommentsRequest(body, BASE_HOST);
	}

	@Override
	protected Observable<ListComments> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.listComments(body, bypassCache);
	}

	@Data
	@Accessors(chain = false)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody implements Endless {

		private String lang;
		@Getter private Integer limit;
		@Getter @Setter private int offset;
		private boolean mature;
		private Order order;
		private String q = Api.Q;
		private Sort sort;

		//		private Integer apkId;
		//		private String apkMd5sum;
		//		private Long appId;
		//		private Long commentId;
		//		private Long reviewId;
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
