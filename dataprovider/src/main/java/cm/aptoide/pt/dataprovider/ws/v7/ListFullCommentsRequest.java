/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.ListFullComments;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
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
public class ListFullCommentsRequest extends V7<ListFullComments,ListFullCommentsRequest.Body> {

	private static final String BASE_HOST = "http://ws2.aptoide.com/api/7/";

	protected ListFullCommentsRequest(Body body, String baseHost) {
		super(body, OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), baseHost);
	}

	public static ListFullCommentsRequest of(long reviewId, int limit) {
		//
		//
		//
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());

		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(limit, reviewId, ManagerPreferences.getAndResetForceServerRefresh());

		return new ListFullCommentsRequest((Body) decorator.decorate(body), BASE_HOST);
	}

	@Override
	protected Observable<ListFullComments> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.listFullComments(body, bypassCache);
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
		@Getter private boolean refresh;

		private long reviewId;

		public Body(int limit, long reviewId, Boolean refresh) {

			this.limit = limit;
			this.reviewId = reviewId;
			this.refresh = refresh;
		}
	}
}
