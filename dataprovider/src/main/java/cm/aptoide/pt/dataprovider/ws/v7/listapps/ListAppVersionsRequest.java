/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import java.util.List;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.OffsetInterface;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListAppVersionsRequest extends V7<ListAppVersions, ListAppVersionsRequest.Body> {

	private ListAppVersionsRequest(boolean bypassCache, OkHttpClient httpClient, Converter.Factory converterFactory, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(bypassCache, new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, BASE_HOST);
	}

	public static ListAppVersionsRequest of(boolean bypassCache) {
		return new ListAppVersionsRequest(bypassCache, OkHttpClientFactory.getSingletoneClient(), WebService
				.getDefaultConverter(), SecurePreferences.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");
	}

	@Override
	protected Observable<ListAppVersions> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.listAppVersions(body, bypassCache);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody implements OffsetInterface<Body> {

		private Integer apkId;
		private String apkMd5sum;
		private Integer appId;
		private String lang = Api.LANG;
		private Integer limit;
		private int offset;
		private Integer packageId;
		private String packageName;
		private String q = Api.Q;
		private List<Long> storeIds;
		private List<String> storeNames;

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn) {
			super(aptoideId, accessToken, aptoideVercode, cdn);
		}
	}
}
