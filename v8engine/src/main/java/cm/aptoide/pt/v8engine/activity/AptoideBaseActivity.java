/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.utils.SimpleSubscriber;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.Lifecycle;
import lombok.Getter;
import rx.functions.Action0;

/**
 * Created by neuro on 01-05-2016.
 */
public abstract class AptoideBaseActivity extends AppCompatActivity implements Lifecycle {

	private static final String TAG = AptoideBaseActivity.class.getName();
	private static final int ACCESS_TO_EXTERNAL_FS_REQUEST_ID = 61;
	private static final int ACCESS_TO_ACCOUNTS_REQUEST_ID = 62;
	@Getter private boolean _resumed = false;
	private Action0 toRunWhenAccessToFileSystemIsGranted;
	private Action0 toRunWhenAccessToAccountsIsGranted;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// https://fabric.io/downloads/gradle/ndk
		// Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
		if (getIntent().getExtras() != null) {
			loadExtras(getIntent().getExtras());
		}
		setContentView(getContentViewId());
		bindViews(getWindow().getDecorView().getRootView());
		setupToolbar();
		setupViews();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * @return the LayoutRes to be set on {@link #setContentView(int)}.
	 */
	@LayoutRes
	public abstract int getContentViewId();

	//
	// code to support android M permission system
	//

	@Override
	protected void onPause() {
		super.onPause();
		_resumed = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		_resumed = true;
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@TargetApi(Build.VERSION_CODES.M)
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
			grantResults) {

		switch (requestCode) {

			case ACCESS_TO_EXTERNAL_FS_REQUEST_ID:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Permission Granted
					Logger.i(TAG, "access to read and write to external storage was granted");
					if (toRunWhenAccessToFileSystemIsGranted != null) {
						toRunWhenAccessToFileSystemIsGranted.call();
					}
				} else {
					// FIXME what should I do here?
					ShowMessage.show(findViewById(android.R.id.content), "access to read and write to external storage" +
							" was denied");
				}
				break;

			case ACCESS_TO_ACCOUNTS_REQUEST_ID:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Permission Granted
					Logger.i(TAG, "access to get accounts was granted");
					if (toRunWhenAccessToAccountsIsGranted != null) {
						toRunWhenAccessToAccountsIsGranted.call();
					}
				} else {
					// FIXME what should I do here?
					ShowMessage.show(findViewById(android.R.id.content), "access to get accounts was denied");
				}
				break;

			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
				break;
		}
	}

	/**
	 * @return o nome so monitor associado a esta activity, para efeitos de Analytics.
	 */
	protected abstract String getAnalyticsScreenName();

	//
	// android 6 permission system
	// consider using https://github.com/hotchemi/PermissionsDispatcher
	//

	@TargetApi(Build.VERSION_CODES.M)
	public void requestAccessToExternalFileSystem(Action0 toRunWhenAccessIsGranted) {
		int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(hasPermission != PackageManager.PERMISSION_GRANTED) {
			this.toRunWhenAccessToFileSystemIsGranted = toRunWhenAccessIsGranted;

			if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				Logger.i(TAG, "showing rationale and requesting permission to access external storage");
				// FIXME improve this rationale messages
				showMessageOKCancel(getString(R.string.access_to_external_storage_rationale), new SimpleSubscriber<GenericDialogs.EResponse>() {

					@Override
					public void onNext(GenericDialogs.EResponse eResponse) {
						super.onNext(eResponse);
						if(eResponse!= GenericDialogs.EResponse.YES) return;

						ActivityCompat.requestPermissions(
								AptoideBaseActivity.this,
								new String[]{
										Manifest.permission.WRITE_EXTERNAL_STORAGE,
										Manifest.permission.READ_EXTERNAL_STORAGE
								},
								ACCESS_TO_EXTERNAL_FS_REQUEST_ID
						);
					}
				});
				return;
			}

			ActivityCompat.requestPermissions(
					this,
					new String[]{
							Manifest.permission.WRITE_EXTERNAL_STORAGE,
							Manifest.permission.READ_EXTERNAL_STORAGE
					},
					ACCESS_TO_EXTERNAL_FS_REQUEST_ID
			);
			Logger.i(TAG, "requesting permission to access external storage");
			return;
		}
		Logger.i(TAG, "already has permission to access external storage");
		toRunWhenAccessIsGranted.call();
	}

	@TargetApi(Build.VERSION_CODES.M)
	public void requestAccessToAccounts(Action0 toRunWhenAccessIsGranted) {
		int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
		if (hasPermission != PackageManager.PERMISSION_GRANTED) {
			this.toRunWhenAccessToAccountsIsGranted = toRunWhenAccessIsGranted;

			if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)) {
				Logger.i(TAG, "showing rationale and requesting permission to access accounts");
				// FIXME improve this rationale messages
				showMessageOKCancel(getString(R.string.access_to_get_accounts_rationale), new SimpleSubscriber<GenericDialogs.EResponse>() {

					@Override
					public void onNext(GenericDialogs.EResponse eResponse) {
						super.onNext(eResponse);
						if(eResponse!= GenericDialogs.EResponse.YES) return;


						ActivityCompat.requestPermissions(
								AptoideBaseActivity.this,
								new String[]{
										Manifest.permission.GET_ACCOUNTS
								},
								ACCESS_TO_ACCOUNTS_REQUEST_ID
						);
					}
				});
				return;
			}

			ActivityCompat.requestPermissions(
					this, new String[]{Manifest.permission.GET_ACCOUNTS}, ACCESS_TO_ACCOUNTS_REQUEST_ID);
			Logger.i(TAG, "requesting permission to access accounts");
			return;
		}
		Logger.i(TAG, "already has permission to access accounts");
		toRunWhenAccessIsGranted.call();
	}

	private void showMessageOKCancel(
			String message,
			SimpleSubscriber<GenericDialogs.EResponse> subscriber
	) {
		GenericDialogs.createGenericOkCancelMessage(this, "", message).subscribe(subscriber);
	}
}
