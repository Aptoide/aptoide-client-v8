/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.util.AptoideUtils;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.Getter;

/**
 * Created by neuro on 20-04-2016.
 */
@Getter
public class BaseBody {

	private String access_token = SecurePreferences.getAccessToken();
	private int aptoide_vercode = AptoideUtils.getVerCode();
	private String cdn = "pool";
}
