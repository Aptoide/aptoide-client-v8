/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.accountmanager.ws;

import android.content.Context;
import android.os.Build;

import java.util.HashMap;
import java.util.Locale;

import cm.aptoide.accountmanager.util.AccountManagerUtils;
import cm.aptoide.accountmanager.util.Filters;
import cm.aptoide.accountmanager.ws.responses.CheckUserCredentialsJson;
import cm.aptoide.pt.preferences.Application;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by neuro on 25-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CheckUserCredentialsRequest extends v3accountManager<CheckUserCredentialsJson> {

	private boolean registerDevice;
	private String deviceId;
	private String model;
	private String sdk;
	private String density;
	private String cpu;
	private String screenSize;
	private String openGl;
	private String token;

	public CheckUserCredentialsRequest(Context context) {
		deviceId = AccountManagerUtils.getDeviceId(context);
		sdk = String.valueOf(AccountManagerUtils.getSdkVer());
		cpu = AccountManagerUtils.getAbis();
		density = String.valueOf(AccountManagerUtils.getNumericScreenSize(context));
		openGl = AccountManagerUtils.getGlEsVer(context);
		model = Build.MODEL;
		screenSize = Filters.Screen.values()[AccountManagerUtils.getScreenSize(context)].name()
				.toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Create a checkUserInfo request
	 *
	 * @param accessToken access token to identify user
	 * @return the built request
	 */
	public static CheckUserCredentialsRequest of(String accessToken) {
		CheckUserCredentialsRequest request = new CheckUserCredentialsRequest(Application
				.getContext());
		request.setToken(accessToken);
		return request;
	}

	public static CheckUserCredentialsRequest of(String accessToken, boolean accountLinked) {
		CheckUserCredentialsRequest request = of(accessToken);
		request.setRegisterDevice(accountLinked);
		return request;
	}

	@Override
	protected Observable<CheckUserCredentialsJson> loadDataFromNetwork(Interfaces interfaces) {
		HashMap<String, String> parameters = new HashMap<>();

		parameters.put("access_token", token);
		parameters.put("mode", "json");

		if (registerDevice) {
			parameters.put("device_id", deviceId);
			parameters.put("model", model);
			parameters.put("maxSdk", sdk);
			parameters.put("myDensity", density);
			parameters.put("myCpu", cpu);
			parameters.put("maxScreen", screenSize);
			parameters.put("maxGles", openGl);
		}

		return interfaces.getUserInfo(parameters);
	}
}
