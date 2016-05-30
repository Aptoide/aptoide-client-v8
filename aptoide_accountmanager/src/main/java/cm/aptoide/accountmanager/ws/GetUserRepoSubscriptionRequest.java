/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.accountmanager.ws;

import java.util.HashMap;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.GetUserRepoSubscription;
import rx.Observable;

/**
 * Created by rmateus on 16-02-2015.
 */
public class GetUserRepoSubscriptionRequest extends v3accountManager<GetUserRepoSubscription> {

	protected GetUserRepoSubscriptionRequest() {
	}

	public static GetUserRepoSubscriptionRequest of() {
		return new GetUserRepoSubscriptionRequest();
	}

	@Override
	protected Observable<GetUserRepoSubscription> loadDataFromNetwork(Interfaces interfaces) {
		HashMap<String, String> parameters = new HashMap<>();

		parameters.put("mode", "json");
		parameters.put("access_token", AptoideAccountManager.getAccessToken());

		return interfaces.getUserRepos(parameters);
	}
}
