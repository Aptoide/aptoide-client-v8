/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.utils.SystemUtils;
import lombok.Getter;

/**
 * Created by neuro on 20-04-2016.
 */
@Getter
public class BaseBody {

	private String access_token = AptoideAccountManager.getAccessToken();
	private int aptoide_vercode = SystemUtils.getVerCode();
	private String cdn = "pool";
}
