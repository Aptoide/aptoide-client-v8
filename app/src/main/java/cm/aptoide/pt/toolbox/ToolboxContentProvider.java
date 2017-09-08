/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/07/2016.
 */

package cm.aptoide.pt.toolbox;

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
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.networking.Authentication;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import cm.aptoide.pt.account.FacebookSignUpAdapter;
import cm.aptoide.pt.account.GoogleSignUpAdapter;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.notification.PullingContentService;
import cm.aptoide.pt.preferences.toolbox.ToolboxKeys;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.Locale;
import java.util.Map;

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
  private AuthenticationPersistence authenticationPersistence;
  private SharedPreferences sharedPreferences;
  private AptoideAccountManager accountManager;

  @Override public boolean onCreate() {
    securityManager = new ToolboxSecurityManager(getContext().getPackageManager());
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI(BuildConfig.CONTENT_AUTHORITY, "token", TOKEN);
    uriMatcher.addURI(BuildConfig.CONTENT_AUTHORITY, "refreshToken", REFRESH_TOKEN);
    uriMatcher.addURI(BuildConfig.CONTENT_AUTHORITY, "repo", REPO);
    uriMatcher.addURI(BuildConfig.CONTENT_AUTHORITY, "loginType", LOGIN_TYPE);
    uriMatcher.addURI(BuildConfig.CONTENT_AUTHORITY, "passHash", PASSHASH);
    uriMatcher.addURI(BuildConfig.CONTENT_AUTHORITY, "loginName", LOGIN_NAME);
    uriMatcher.addURI(BuildConfig.CONTENT_AUTHORITY, "changePreference", CHANGE_PREFERENCE);
    authenticationPersistence =
        ((AptoideApplication) getContext().getApplicationContext()).getAuthenticationPersistence();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    sharedPreferences =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences();
    return true;
  }

  @Nullable @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sortOrder) {
    if (securityManager.checkSignature(Binder.getCallingUid(), BuildConfig.BACKUP_SIGNATURE,
        BACKUP_PACKAGE) || securityManager.checkSignature(Binder.getCallingUid(),
        BuildConfig.UPLOADER_SIGNATURE, UPLOADER_PACKAGE)) {

      final Authentication authentication = authenticationPersistence.getAuthentication()
          .onErrorReturn(null)
          .toBlocking()
          .value();

      final Account account = accountManager.accountStatus()
          .toBlocking()
          .first();

      if (authentication == null || account == null) {
        throw new IllegalStateException("User not logged in.");
      }

      switch (uriMatcher.match(uri)) {
        case TOKEN:
          return create("userToken", authentication.getAccessToken());
        case REFRESH_TOKEN:
          return create("userRefreshToken", authentication.getRefreshToken());
        case REPO:
          return create("userRepo", account.getStore()
              .getName());
        case PASSHASH:
          if (AptoideAccountManager.APTOIDE_SIGN_UP_TYPE.equals(authentication.getType())) {
            return create("userPass",
                AptoideUtils.AlgorithmU.computeSha1(authentication.getPassword()));
          } else if (FacebookSignUpAdapter.TYPE.equals(authentication.getType()) || GoogleSignUpAdapter.TYPE.equals(
              authentication.getType())) {
            return create("userPass", authentication.getPassword());
          }
        case LOGIN_TYPE:
          return create("loginType", authentication.getType()
              .toLowerCase(Locale.US));
        case LOGIN_NAME:
          return create("loginName", authentication.getEmail());
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
            SharedPreferences.Editor edit = sharedPreferences.edit();
            for (final Map.Entry<String, Object> entry : values.valueSet()) {
              Object value = entry.getValue();
              if (value instanceof String) {
                if (!ToolboxManager.isDebug(sharedPreferences)) {
                  AptoideUtils.ThreadU.runOnUiThread(
                      () -> Toast.makeText(context, "Please enable debug mode for toolbox to work.",
                          Toast.LENGTH_LONG)
                          .show());
                }
                if (entry.getKey()
                    .equals(ToolboxKeys.FORCE_COUNTRY)) {
                  ToolboxManager.setForceCountry((String) value, sharedPreferences);
                  changed++;
                } else if (entry.getKey()
                    .equals(ToolboxKeys.NOTIFICATION_TYPE)) {
                  ToolboxManager.setNotificationType((String) value, sharedPreferences);
                  changed++;
                } else if (entry.getKey()
                    .equals("pullNotificationAction")) {
                  Intent intent = new Intent(context, PullingContentService.class);
                  intent.setAction(PullingContentService.PUSH_NOTIFICATIONS_ACTION);
                  context.startService(intent);
                  changed++;
                } else if (entry.getKey()
                    .equals("UpdatesAction")) {
                  Intent intent = new Intent(context, PullingContentService.class);
                  intent.setAction(PullingContentService.UPDATES_ACTION);
                  context.startService(intent);
                  changed++;
                }
              } else if (value instanceof Boolean) {
                if (entry.getKey()
                    .equals(ToolboxKeys.DEBUG)) {
                  ToolboxManager.setDebug((Boolean) entry.getValue(), sharedPreferences);
                  Logger.setDBG((Boolean) entry.getValue());
                  changed++;
                }
                if (entry.getKey()
                    .equals(ToolboxKeys.TOOLBOX_ENABLE_HTTP_SCHEME)) {
                  ToolboxManager.setToolboxEnableHttpScheme((Boolean) entry.getValue(),
                      sharedPreferences);
                  changed++;
                }
                if (entry.getKey()
                    .equals(ToolboxKeys.TOOLBOX_RETROFIT_LOGS)) {
                  ToolboxManager.setToolboxEnableRetrofitLogs((Boolean) entry.getValue(),
                      sharedPreferences);
                  changed++;
                }
              } else if (value instanceof Long) {
                if (entry.getKey()
                    .equals(ToolboxKeys.PUSH_NOTIFICATION_PULL_INTERVAL)) {
                  ToolboxManager.setPushNotificationPullingInterval(((Long) value),
                      sharedPreferences);
                  changed++;
                }
              }
              if (changed > 0 && !TextUtils.isEmpty(entry.getValue()
                  .toString())) {
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
      CrashReport.getInstance()
          .log(e);
    }
    return changed;
  }

  private MatrixCursor create(String key, String value) {
    final MatrixCursor cursor = new MatrixCursor(new String[] { key }, 1);
    return cursor;
  }
}
