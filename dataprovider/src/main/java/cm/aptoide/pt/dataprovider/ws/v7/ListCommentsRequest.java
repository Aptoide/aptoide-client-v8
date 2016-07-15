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

	protected ListCommentsRequest(Body body, String baseHost) {
		super(
				body,
				OkHttpClientFactory.getSingletonClient(),
				WebService.getDefaultConverter(),
				baseHost
		);
	}

	public static ListCommentsRequest of(long appId) {
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(
				idsRepository.getAptoideClientUUID(),
				AptoideAccountManager.getAccessToken(),
				AptoideUtils.Core.getVerCode(),
				"pool",
				Api.LANG, Api.isMature(), Api.Q, appId
		);
		return new ListCommentsRequest(body, BASE_HOST);
	}

	public static ListCommentsRequest of(long appId, boolean onlyReviews, int max) {
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(idsRepository.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool", Api.LANG,
				Api
				.isMature(), Api.Q, appId, onlyReviews, max);
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

		private Integer apkId;
		private String apkMd5sum;
		private Long appId;
		private Long commentId;
		private String packageName;
		private Long reviewId;
		private Integer subLimit;

		public Body(String aptoideId, String accessToken, int aptoideVersionCode, String cdn, String lang, boolean mature, String q, long appId) {
			super(aptoideId, accessToken, aptoideVersionCode, cdn, lang, mature, q);
			this.appId = appId;
		}

		public Body(String aptoideId, String accessToken, int aptoideVersionCode, String cdn, String lang, boolean mature, String q, long appId, boolean
				onlyReviews, int max) {
			super(aptoideId, accessToken, aptoideVersionCode, cdn, lang, mature, q);
			this.appId = appId;
		}

		public enum Sort {
			latest, points
		}
	}
}
