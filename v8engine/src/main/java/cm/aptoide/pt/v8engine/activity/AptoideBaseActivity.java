/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.crashreports.CrashlyticsCrashLogger;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigation.NavigationManagerV4;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.LanguageUtils;
import cm.aptoide.pt.utils.SimpleSubscriber;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.interfaces.UiComponentBasics;
import rx.functions.Action0;

/**
 * Created by neuro on 01-05-2016.
 */
public abstract class AptoideBaseActivity extends AppCompatActivity
    implements UiComponentBasics, PermissionRequest {

  private static final String TAG = AptoideBaseActivity.class.getName();
  private static final int ACCESS_TO_EXTERNAL_FS_REQUEST_ID = 61;
  private static final int ACCESS_TO_ACCOUNTS_REQUEST_ID = 62;
  private boolean _resumed = false;
  @Nullable private Action0 toRunWhenAccessToFileSystemIsGranted;
  @Nullable private Action0 toRunWhenAccessToFileSystemIsDenied;
  @Nullable private Action0 toRunWhenAccessToAccountsIsGranted;
  @Nullable private Action0 toRunWhenAccessToAccountsIsDenied;
  @Nullable private Action0 toRunWhenDownloadAccessIsGranted;
  @Nullable private Action0 toRunWhenDownloadAccessIsDenied;

  private NavigationManagerV4 navManager;

  @Override protected void onCreate(Bundle savedInstanceState) {

    navManager = NavigationManagerV4.Builder.buildWith(this);

    super.onCreate(savedInstanceState);

    //If the user had previously chosen a language, the app is displayed in that language, if not, it starts with the device's language as default
    String lang = ManagerPreferences.getLanguage();
    Resources res = getResources();
    DisplayMetrics dm = res.getDisplayMetrics();
    Configuration conf = res.getConfiguration();
    conf.locale = LanguageUtils.getLocaleFromString(lang);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      conf.setLayoutDirection(LanguageUtils.getLocaleFromString(lang));
    }
    res.updateConfiguration(conf, dm);

    // https://fabric.io/downloads/gradle/ndk
    // Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
      ((CrashlyticsCrashLogger) CrashReport.getInstance()
          .getLogger(CrashlyticsCrashLogger.class)).setLanguage(
          getResources().getConfiguration().locale.getLanguage());
    } else {
      ((CrashlyticsCrashLogger) CrashReport.getInstance()
          .getLogger(CrashlyticsCrashLogger.class)).setLanguage(getResources().getConfiguration()
          .getLocales()
          .get(0)
          .getLanguage());
    }

    setUpAnalytics();

    if (getIntent().getExtras() != null) {
      loadExtras(getIntent().getExtras());
    }
    setContentView(getContentViewId());
    bindViews(getWindow().getDecorView()
        .getRootView());
    setupToolbar();
    setupViews();
  }

  private void setUpAnalytics() {
    Analytics.Dimensions.setGmsPresent(
        DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(this));
  }

  /**
   * @return the LayoutRes to be set on {@link #setContentView(int)}.
   */
  @LayoutRes public abstract int getContentViewId();

  @Override protected void onStart() {
    super.onStart();
    Analytics.Lifecycle.Activity.onStart(this);
  }

  @Override protected void onStop() {
    super.onStop();
    Analytics.Lifecycle.Activity.onStop(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  @Override protected void onPause() {
    super.onPause();
    _resumed = false;
    Analytics.Lifecycle.Activity.onPause(this);
  }

  @Override protected void onResume() {
    super.onResume();
    _resumed = true;
    Analytics.Lifecycle.Activity.onResume(this);
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {

    // got this error on fabric => added this check
    if (grantResults.length == 0) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    } else {
      switch (requestCode) {

        case ACCESS_TO_EXTERNAL_FS_REQUEST_ID:
          if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission Granted
            Logger.i(TAG, "access to read and write to external storage was granted");
            if (toRunWhenAccessToFileSystemIsGranted != null) {
              toRunWhenAccessToFileSystemIsGranted.call();
            }
          } else {
            if (toRunWhenAccessToFileSystemIsDenied != null) {
              toRunWhenAccessToFileSystemIsDenied.call();
            }
            ShowMessage.asSnack(findViewById(android.R.id.content),
                "access to read and write to external " + "storage" + " was denied");
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
            if (toRunWhenAccessToAccountsIsDenied != null) {
              toRunWhenAccessToAccountsIsDenied.call();
            }
            ShowMessage.asSnack(findViewById(android.R.id.content),
                "access to get accounts was denied");
          }
          break;

        default:
          super.onRequestPermissionsResult(requestCode, permissions, grantResults);
          break;
      }
    }
  }

  public boolean is_resumed() {
    return _resumed;
  }

  //
  // code to support android M permission system
  //
  // android 6 permission system
  // consider using https://github.com/hotchemi/PermissionsDispatcher
  //

  public NavigationManagerV4 getNavigationManager() {
    return navManager;
  }

  /**
   * @return o nome so monitor associado a esta activity, para efeitos de Analytics.
   */
  protected abstract String getAnalyticsScreenName();

  @TargetApi(Build.VERSION_CODES.M)
  public void requestAccessToExternalFileSystem(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDennied) {
    requestAccessToExternalFileSystem(true, toRunWhenAccessIsGranted, toRunWhenAccessIsDennied);
  }

  @TargetApi(Build.VERSION_CODES.M)
  public void requestAccessToExternalFileSystem(boolean forceShowRationale,
      @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDennied) {
    int hasPermission =
        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    if (hasPermission != PackageManager.PERMISSION_GRANTED) {
      this.toRunWhenAccessToFileSystemIsGranted = toRunWhenAccessIsGranted;
      this.toRunWhenAccessToFileSystemIsDenied = toRunWhenAccessIsDennied;

      if (forceShowRationale || ActivityCompat.shouldShowRequestPermissionRationale(this,
          Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        Logger.i(TAG, "showing rationale and requesting permission to access external storage");

        // TODO: 19/07/16 sithengineer improve this rationale messages
        showMessageOKCancel(getString(R.string.storage_access_permission_request_message),
            new SimpleSubscriber<GenericDialogs.EResponse>() {

              @Override public void onNext(GenericDialogs.EResponse eResponse) {
                super.onNext(eResponse);
                if (eResponse != GenericDialogs.EResponse.YES) {
                  if (toRunWhenAccessToFileSystemIsDenied != null) {
                    toRunWhenAccessToFileSystemIsDenied.call();
                  }
                  return;
                }

                ActivityCompat.requestPermissions(AptoideBaseActivity.this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }, ACCESS_TO_EXTERNAL_FS_REQUEST_ID);
              }
            });
        return;
      }

      ActivityCompat.requestPermissions(this, new String[] {
          Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
      }, ACCESS_TO_EXTERNAL_FS_REQUEST_ID);
      Logger.i(TAG, "requesting permission to access external storage");
      return;
    }
    Logger.i(TAG, "already has permission to access external storage");
    if (toRunWhenAccessIsGranted != null) {
      toRunWhenAccessIsGranted.call();
    }
  }

  @TargetApi(Build.VERSION_CODES.M)
  public void requestAccessToAccounts(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    requestAccessToAccounts(true, toRunWhenAccessIsGranted, toRunWhenAccessIsDenied);
  }

  @TargetApi(Build.VERSION_CODES.M) public void requestAccessToAccounts(boolean forceShowRationale,
      @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied) {
    int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
    if (hasPermission != PackageManager.PERMISSION_GRANTED) {
      this.toRunWhenAccessToAccountsIsGranted = toRunWhenAccessIsGranted;
      this.toRunWhenAccessToAccountsIsDenied = toRunWhenAccessIsDenied;

      if (forceShowRationale || ActivityCompat.shouldShowRequestPermissionRationale(this,
          Manifest.permission.GET_ACCOUNTS)) {
        Logger.i(TAG, "showing rationale and requesting permission to access accounts");

        // TODO: 19/07/16 sithengineer improve this rationale messages
        showMessageOKCancel(getString(R.string.access_to_get_accounts_rationale),
            new SimpleSubscriber<GenericDialogs.EResponse>() {

              @Override public void onNext(GenericDialogs.EResponse eResponse) {
                super.onNext(eResponse);
                if (eResponse != GenericDialogs.EResponse.YES) {
                  if (toRunWhenAccessToAccountsIsDenied != null) {
                    toRunWhenAccessToAccountsIsDenied.call();
                  }
                  return;
                }

                ActivityCompat.requestPermissions(AptoideBaseActivity.this, new String[] {
                    Manifest.permission.GET_ACCOUNTS
                }, ACCESS_TO_ACCOUNTS_REQUEST_ID);
              }
            });
        return;
      }

      ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.GET_ACCOUNTS },
          ACCESS_TO_ACCOUNTS_REQUEST_ID);
      Logger.i(TAG, "requesting permission to access accounts");
      return;
    }
    Logger.i(TAG, "already has permission to access accounts");
    if (toRunWhenAccessIsGranted != null) {
      toRunWhenAccessIsGranted.call();
    }
  }

  public void requestDownloadAccess(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    int message = R.string.general_downloads_dialog_no_download_rule_message;

    if ((AptoideUtils.SystemU.getConnectionType()
        .equals("mobile") && !ManagerPreferences.getGeneralDownloadsMobile())
        || (AptoideUtils.SystemU.getConnectionType()
        .equals("wifi") && !ManagerPreferences.getGeneralDownloadsWifi())) {
      this.toRunWhenDownloadAccessIsGranted = toRunWhenAccessIsGranted;
      this.toRunWhenDownloadAccessIsDenied = toRunWhenAccessIsDenied;
      if ((AptoideUtils.SystemU.getConnectionType()
          .equals("wifi") || AptoideUtils.SystemU.getConnectionType()
          .equals("mobile"))
          && !ManagerPreferences.getGeneralDownloadsWifi()
          && !ManagerPreferences.getGeneralDownloadsMobile()) {
        message = R.string.general_downloads_dialog_no_download_rule_message;
      } else if (AptoideUtils.SystemU.getConnectionType()
          .equals("wifi") && !ManagerPreferences.getGeneralDownloadsWifi()) {
        message = R.string.general_downloads_dialog_only_mobile_message;
      } else if (AptoideUtils.SystemU.getConnectionType()
          .equals("mobile") && !ManagerPreferences.getGeneralDownloadsMobile()) {
        message = R.string.general_downloads_dialog_only_wifi_message;
      }

      showMessageOKCancel(getString(message), new SimpleSubscriber<GenericDialogs.EResponse>() {

        @Override public void onNext(GenericDialogs.EResponse eResponse) {
          super.onNext(eResponse);
          if (eResponse == GenericDialogs.EResponse.YES) {
            if (AptoideBaseActivity.this instanceof FragmentShower) {
              ((FragmentShower) AptoideBaseActivity.this).pushFragmentV4(
                  V8Engine.getFragmentProvider()
                      .newSettingsFragment());
            } else {
              Logger.e(AptoideBaseActivity.class.getSimpleName(), new IllegalArgumentException(
                  "The Fragment should be an instance of the " + "Activity Context"));
            }
          } else {
            if (toRunWhenAccessIsDenied != null) {
              toRunWhenAccessIsDenied.call();
            }
          }
        }
      });
      return;
    }
    if (toRunWhenAccessIsGranted != null) {
      toRunWhenAccessIsGranted.call();
    }
  }

  private void showMessageOKCancel(String message,
      SimpleSubscriber<GenericDialogs.EResponse> subscriber) {
    GenericDialogs.createGenericOkCancelMessage(this, "", message)
        .subscribe(subscriber);
  }
}
