/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListAppsUpdatesRequest extends V7<ListAppsUpdates> {

	private final Body body = new Body();

	private ListAppsUpdatesRequest() {
	}

	public static ListAppsUpdatesRequest of() {
		return new ListAppsUpdatesRequest();
	}

	@Override
	protected Observable<ListAppsUpdates> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.listAppsUpdates(body);
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		private List<ApksData> apksData;
		private String lang = Api.LANG;
		private String q = Api.Q;
		private List<Integer> storeIds;
		private List<String> storeNames;
		private List<StoreAuth> storesAuth;
	}

	@Data
	public static class StoreAuth {

		private String storeName;
		private String storeUser;
		private String storePassSha1;
	}

	@Data
	@AllArgsConstructor
	public static class ApksData {

		@JsonProperty("package") private String packageName;

		private int vercode;
		private String signature;
	}
}
