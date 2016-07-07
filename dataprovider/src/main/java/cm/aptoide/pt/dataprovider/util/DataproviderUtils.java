/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;
import java.util.Date;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import io.realm.Realm;
import lombok.Cleanup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by neuro on 20-04-2016.
 */
public class DataproviderUtils {

	private static final String TAG = DataproviderUtils.class.getName();

	public static void checkUpdates() {
		checkUpdates(null);
	}

	public static void checkUpdates(@Nullable SuccessRequestListener<ListAppsUpdates> successRequestListener) {
		ListAppsUpdatesRequest.of().execute(listAppsUpdates -> {
			@Cleanup Realm realm = Database.get();
			for (App app : listAppsUpdates.getList()) {
				Database.save(new Update(app), realm);
			}

			if (successRequestListener != null) {
				successRequestListener.call(listAppsUpdates);
			}
		}, Throwable::printStackTrace, true);
	}

	/**
	 * Execute a simple request (knock at the door) to the given URL.
	 *
	 * @param url
	 */
	public static void knock(String url) {
		OkHttpClient client = new OkHttpClient();

		Request click = new Request.Builder().url(url).build();

		client.newCall(click).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {

			}
		});
	}

	public static class AdNetworksUtils {

		public static String parseMacros(@NonNull String clickUrl) {

			clickUrl = clickUrl.replace("[USER_ANDROID_ID]", AptoideUtils.SystemU.getAndroidId());
			clickUrl = clickUrl.replace("[USER_UDID]", SecurePreferences.getAptoideClientUUID());
			clickUrl = clickUrl.replace("[USER_AAID]", SecurePreferences.getAdvertisingId());
			clickUrl = clickUrl.replace("[TIME_STAMP]", String.valueOf(new Date().getTime()));

			return clickUrl;
		}

		public static boolean isGooglePlayServicesAvailable() {
			return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(DataProvider.getContext()) == ConnectionResult.SUCCESS;
		}
	}
}
