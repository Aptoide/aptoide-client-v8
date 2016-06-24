/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.preferences;

/**
 * Created by neuro on 10-05-2016.
 */
public interface AptoideConfiguration {

	String getAppId();

	// Cache
	String getCachePath();

	String getApkCachePath();

	String getImagesCachePath();

	// Account
	String getAccountType();

	String getAutoUpdateUrl();

	// Market
	String getMarketName();

	int getIcon();

	String getDefaultStore();

	// Providers
	String getUpdatesSyncAdapterAuthority();

	String getSearchAuthority();

	String getAutoUpdatesSyncAdapterAuthority();

	// Authorities
	String getTimelineActivitySyncAdapterAuthority();

	String getTimeLinePostsSyncAdapterAuthority();

	// Classes
	Class<?> getPushNotificationReceiverClass();

	// Partners

	/**
	 * @return partner id. null for vanilla.
	 */
	String getPartnerId();
}
