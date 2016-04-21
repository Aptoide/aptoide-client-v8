/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 21/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.preferences.secure.SecurePreferences;
import cm.aptoide.pt.dataprovider.util.AptoideUtils;
import lombok.Data;

/**
 * Created by neuro on 20-04-2016.
 */
@Data
public class BaseBody {

	private String access_token = SecurePreferences.getAccessToken();
	private int aptoide_vercode = AptoideUtils.getVerCode();
	private String cdn = "pool";
}
