/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/06/2016.
 */

package cm.aptoide.pt.v8engine.interfaces;

import android.annotation.TargetApi;
import android.os.Build;

import rx.functions.Action0;

/**
 * Created by sithengineer on 16/06/16.
 */
public interface PermissionRequest {

	@TargetApi(Build.VERSION_CODES.M)
	void requestAccessToExternalFileSystem(Action0 toRunWhenAccessIsGranted);

	@TargetApi(Build.VERSION_CODES.M)
	void requestAccessToExternalFileSystem(boolean forceShowRationale, Action0 toRunWhenAccessIsGranted);

	@TargetApi(Build.VERSION_CODES.M)
	void requestAccessToAccounts(Action0 toRunWhenAccessIsGranted);

	@TargetApi(Build.VERSION_CODES.M)
	void requestAccessToAccounts(boolean forceShowRationale, Action0 toRunWhenAccessIsGranted);
}
