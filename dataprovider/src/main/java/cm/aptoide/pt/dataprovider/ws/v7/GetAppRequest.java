/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import java.util.Arrays;
import java.util.List;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v7.GetApp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetAppRequest extends V7<GetApp> {

	private final Body body = new Body();

	public static GetAppRequest of(int appId) {
		GetAppRequest getAppRequest = new GetAppRequest();

		getAppRequest.body.appId = appId;

		return getAppRequest;
	}

	@Override
	protected Observable<GetApp> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.getApp(body);
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
		private Integer appId;
		private String lang = Api.LANG;
		private Integer limit;
		private List<AppNodes> nodes;
		private Integer offset;
		private Integer packageId;
		private String packageName;
		private String q = Api.Q;
		private Integer storeId;
		private List<Integer> storeIds;
		private String storeName;
		//		Doesn't make sense without stores_auth_map
//		private List<String> storeNames;
		private String storePassSha1;
		private String storeUser;
		//  stores_auth_map implementation required
	}
}
