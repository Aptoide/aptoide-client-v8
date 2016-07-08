/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 08/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import java.util.List;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListAppVersionsRequest extends V7<ListAppVersions,ListAppVersionsRequest.Body> {

	private ListAppVersionsRequest(OkHttpClient httpClient, Converter.Factory converterFactory, String aptoideId, String accessToken, int versionCode, String
			cdn) {
		super(new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, BASE_HOST);
	}

	private ListAppVersionsRequest(OkHttpClient httpClient, Converter.Factory converterFactory, String aptoideId, String accessToken, int versionCode, String
			cdn, String packageName) {
		super(new Body(aptoideId, accessToken, versionCode, cdn, packageName), httpClient, converterFactory, BASE_HOST);
	}

	public static ListAppVersionsRequest of() {
		return new ListAppVersionsRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), SecurePreferences.getAptoideClientUUID()
				, AptoideAccountManager
				.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");
	}

	public static ListAppVersionsRequest of(String packageName) {
		return new ListAppVersionsRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), SecurePreferences.getAptoideClientUUID()
				, AptoideAccountManager
				.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool", packageName);
	}

	@Override
	protected Observable<ListAppVersions> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.listAppVersions(body, bypassCache);
	}

	@Data
	//@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody implements Endless {

		private Integer apkId;
		private String apkMd5sum;
		private Integer appId;
		private String lang = Api.LANG;
		@Setter
		@Getter
		private int limit;
		@Setter
		@Getter
		private int offset;
		private Integer packageId;
		private String packageName;
		private String q = Api.Q;
		private List<Long> storeIds;
		private List<String> storeNames;

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn) {
			super(aptoideId, accessToken, aptoideVercode, cdn, Api.LANG, Api.MATURE, Api.Q);
		}

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn, String packageName) {
			super(aptoideId, accessToken, aptoideVercode, cdn, Api.LANG, Api.MATURE, Api.Q);
			this.packageName = packageName;
		}
	}
}
