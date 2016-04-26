/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.accountmanager.ws;

import java.util.HashMap;

import cm.aptoide.accountmanager.ws.responses.OAuth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by neuro on 25-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OAuth2AuthenticationRequest extends v3accountManager<OAuth> {

	private String username;
	private String password;
	private Mode mode;
	private String nameForGoogle;

	@Override
	protected Observable<OAuth> loadDataFromNetwork(Interfaces interfaces) {
		HashMap<String, String> parameters = new HashMap<>();

		parameters.put("grant_type", "password");
		parameters.put("client_id", "Aptoide");
		parameters.put("mode", "json");

		switch (mode) {
			case APTOIDE:
				parameters.put("username", username);
				parameters.put("password", password);
				break;
			case GOOGLE:
				parameters.put("authMode", "google");
				parameters.put("oauthUserName", nameForGoogle);
				parameters.put("oauthToken", password);
				break;
			case FACEBOOK:
				parameters.put("authMode", "facebook");
				parameters.put("oauthToken", password);
				break;
		}

//		// TODO: 25-04-2016 neuro oemId :)
//		if(Aptoide.getConfiguration().getExtraId().length()>0){
//			parameters.put("oem_id", Aptoide.getConfiguration().getExtraId());
//		}

		return interfaces.oauth2Authentication(parameters);
	}
}
