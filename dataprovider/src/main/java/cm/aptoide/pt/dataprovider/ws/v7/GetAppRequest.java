/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import java.util.Arrays;
import java.util.List;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v7.GetApp;
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
public class GetAppRequest extends V7<GetApp, GetAppRequest.Body> {

	private GetAppRequest(OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost, String cdn, int versionCode, String accessToken, String aptoideId) {
		super(new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, baseHost);
	}

	public static GetAppRequest of(long appId) {
		GetAppRequest getAppRequest = new GetAppRequest(OkHttpClientFactory.getSingletoneClient(),
				WebService.getDefaultConverter(), BASE_HOST, "pool", AptoideUtils.Core.getVerCode(),
				AptoideAccountManager.getAccessToken(), SecurePreferences.getAptoideClientUUID());

		getAppRequest.body.appId = appId;

		return getAppRequest;
	}

	@Override
	protected Observable<GetApp> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getApp(body, bypassCache);
	}

	public enum AppNodes {
		meta, versions;

		public static List<AppNodes> list() {
			return Arrays.asList(values());
		}
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		private Integer apkId;
		private String apkMd5sum;
		private Long appId;
		private String lang = Api.LANG;
		private Integer limit;
		private List<AppNodes> nodes;
		private Integer offset;
		private Integer packageId;
		private String packageName;
		private String q = Api.Q;
		private Integer storeId;
		private List<Long> storeIds;
		private String storeName;
		private String storePassSha1;
		private String storeUser;

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn) {
			super(aptoideId, accessToken, aptoideVercode, cdn);
		}
	}
}
