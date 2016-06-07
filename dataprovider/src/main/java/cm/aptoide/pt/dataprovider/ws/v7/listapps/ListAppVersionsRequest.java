/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import java.util.List;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.OffsetInterface;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListAppVersionsRequest extends V7<ListAppVersions, ListAppVersionsRequest.Body> {

	private ListAppVersionsRequest(boolean bypassCache) {
		super(bypassCache, new Body());
	}

	public static ListAppVersionsRequest of(boolean bypassCache) {
		return new ListAppVersionsRequest(bypassCache);
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
		// Honestly don't know lol
//		private List<StoreAuth> storesAuth;
	}
}
