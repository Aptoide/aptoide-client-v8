/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 04-07-2016.
 */
public class ListCommentsRequest extends BaseRequestWithStore<ListComments,ListCommentsRequest.Body> {

	protected ListCommentsRequest(V7Url v7Url, String baseHost) {
		super(v7Url, new Body(SecurePreferences.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool"),
				OkHttpClientFactory
				.getSingletonClient(), WebService.getDefaultConverter(), baseHost);
	}

	public static ListCommentsRequest ofAction(String url) {
		return new ListCommentsRequest(new V7Url(url).remove(V7.BASE_HOST).remove("listComments"), BASE_HOST);
	}

	@Override
	protected Observable<ListComments> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.listComments(url, body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore implements OffsetInterface<Body> {

		private String lang = Api.LANG;
		private Integer limit = getDefaultLimit();
		private boolean mature;
		private Integer offset;
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

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn) {
			super(aptoideId, accessToken, aptoideVercode, cdn);
		}

		public enum Sort {
			latest, points
		}
	}
}
