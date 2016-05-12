/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 12/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import android.content.pm.PackageInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import cm.aptoide.pt.dataprovider.util.AptoideUtils;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.utils.AlgorithmUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListAppsUpdatesRequest extends V7<ListAppsUpdates> {

	private static final int SPLIT_SIZE = 100;
	private final Body body = new Body();

	private ListAppsUpdatesRequest(boolean bypassCache) {
		super(bypassCache);
	}

	public static ListAppsUpdatesRequest of(boolean bypassCache) {
		return new ListAppsUpdatesRequest(bypassCache);
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

	private static List<ApksData> getInstalledApksData() {
		LinkedList<ApksData> apksDatas = new LinkedList<>();

		for (PackageInfo packageInfo : AptoideUtils.getInstalledApps()) {
			apksDatas.add(new ApksData(packageInfo.packageName, packageInfo.versionCode,
					AlgorithmUtils
					.computeSHA1sumFromBytes(packageInfo.signatures[0].toByteArray())
					.toUpperCase(Locale.ENGLISH)));
		}

		return apksDatas;
	}

	@Override
	protected Observable<ListAppsUpdates> loadDataFromNetwork(Interfaces interfaces) {
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
	@NoArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		private List<ApksData> apksData = getInstalledApksData();
		private String lang = Api.LANG;
		private String q = Api.Q;
		private List<Long> storeIds = getSubscribedStoresIds();
		private List<String> storeNames;
		private List<StoreAuth> storesAuth;

		public Body(Body body) {
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
