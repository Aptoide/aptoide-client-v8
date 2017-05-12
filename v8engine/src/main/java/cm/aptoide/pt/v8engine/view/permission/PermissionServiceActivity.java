/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.v8engine.view.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.SimpleSubscriber;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.account.LoginBottomSheetActivity;
import com.facebook.FacebookSdk;
import rx.functions.Action0;

/**
 * Created by marcelobenites on 18/01/17.
 */

public abstract class PermissionServiceActivity extends LoginBottomSheetActivity
    implements PermissionService {

  private static final String TAG = PermissionServiceActivity.class.getName();
  private static final int ACCESS_TO_EXTERNAL_FS_REQUEST_ID = 61;
  private static final int ACCESS_TO_ACCOUNTS_REQUEST_ID = 62;

  private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

  @Nullable private Action0 toRunWhenAccessToFileSystemIsGranted;
  @Nullable private Action0 toRunWhenAccessToFileSystemIsDenied;
  @Nullable private Action0 toRunWhenAccessToAccountsIsGranted;
  @Nullable private Action0 toRunWhenAccessToAccountsIsDenied;
  @Nullable private Action0 toRunWhenDownloadAccessIsGranted;
  @Nullable private Action0 toRunWhenDownloadAccessIsDenied;
  @Nullable private Action0 toRunWhenAccessToContactsIsGranted;
  @Nullable private Action0 toRunWhenAccessToContactsIsDenied;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!FacebookSdk.isInitialized()) {
      FacebookSdk.sdkInitialize(getApplicationContext());
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToExternalFileSystem(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDennied) {
    requestAccessToExternalFileSystem(true, toRunWhenAccessIsGranted, toRunWhenAccessIsDennied);
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
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

                ActivityCompat.requestPermissions(PermissionServiceActivity.this, new String[] {
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

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToAccounts(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    requestAccessToAccounts(true, toRunWhenAccessIsGranted, toRunWhenAccessIsDenied);
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToAccounts(boolean forceShowRationale,
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

                ActivityCompat.requestPermissions(PermissionServiceActivity.this, new String[] {
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

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToContacts(boolean forceShowRationale,
      @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied) {
    int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
    if (hasPermission != PackageManager.PERMISSION_GRANTED) {
      this.toRunWhenAccessToContactsIsGranted = toRunWhenAccessIsGranted;
      this.toRunWhenAccessToContactsIsDenied = toRunWhenAccessIsDenied;

      if (forceShowRationale) {
        Logger.i(TAG, "showing rationale and requesting permission to access accounts");

        // TODO: 19/07/16 sithengineer improve this rationale messages
        showMessageOKCancel("You need to allow access to get contacts",
            new SimpleSubscriber<GenericDialogs.EResponse>() {

              @Override public void onNext(GenericDialogs.EResponse eResponse) {
                super.onNext(eResponse);
                if (eResponse != GenericDialogs.EResponse.YES) {
                  if (toRunWhenAccessToContactsIsDenied != null) {
                    toRunWhenAccessToContactsIsDenied.call();
                  }
                  return;
                }

                ActivityCompat.requestPermissions(PermissionServiceActivity.this, new String[] {
                    Manifest.permission.READ_CONTACTS
                }, PERMISSIONS_REQUEST_READ_CONTACTS);
              }
            });
        return;
      }

      ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_CONTACTS },
          PERMISSIONS_REQUEST_READ_CONTACTS);
      Logger.i(TAG, "requesting permission to access accounts");
      return;
    }
    Logger.i(TAG, "already has permission to access accounts");
    if (toRunWhenAccessIsGranted != null) {
      toRunWhenAccessIsGranted.call();
    }
  }

  @Override public void requestDownloadAccess(@Nullable Action0 toRunWhenAccessIsGranted,
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
            getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
                .newSettingsFragment());
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

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Fragment fragment = getFragmentNavigator().peekLast();
    if (fragment != null) {
      fragment.onActivityResult(requestCode, resultCode, data);
    } else {
      Logger.d("Twitter", "fragment is null");
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {

    // got this error on fabric => added this check
    if (grantResults.length == 0) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    switch (requestCode) {
      case PERMISSIONS_REQUEST_READ_CONTACTS:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // Permission Granted
          Logger.i(TAG, "access to read and write to external storage was granted");
          if (toRunWhenAccessToContactsIsGranted != null) {
            toRunWhenAccessToContactsIsGranted.call();
          }
        } else {
          if (toRunWhenAccessToContactsIsDenied != null) {
            toRunWhenAccessToContactsIsDenied.call();
          }
        }
        break;

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
