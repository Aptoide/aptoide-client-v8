/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/07/2016.
 */

package cm.aptoide.pt.v8engine.toolbox;

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
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.managed.ManagedKeys;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.services.PullingContentService;
import java.util.Locale;
import java.util.Map;

/**
 * Created by marcelobenites on 7/7/16.
 */
public class ToolboxContentProvider extends ContentProvider {

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
  private AptoideAccountManager aptoideAccountManager;

  @Override public boolean onCreate() {
    securityManager = new ToolboxSecurityManager(getContext().getPackageManager());
    final String authority = Application.getConfiguration().getContentAuthority();
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI(authority, "token", TOKEN);
    uriMatcher.addURI(authority, "refreshToken", REFRESH_TOKEN);
    uriMatcher.addURI(authority, "repo", REPO);
    uriMatcher.addURI(authority, "loginType", LOGIN_TYPE);
    uriMatcher.addURI(authority, "passHash", PASSHASH);
    uriMatcher.addURI(authority, "loginName", LOGIN_NAME);
    uriMatcher.addURI(authority, "changePreference", CHANGE_PREFERENCE);
    aptoideAccountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    return true;
  }

  @Nullable @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sortOrder) {
    if (securityManager.checkSignature(Binder.getCallingUid(), BuildConfig.BACKUP_SIGNATURE,
        BACKUP_PACKAGE) || securityManager.checkSignature(Binder.getCallingUid(),
        BuildConfig.UPLOADER_SIGNATURE, UPLOADER_PACKAGE)) {

      final Account account = aptoideAccountManager.getAccount();
      switch (uriMatcher.match(uri)) {
        case TOKEN:
          if (account != null) {
            final MatrixCursor tokenCursor = new MatrixCursor(new String[] { "userToken" }, 1);
            tokenCursor.addRow(new Object[] { account.getAccessToken() });
            return tokenCursor;
          }
          throw new IllegalStateException("User not logged in.");
        case REFRESH_TOKEN:
          if (account != null) {
            final MatrixCursor tokenCursor =
                new MatrixCursor(new String[] { "userRefreshToken" }, 1);
            tokenCursor.addRow(new Object[] { account.getRefreshToken() });
            return tokenCursor;
          }
          throw new IllegalStateException("User not logged in.");
        case REPO:
          if (account != null) {
            final MatrixCursor userRepoCursor = new MatrixCursor(new String[] { "userRepo" }, 1);
            userRepoCursor.addRow(new Object[] { account.getStoreName() });
            return userRepoCursor;
          }
          throw new IllegalStateException("User not logged in.");
        case PASSHASH:

          if (account != null) {
            final MatrixCursor passwordCursor = new MatrixCursor(new String[] { "userPass" }, 1);
            if (Account.Type.APTOIDE.equals(account.getType())) {
              passwordCursor.addRow(new String[] {
                  AptoideUtils.AlgorithmU.computeSha1(account.getPassword())
              });
              return passwordCursor;
            } else if (Account.Type.FACEBOOK.equals(account.getType())
                || Account.Type.GOOGLE.equals(account.getType())) {
              passwordCursor.addRow(new String[] { account.getPassword() });
              return passwordCursor;
            }
          }
          throw new IllegalStateException("User not logged in.");
        case LOGIN_TYPE:

          if (account != null) {
            final MatrixCursor loginTypeCursor = new MatrixCursor(new String[] { "loginType" }, 1);
            loginTypeCursor.addRow(
                new String[] { account.getType().name().toLowerCase(Locale.US) });
            return loginTypeCursor;
          }
          throw new IllegalStateException("User not logged in.");
        case LOGIN_NAME:

          if (account != null) {
            final MatrixCursor userRepoCursor = new MatrixCursor(new String[] { "loginName" }, 1);
            userRepoCursor.addRow(new Object[] { account.getEmail() });
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
                } else if (entry.getKey().equals("UpdatesAction")) {
                  Intent intent = new Intent(context, PullingContentService.class);
                  intent.setAction(PullingContentService.UPDATES_ACTION);
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
