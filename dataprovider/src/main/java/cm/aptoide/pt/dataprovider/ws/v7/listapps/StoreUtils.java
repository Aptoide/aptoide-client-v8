/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.utils.AptoideUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import lombok.Cleanup;

/**
 * Created by neuro on 11-05-2016.
 */
public class StoreUtils {

	public static final String PRIVATE_STORE_ERROR = "STORE-3";
	public static final String PRIVATE_STORE_WRONG_CREDENTIALS = "STORE-4";

	public static List<Long> getSubscribedStoresIds() {

		List<Long> storesNames = new LinkedList<>();
		@Cleanup Realm realm = DeprecatedDatabase.get();
		RealmResults<cm.aptoide.pt.database.realm.Store> stores = DeprecatedDatabase.StoreQ.getAll(realm);
		for (cm.aptoide.pt.database.realm.Store store : stores) {
			storesNames.add(store.getStoreId());
		}

		return storesNames;
	}

	public static List<String> getSubscribedStoresNames() {

		List<String> storesNames = new LinkedList<>();
		@Cleanup Realm realm = DeprecatedDatabase.get();
		RealmResults<cm.aptoide.pt.database.realm.Store> stores = DeprecatedDatabase.StoreQ.getAll(realm);
		for (cm.aptoide.pt.database.realm.Store store : stores) {
			storesNames.add(store.getStoreName());
		}

		return storesNames;
	}

	public static Map<String,List<String>> getSubscribedStoresAuthMap() {
		@Cleanup Realm realm = DeprecatedDatabase.get();
		Map<String,List<String>> storesAuthMap = new HashMap<>();
		RealmResults<cm.aptoide.pt.database.realm.Store> stores = DeprecatedDatabase.StoreQ.getAll(realm);
		for (cm.aptoide.pt.database.realm.Store store : stores) {
			if (store.getPasswordSha1() != null) {
				storesAuthMap.put(store.getStoreName(), new LinkedList<>(Arrays.asList(store.getUsername(), store.getPasswordSha1())));
			}
		}
		return storesAuthMap.size() > 0 ? storesAuthMap : null;
	}

	/**
	 * If you want to do event tracking (Analytics) use (v8engine)StoreUtilsProxy.subscribeStore instead, else, use this
	 *
	 * @param storeName
	 * @param successRequestListener
	 * @param errorRequestListener
	 */
	@Deprecated
	public static void subscribeStore(String storeName, @Nullable SuccessRequestListener<GetStoreMeta>
			successRequestListener, @Nullable ErrorRequestListener errorRequestListener) {
		subscribeStore(GetStoreMetaRequest.of(storeName), successRequestListener, errorRequestListener);
	}

	/**
	 * If you want to do event tracking (Analytics) use (v8engine)StoreUtilsProxy.subscribeStore instead, else, use this.
	 *
	 * @param getStoreMetaRequest
	 * @param successRequestListener
	 * @param errorRequestListener
	 */
	@Deprecated
	public static void subscribeStore(GetStoreMetaRequest getStoreMetaRequest, @Nullable
	SuccessRequestListener<GetStoreMeta> successRequestListener, @Nullable ErrorRequestListener errorRequestListener) {
		getStoreMetaRequest.execute(getStoreMeta -> {

			if (BaseV7Response.Info.Status.OK.equals(getStoreMeta.getInfo().getStatus())) {

				@Cleanup Realm realm = DeprecatedDatabase.get();

				cm.aptoide.pt.database.realm.Store store = new cm.aptoide.pt.database.realm.Store();

				Store storeData = getStoreMeta.getData();
				store.setStoreId(storeData.getId());
				store.setStoreName(storeData.getName());
				store.setDownloads(storeData.getStats().getDownloads());

				String avatar = storeData.getAvatar();

				if (avatar != null) {
					avatar = AptoideUtils.ImageSizeU.parseAvatarUrl(avatar);
				}

				store.setIconPath(avatar);
				store.setTheme(storeData.getAppearance().getTheme());

				if (isPrivateCredentialsSet(getStoreMetaRequest)) {
					store.setUsername(getStoreMetaRequest.getBody().getStoreUser());
					store.setPasswordSha1(getStoreMetaRequest.getBody().getStorePassSha1());
				}

				// TODO: 18-05-2016 neuro private ainda na ta
				if (AptoideAccountManager.isLoggedIn()) {
					AptoideAccountManager.subscribeStore(storeData.getName());
				}

				DeprecatedDatabase.save(store, realm);

				if (successRequestListener != null) {
					successRequestListener.call(getStoreMeta);
				}
			}
		}, (e) -> {
			if (errorRequestListener != null) {
				errorRequestListener.onError(e);
			}
		});
	}

	private static boolean isPrivateCredentialsSet(GetStoreMetaRequest getStoreMetaRequest) {
		return getStoreMetaRequest.getBody().getStoreUser() != null && getStoreMetaRequest.getBody()
				.getStorePassSha1() != null;
	}

	public static boolean isSubscribedStore(String storeName) {
		@Cleanup Realm realm = DeprecatedDatabase.get();
		return DeprecatedDatabase.StoreQ.get(storeName, realm) != null;
	}

	public static String split(String repoUrl) {
		Logger.d("Aptoide-RepoUtils", "Splitting " + repoUrl);
		repoUrl = formatRepoUri(repoUrl);
		return repoUrl.split("http://")[1].split("\\.store")[0].split("\\.bazaarandroid.com")[0];
	}

	public static String formatRepoUri(String repoUri) {

		repoUri = repoUri.toLowerCase(Locale.ENGLISH);

		if (repoUri.contains("http//")) {
			repoUri = repoUri.replaceFirst("http//", "http://");
		}

		if (repoUri.length() != 0 && repoUri.charAt(repoUri.length() - 1) != '/') {
			repoUri = repoUri + '/';
			Logger.d("Aptoide-ManageRepo", "repo uri: " + repoUri);
		}
		if (!repoUri.startsWith("http://")) {
			repoUri = "http://" + repoUri;
			Logger.d("Aptoide-ManageRepo", "repo uri: " + repoUri);
		}

		return repoUri;
	}
}
