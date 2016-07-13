/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
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
public class ListCommentsRequest extends BaseRequestWithStore<ListComments,ListCommentsRequest.Body> {

	private String url;

	protected ListCommentsRequest(String url, Body body, String baseHost) {
		super(
				body,
				OkHttpClientFactory.getSingletonClient(),
				WebService.getDefaultConverter(),
				baseHost
		);

		this.url = url;
	}

	public static ListCommentsRequest ofAction(String url) {
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		V7Url v7Url = new V7Url(url).remove(V7.BASE_HOST).remove("listComments");
		Body body = new Body(
				idsRepository.getAptoideClientUUID(),
				AptoideAccountManager.getAccessToken(),
				AptoideUtils.Core.getVerCode(),
				"pool",
				v7Url.getStoreName(),
				Api.LANG,
				Api.MATURE,
				Api.Q
		);
		return new ListCommentsRequest(url, body, BASE_HOST);
	}

	@Override
	protected Observable<ListComments> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.listComments(url, body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBodyWithStore implements Endless {

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

		public Body(String aptoideId, String accessToken, int aptoideVersionCode, String cdn, String storeName, String lang, boolean mature, String q) {
			super(aptoideId, accessToken, aptoideVersionCode, cdn, lang, mature, q, storeName);
		}

		public enum Sort {
			latest, points
		}
	}
}
