/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.ExcludedUpdate;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListAppsUpdatesRequest extends V7<ListAppsUpdates, ListAppsUpdatesRequest.Body> {

	private static final int SPLIT_SIZE = 100;

	private ListAppsUpdatesRequest(OkHttpClient httpClient, Converter.Factory converterFactory, String aptoideId, String accessToken, int versionCode, String cdn) {
		super(new Body(aptoideId, accessToken, versionCode, cdn), httpClient, converterFactory, BASE_HOST);
	}

	public static ListAppsUpdatesRequest of() {
		return new ListAppsUpdatesRequest(OkHttpClientFactory.getSingletoneClient(), WebService.getDefaultConverter(), SecurePreferences.getAptoideClientUUID(), AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool");
	}

	// // TODO: 12-05-2016 neuro check deprecated
	@Deprecated
	private static List<Long> getSubscribedStoresIds() {
		LinkedList<Long> storesIds = new LinkedList<>();

		for (Store store : StoreUtils.getSubscribedStores()) {
			storesIds.add(store.getId());
		}

		return storesIds;
	}

	private static List<ApksData> getInstalledApksDataWithoutExcluded() {
		LinkedList<ApksData> apksDatas = new LinkedList<>();

		@Cleanup Realm realm = Database.get();

		RealmResults<ExcludedUpdate> excludedUpdates = Database.ExcludedUpdatesQ.getAll(realm);
		RealmResults<Installed> installeds = Database.InstalledQ.getAll(realm);
		for (Installed installed : installeds) {
			if (!Database.ExcludedUpdatesQ.contains(installed.getPackageName(), realm)) {
				apksDatas.add(new ApksData(installed.getPackageName(), installed.getVersionCode(), installed
						.getSignature()));
			}
		}

		return apksDatas;
	}

	@Override
	protected Observable<ListAppsUpdates> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		ListAppsUpdates resultListAppsUpdates = new ListAppsUpdates();

		if (body.getApksData().size() > SPLIT_SIZE) {

			int latchCount = body.getApksData().size() / SPLIT_SIZE + (body.getApksData()
					.size() % SPLIT_SIZE > 0 ? 1 : 0);
			CountDownLatch countDownLatch = new CountDownLatch(latchCount);

			resultListAppsUpdates.setList(new LinkedList<>());
			List<ApksData> apksData = body.getApksData();

			for (int n = 0; n < apksData.size(); n += SPLIT_SIZE) {
				Body tmpBody = new Body(body).setApksData(apksData.subList(n, n + SPLIT_SIZE >
						apksData
						.size() ? n + apksData.size() % SPLIT_SIZE : n + SPLIT_SIZE));

				interfaces.listAppsUpdates(tmpBody, bypassCache)
						.subscribeOn(Schedulers.io())
						.subscribe(listAppsUpdates -> {
							resultListAppsUpdates.getList().addAll(listAppsUpdates.getList());
							countDownLatch.countDown();
						}, throwable -> {
							countDownLatch.countDown();
							throwable.printStackTrace();
						});
			}

			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return Observable.just(resultListAppsUpdates);
		} else {
			return interfaces.listAppsUpdates(body, bypassCache);
		}
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		private List<ApksData> apksData = getInstalledApksDataWithoutExcluded();
		private String lang = Api.LANG;
		private String q = Api.Q;
		// TODO: 27-05-2016 neuro implement
		private List<Long> storeIds = getSubscribedStoresIds();
		private List<String> storeNames;
		private List<StoreAuth> storesAuth;

		public Body(String aptoideId, String accessToken, int versionCode, String cdn) {
			super(aptoideId, accessToken, versionCode, cdn);
		}

		public Body(Body body) {
			this(body.getAptoideId(), body.getAccessToken(), body.getAptoideVercode(), body.getCdn());
			if (body.getApksData() != null) {
				this.apksData = new LinkedList<>(body.getApksData());
			}
			this.lang = body.getLang();
			this.q = body.getQ();

			if (body.getStoreIds() != null) {
				this.storeIds = new LinkedList<>(body.getStoreIds());
			}
			if (body.getStoreNames() != null) {
				this.storeNames = new LinkedList<>(body.getStoreNames());
			}
			if (body.getStoresAuth() != null) {
				this.storesAuth = new LinkedList<>(body.getStoresAuth());
			}
		}
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
