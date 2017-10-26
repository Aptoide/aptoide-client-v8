/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.SimpleSubscriber;
import cm.aptoide.pt.utils.design.ShowMessage;
import com.facebook.FacebookSdk;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Action0;

@Deprecated public abstract class PermissionServiceActivity extends ActivityResultNavigator
    implements PermissionService {

  private static final String TAG = PermissionServiceActivity.class.getName();
  private static final int ACCESS_TO_EXTERNAL_FS_REQUEST_ID = 61;
  private static final int ACCESS_TO_ACCOUNTS_REQUEST_ID = 62;

  private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
  private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 101;
  private static final int PERMISSIONS_REQUEST_LOCATION_AND_EXTERNAL_STORAGE = 102;
  private static final int PERMISSIONS_REQUEST_WRITE_SETTINGS = 103;
  private static final int PERMISSIONS_REQUEST_LOCATION_ENABLING = 104;

  @Nullable private Action0 toRunWhenAccessToFileSystemIsGranted;
  @Nullable private Action0 toRunWhenAccessToFileSystemIsDenied;
  @Nullable private Action0 toRunWhenAccessToAccountsIsGranted;
  @Nullable private Action0 toRunWhenAccessToAccountsIsDenied;
  @Nullable private Action0 toRunWhenAccessToContactsIsGranted;
  @Nullable private Action0 toRunWhenAccessToContactsIsDenied;

  @Nullable private Action0 toRunWhenAccessToLocationAndExternalStorageIsGranted;
  @Nullable private Action0 toRunWhenAccessToLocationAndExternalStorageIsDenied;
  @Nullable private Action0 toRunWhenAccessToWriteSettingsIsDenied;
  @Nullable private Action0 toRunWhenAccessToWriteSettingsIsGranted;
  @Nullable private Action0 toRunWhenLocationEnablingGranted;
  @Nullable private Action0 toRunWhenLocationEnablingDenied;

  private SharedPreferences sharedPreferences;
  private ConnectivityManager connectivityManager;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    sharedPreferences =
        ((AptoideApplication) getApplicationContext()).getDefaultSharedPreferences();
    if (!FacebookSdk.isInitialized()) {
      FacebookSdk.sdkInitialize(getApplicationContext());
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
        Logger.v(TAG, "showing rationale and requesting permission to access accounts");

        showMessageOKCancel(R.string.access_to_get_accounts_rationale,
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
      Logger.v(TAG, "requesting permission to access accounts");
      return;
    }
    Logger.v(TAG, "already has permission to access accounts");
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
        Logger.v(TAG, "showing rationale and requesting permission to access accounts");

        showMessageOKCancel(R.string.access_to_get_accounts_rationale,
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
      Logger.v(TAG, "requesting permission to access accounts");
      return;
    }
    Logger.v(TAG, "already has permission to access accounts");
    if (toRunWhenAccessIsGranted != null) {
      toRunWhenAccessIsGranted.call();
    }
  }

  @Override public void requestDownloadAccess(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    int message = R.string.general_downloads_dialog_no_download_rule_message;

    if ((AptoideUtils.SystemU.getConnectionType(connectivityManager)
        .equals("mobile") && !ManagerPreferences.getGeneralDownloadsMobile(sharedPreferences)) || (
        AptoideUtils.SystemU.getConnectionType(connectivityManager)
            .equals("wifi")
            && !ManagerPreferences.getGeneralDownloadsWifi(sharedPreferences))) {
      if ((AptoideUtils.SystemU.getConnectionType(connectivityManager)
          .equals("wifi") || AptoideUtils.SystemU.getConnectionType(connectivityManager)
          .equals("mobile"))
          && !ManagerPreferences.getGeneralDownloadsWifi(sharedPreferences)
          && !ManagerPreferences.getGeneralDownloadsMobile(sharedPreferences)) {
        message = R.string.general_downloads_dialog_no_download_rule_message;
      } else if (AptoideUtils.SystemU.getConnectionType(connectivityManager)
          .equals("wifi") && !ManagerPreferences.getGeneralDownloadsWifi(sharedPreferences)) {
        message = R.string.general_downloads_dialog_only_mobile_message;
      } else if (AptoideUtils.SystemU.getConnectionType(connectivityManager)
          .equals("mobile") && !ManagerPreferences.getGeneralDownloadsMobile(sharedPreferences)) {
        message = R.string.general_downloads_dialog_only_wifi_message;
      }

      showMessageOKCancel(message, new SimpleSubscriber<GenericDialogs.EResponse>() {

        @Override public void onNext(GenericDialogs.EResponse eResponse) {
          super.onNext(eResponse);
          if (eResponse == GenericDialogs.EResponse.YES) {
            getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
                .newSettingsFragment(), true);
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

  @Override public void requestAccessToCamera(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
    if (hasPermission != PackageManager.PERMISSION_GRANTED) {
      this.toRunWhenAccessToFileSystemIsGranted = toRunWhenAccessIsGranted;
      this.toRunWhenAccessToFileSystemIsDenied = toRunWhenAccessIsDenied;

      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
        Logger.v(TAG, "showing rationale and requesting permission to access camera");

        showMessageOKCancel(R.string.camera_access_permission_request_message,
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
                    Manifest.permission.CAMERA
                }, PERMISSIONS_REQUEST_ACCESS_CAMERA);
              }
            });
        return;
      }

      ActivityCompat.requestPermissions(PermissionServiceActivity.this, new String[] {
          Manifest.permission.CAMERA
      }, PERMISSIONS_REQUEST_ACCESS_CAMERA);
      Logger.v(TAG, "requesting permission to access camera");
      return;
    }
    Logger.v(TAG, "already has permission to access camera");
    if (toRunWhenAccessIsGranted != null) {
      toRunWhenAccessIsGranted.call();
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToExternalFileSystem(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDennied) {
    requestAccessToExternalFileSystem(true, toRunWhenAccessIsGranted, toRunWhenAccessIsDennied);
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToExternalFileSystem(boolean forceShowRationale,
      @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied) {
    requestAccessToExternalFileSystem(forceShowRationale,
        R.string.storage_access_permission_request_message, toRunWhenAccessIsGranted,
        toRunWhenAccessIsDenied);
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToExternalFileSystem(boolean forceShowRationale,
      @StringRes int rationaleMessage, @Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    int hasPermission =
        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    if (hasPermission != PackageManager.PERMISSION_GRANTED) {
      this.toRunWhenAccessToFileSystemIsGranted = toRunWhenAccessIsGranted;
      this.toRunWhenAccessToFileSystemIsDenied = toRunWhenAccessIsDenied;

      if (forceShowRationale || ActivityCompat.shouldShowRequestPermissionRationale(this,
          Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        Logger.v(TAG, "showing rationale and requesting permission to access external storage");

        // TODO: 19/07/16 improve this rationale messages
        showMessageOKCancel(rationaleMessage, new SimpleSubscriber<GenericDialogs.EResponse>() {

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
      Logger.v(TAG, "requesting permission to access external storage");
      return;
    }
    Logger.v(TAG, "already has permission to access external storage");
    if (toRunWhenAccessIsGranted != null) {
      toRunWhenAccessIsGranted.call();
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToLocationAndExternalStorage(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {

    List<String> notGrantedPermissions = new ArrayList<>();
    String[] permissions = new String[] {
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    for (String permission : permissions) {
      if (ContextCompat.checkSelfPermission(this, permission)
          != PackageManager.PERMISSION_GRANTED) {
        notGrantedPermissions.add(permission);
      }
    }
    if (notGrantedPermissions.isEmpty()) {
      if (toRunWhenAccessIsGranted != null) {
        toRunWhenAccessIsGranted.call();
      }
    } else {
      this.toRunWhenAccessToLocationAndExternalStorageIsGranted = toRunWhenAccessIsGranted;
      this.toRunWhenAccessToLocationAndExternalStorageIsDenied = toRunWhenAccessIsDenied;

      ActivityCompat.requestPermissions(this, notGrantedPermissions.toArray(new String[0]),
          PERMISSIONS_REQUEST_LOCATION_AND_EXTERNAL_STORAGE);
    }
  }

  @TargetApi(Build.VERSION_CODES.M) @Override
  public void requestAccessToWriteSettings(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    if (Settings.System.canWrite(this)) {
      if (toRunWhenAccessIsGranted != null) {
        toRunWhenAccessIsGranted.call();
      }
    } else {
      this.toRunWhenAccessToWriteSettingsIsGranted = toRunWhenAccessIsGranted;
      this.toRunWhenAccessToWriteSettingsIsDenied = toRunWhenAccessIsDenied;
      Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
      intent.setData(Uri.parse("package:" + this.getPackageName()));
      startActivityForResult(intent, PERMISSIONS_REQUEST_WRITE_SETTINGS);
    }
  }

  private void showMessageOKCancel(@StringRes int messageId,
      SimpleSubscriber<GenericDialogs.EResponse> subscriber) {
    showMessageOKCancel(getString(messageId), subscriber);
  }

  private void showMessageOKCancel(String message,
      SimpleSubscriber<GenericDialogs.EResponse> subscriber) {
    GenericDialogs.createGenericOkCancelMessage(this, "", message)
        .subscribe(subscriber);
  }

  private boolean checkLocationPermission() {
    int locationMode = 0;
    String locationProviders;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      try {
        locationMode =
            Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
      } catch (Settings.SettingNotFoundException e) {
        e.printStackTrace();
        return false;
      }

      return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    } else {
      locationProviders = Settings.Secure.getString(this.getContentResolver(),
          Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
      return !TextUtils.isEmpty(locationProviders);
    }
  }

  @Override public void requestToEnableLocation(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied) {
    if (checkLocationPermission()) {
      if (toRunWhenAccessIsGranted != null) {
        toRunWhenAccessIsGranted.call();
      }
    } else {
      this.toRunWhenLocationEnablingGranted = toRunWhenAccessIsGranted;
      this.toRunWhenLocationEnablingDenied = toRunWhenAccessIsDenied;

      Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
      startActivityForResult(locationIntent, PERMISSIONS_REQUEST_LOCATION_ENABLING);
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
      case PERMISSIONS_REQUEST_ACCESS_CAMERA:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          Logger.v(TAG, "access to camera was granted");
          if (toRunWhenAccessToContactsIsGranted != null) {
            toRunWhenAccessToContactsIsGranted.call();
          }
        } else {
          if (toRunWhenAccessToContactsIsDenied != null) {
            toRunWhenAccessToContactsIsDenied.call();
          }
        }
        break;

      case PERMISSIONS_REQUEST_READ_CONTACTS:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          Logger.v(TAG, "access to read and write to external storage was granted");
          if (toRunWhenAccessToContactsIsGranted != null) {
            toRunWhenAccessToContactsIsGranted.call();
          }
        } else {
          if (toRunWhenAccessToContactsIsDenied != null) {
            toRunWhenAccessToContactsIsDenied.call();
          }
        }
        break;

      case PERMISSIONS_REQUEST_LOCATION_AND_EXTERNAL_STORAGE:
        boolean allPermissionsGranted = true;
        for (int perm : grantResults) {
          if (perm != PackageManager.PERMISSION_GRANTED) {
            allPermissionsGranted = false;
          }
        }

        if (allPermissionsGranted) {
          Logger.v(TAG, "access to location and external storage was granted");
          if (toRunWhenAccessToLocationAndExternalStorageIsGranted != null) {
            toRunWhenAccessToLocationAndExternalStorageIsGranted.call();
          }
        } else {
          if (toRunWhenAccessToLocationAndExternalStorageIsDenied != null) {
            toRunWhenAccessToLocationAndExternalStorageIsDenied.call();
          }
        }
        break;

      case ACCESS_TO_EXTERNAL_FS_REQUEST_ID:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          Logger.v(TAG, "access to read and write to external storage was granted");
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
          Logger.v(TAG, "access to get accounts was granted");
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

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case PERMISSIONS_REQUEST_WRITE_SETTINGS:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          if (Settings.System.canWrite(this)) {
            Logger.v(TAG, "access to write settings was granted");
            if (toRunWhenAccessToWriteSettingsIsGranted != null) {
              toRunWhenAccessToWriteSettingsIsGranted.call();
            }
          } else {
            if (toRunWhenAccessToWriteSettingsIsDenied != null) {
              toRunWhenAccessToWriteSettingsIsDenied.call();
            }
          }
        }
        break;

      case PERMISSIONS_REQUEST_LOCATION_ENABLING:
        if (checkLocationPermission()) {
          if (toRunWhenLocationEnablingGranted != null) {
            toRunWhenLocationEnablingGranted.call();
          }
        } else {
          if (toRunWhenLocationEnablingDenied != null) {
            toRunWhenLocationEnablingDenied.call();
          }
        }

      default:
        super.onActivityResult(requestCode, resultCode, data);
        break;
    }
  }
}
