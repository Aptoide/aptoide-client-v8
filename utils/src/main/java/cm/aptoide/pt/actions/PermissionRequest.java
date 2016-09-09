/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 19/07/2016.
 */

package cm.aptoide.pt.actions;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.Nullable;

import rx.functions.Action0;

/**
 * Created by sithengineer on 16/06/16.
 */
public interface PermissionRequest {

	@TargetApi(Build.VERSION_CODES.M)
	void requestAccessToExternalFileSystem(@Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied);

	@TargetApi(Build.VERSION_CODES.M)
	void requestAccessToExternalFileSystem(boolean forceShowRationale, @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied);

	@TargetApi(Build.VERSION_CODES.M)
	void requestAccessToAccounts(@Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied);

	@TargetApi(Build.VERSION_CODES.M)
	void requestAccessToAccounts(boolean forceShowRationale, @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied);

	void requestDownloadAccess(@Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied);
}
