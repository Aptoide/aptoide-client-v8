/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/07/2016.
 */

package cm.aptoide.pt.v8engine.toolbox;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.BuildConfig;
import cm.aptoide.accountmanager.Constants;
import cm.aptoide.accountmanager.util.UserCompleteData;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.pt.actions.UserData;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.managed.ManagedKeys;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.services.PullingContentService;
import java.util.Locale;
import java.util.Map;

/**
 * Created by marcelobenites on 7/7/16.
 */
public class ToolboxContentProvider extends ContentProvider {
  private static final String TAG = ToolboxContentProvider.class.getSimpleName();

  private static final String BACKUP_PACKAGE = "pt.aptoide.backupapps";
  private static final String UPLOADER_PACKAGE = "pt.caixamagica.aptoide.uploader";
  private static final int TOKEN = 1;
  private static final int REPO = 2;
  private static final int PASSHASH = 3;
  private static final int LOGIN_TYPE = 4;
  private static final int LOGIN_NAME = 5;
  private static final int CHANGE_PREFERENCE = 6;
  private static final int REFRESH_TOKEN = 7;

  private UriMatcher uriMatcher;
  private ToolboxSecurityManager securityManager;

  @Override public boolean onCreate() {
    securityManager = new ToolboxSecurityManager(getContext().getPackageManager());
    final String authority = BuildConfig.APPLICATION_ID + ".UpdatesProvider";
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI(authority, "token", TOKEN);
    uriMatcher.addURI(authority, "refreshToken", REFRESH_TOKEN);
    uriMatcher.addURI(authority, "repo", REPO);
    uriMatcher.addURI(authority, "loginType", LOGIN_TYPE);
    uriMatcher.addURI(authority, "passHash", PASSHASH);
    uriMatcher.addURI(authority, "loginName", LOGIN_NAME);
    uriMatcher.addURI(authority, "changePreference", CHANGE_PREFERENCE);
    return true;
  }

  @Nullable @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sortOrder) {
    if (securityManager.checkSignature(Binder.getCallingUid(), BuildConfig.BACKUP_SIGNATURE,
        BACKUP_PACKAGE) || securityManager.checkSignature(Binder.getCallingUid(),
        BuildConfig.UPLOADER_SIGNATURE, UPLOADER_PACKAGE)) {
      switch (uriMatcher.match(uri)) {
        case TOKEN:
          final String accessToken = AptoideAccountManager.getAccessToken();
          if (accessToken != null) {
            final MatrixCursor tokenCursor = new MatrixCursor(new String[] { "userToken" }, 1);
            tokenCursor.addRow(new Object[] { accessToken });
            return tokenCursor;
          }
          throw new IllegalStateException("User not logged in.");
        case REFRESH_TOKEN:
          final String refreshedToken = AptoideAccountManager.getRefreshToken();

          if (refreshedToken != null) {
            final MatrixCursor tokenCursor =
                new MatrixCursor(new String[] { "userRefreshToken" }, 1);
            tokenCursor.addRow(new Object[] { refreshedToken });
            return tokenCursor;
          }
          throw new IllegalStateException("User not logged in.");
        case REPO:

          final UserCompleteData userCompleteData = AptoideAccountManager.getUserData();
          if (userCompleteData != null) {
            final MatrixCursor userRepoCursor = new MatrixCursor(new String[] { "userRepo" }, 1);
            userRepoCursor.addRow(new Object[] { userCompleteData.getUserRepo() });
            return userRepoCursor;
          }
          throw new IllegalStateException("User not logged in.");
        case PASSHASH:

          final AccountManager accountManager = AccountManager.get(getContext());
          final Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
          final LoginMode loginMode = AptoideAccountManager.getLoginMode();
          if (accounts.length > 0 && loginMode != null) {
            final MatrixCursor passwordCursor = new MatrixCursor(new String[] { "userPass" }, 1);
            if (LoginMode.APTOIDE.equals(loginMode)) {
              passwordCursor.addRow(new String[] {
                  AptoideUtils.AlgorithmU.computeSha1(accountManager.getPassword(accounts[0]))
              });
              return passwordCursor;
            } else if (LoginMode.APTOIDE.equals(loginMode) || LoginMode.GOOGLE.equals(loginMode)) {
              passwordCursor.addRow(new String[] { accountManager.getPassword(accounts[0]) });
              return passwordCursor;
            }
          }
          throw new IllegalStateException("User not logged in.");
        case LOGIN_TYPE:

          final LoginMode loginType = AptoideAccountManager.getLoginMode();
          if (loginType != null) {
            final MatrixCursor loginTypeCursor = new MatrixCursor(new String[] { "loginType" }, 1);
            loginTypeCursor.addRow(new String[] { loginType.name().toLowerCase(Locale.US) });
            return loginTypeCursor;
          }
          throw new IllegalStateException("User not logged in.");
        case LOGIN_NAME:

          final UserData userName = AptoideAccountManager.getUserData();
          if (userName != null) {
            final MatrixCursor userRepoCursor = new MatrixCursor(new String[] { "loginName" }, 1);
            userRepoCursor.addRow(new Object[] { userName.getUserEmail() });
            return userRepoCursor;
          }
          throw new IllegalStateException("User not logged in.");
        default:
          throw new IllegalArgumentException(
              "Only /token, /repo, /passHash, /loginType and /loginName supported.");
      }
    } else {
      throw new SecurityException("Package not authorized to access provider.");
    }
  }

  @Nullable @Override public String getType(Uri uri) {
    return null;
  }

  @Nullable @Override public Uri insert(Uri uri, ContentValues values) {
    return null;
  }

  @Override public int delete(Uri uri, String selection, String[] selectionArgs) {
    return 0;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

    int changed = 0;
    try {
      int uid = Binder.getCallingUid();
      Context context = getContext();
      PackageManager pm = context.getPackageManager();
      String callerPackage = pm.getPackagesForUid(uid)[0];
      Log.d("AptoideDebug", "Someone is trying to update preferences");
      int result = pm.checkSignatures(callerPackage, context.getPackageName());

      if (result == PackageManager.SIGNATURE_MATCH) {
        switch (uriMatcher.match(uri)) {
          case CHANGE_PREFERENCE:
            SharedPreferences.Editor edit =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
            for (final Map.Entry<String, Object> entry : values.valueSet()) {
              Object value = entry.getValue();
              if (value instanceof String) {
                if (!ManagerPreferences.isDebug()) {
                  AptoideUtils.ThreadU.runOnUiThread(
                      () -> Toast.makeText(context, "Please enable debug mode for toolbox to work.",
                          Toast.LENGTH_LONG).show());
                }
                if (entry.getKey().equals(ManagedKeys.FORCE_COUNTRY)) {
                  ManagerPreferences.setForceCountry((String) value);
                  changed++;
                } else if (entry.getKey().equals(ManagedKeys.NOTIFICATION_TYPE)) {
                  ManagerPreferences.setNotificationType((String) value);
                  changed++;
                } else if (entry.getKey().equals("pullNotificationAction")) {
                  Intent intent = new Intent(context, PullingContentService.class);
                  intent.setAction(PullingContentService.PUSH_NOTIFICATIONS_ACTION);
                  context.startService(intent);
                  changed++;
                }
              } else if (value instanceof Boolean) {
                if (entry.getKey().equals(ManagedKeys.DEBUG)) {
                  ManagerPreferences.setDebug((Boolean) entry.getValue());
                  Logger.setDBG((Boolean) entry.getValue());
                  changed++;
                }
              }
              if (changed > 0 && !TextUtils.isEmpty(entry.getValue().toString())) {
                AptoideUtils.ThreadU.runOnUiThread(() -> Toast.makeText(context,
                    "Preference set: " + entry.getKey() + "=" + entry.getValue(), Toast.LENGTH_LONG)
                    .show());
              }
            }

            edit.apply();
            return changed;
          default:
            return changed;
        }
      }
    } catch (NullPointerException e) {
      //it can happen if package manager or context is null
      CrashReport.getInstance().log(e);
    }
    return changed;
  }
}
