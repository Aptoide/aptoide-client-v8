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
import cm.aptoide.accountmanager.Constants;
import cm.aptoide.accountmanager.util.UserCompleteData;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.pt.actions.UserData;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.managed.ManagedKeys;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
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
  private static final String BACKUP_SIGNATURE =
      "308203643082024ca0030201020204503fc625300d06092a864886f70d01010505003073310b30090603550406130270743110300e06035504081307556e6b6e6f776e310f300d060355040713064c6973626f6e31153013060355040a130c4361697861204d61676963613110300e060355040b13074170746f696465311830160603550403130f4475617274652053696c76656972613020170d3132303833303139353933335a180f32303934313031393139353933335a3073310b30090603550406130270743110300e06035504081307556e6b6e6f776e310f300d060355040713064c6973626f6e31153013060355040a130c4361697861204d61676963613110300e060355040b13074170746f696465311830160603550403130f4475617274652053696c766569726130820122300d06092a864886f70d01010105000382010f003082010a0282010100a7032cb40819b62cd596bc1c121951724e9a7d6612222d63dab58a18970339f77911b8e2a0665aa15efb051d4dd710c99e1fcaea006a651b7c113a71649c315e27122b9e0a214a240f34559394cca116c609d5bbf670ed85c7b983f0026154278bffd2b53d8aea4735ed99c39ea45db004c16bee078bb0b40e38ae510cacd1955a4e3eb90347d344cdcce07bddb89d9cd2077558914179a8157a87eac86e1b1a07a3f697a5f3f6512e276741d76bcc0c4809117c279fbd55d8c2b3d70468fbe4869394d9f2740bcccdf727da10c06de5c6a0d2f893bce078e058604726d32ab17e3b113a3dcbe0c22f2532738cae8cc5fa98c6b8306680b07ef8f0fca5d5910b0203010001300d06092a864886f70d01010505000382010100361152e42ece11bfd72e5795c9e91079b39c5280e30e3394671ca108fd7de9c3cebef2fc2f5ba752664ba44fcddaf49e91a1d7683cafdc11275fa7c1487ae78a659a8dae5d696cd93de810c67f127568dfa60c1962ec5ad2a3ea0560f75ad4a2ea9d388d4497b561242f090de2d3347dd32494ba6305735fa21d82f037f4355583fdfb1f46a56c19526969ba5f7f556cca9b9069cd9a9e3cd566d2b8c33138609e8794fb0abb11d33ed2c507f7f7df9ce24b3b64713ccdf2450bb5ec4efedba541dce271c8b3759b340b0467c06624cd3881b769a1d4a1b1fc0bec97d6b8561b032089ab8ca108595759bbd9b95fd43a3d28f518fb9d193125c8fa9b224f831c";
  private static final String UPLOADER_PACKAGE = "pt.caixamagica.aptoide.uploader";
  private static final String UPLOADER_SIGNATURE =
      "308205653082034ca00302010202044df76b53300d06092a864886f70d01010505003073310b30090603550406130270743110300e06035504081307556e6b6e6f776e310f300d060355040713064c6973626f6131153013060355040a130c4361697861204d61676963613110300e060355040b1307556e6b6e6f776e311830160603550403130f4475617274652053696c76656972613020170d3131303631343134303831395a180f32303933303830323134303831395a3073310b30090603550406130270743110300e06035504081307556e6b6e6f776e310f300d060355040713064c6973626f6131153013060355040a130c4361697861204d61676963613110300e060355040b1307556e6b6e6f776e311830160603550403130f4475617274652053696c766569726130820222300d06092a864886f70d01010105000382020f003082020a02820201026cfe7512fa0c40520971ee83e227208e072a1e1962a4fd0cd5c709e33dc45ce856e9ddc2b9a918394e96ec462d5fea2db81c443b9dbedd75a1031a1f1593b86eef83302f9ecdc0dfd227a3e11ccedb056e58c79b9177dbefba122a390dac88a90a317cb55a9171ab428b46c2e29b5d7fef2e823f5985b9c165a1edba7c82b4f8d5e3aa346996019cb8b7bcc768f5fdae15975add5e53c1fc022e4c99dababf3a80c5a09680ba4b8889cc4399940d92d11c289268d3f2671b98f871964f21c5870d9a1c72c8fbea65a637a06643f246e733fff37b7db4020fd2b6e7343fdbac2ddd20f8a48710d944d8f76432a3225f72c6a50c4e76247fb9256f294eeb9e24080ad28094fbfcfa6e4b5a85d652b1c5d967b39ee1272955a134a0ff1e89bb01f98d710204c72ca4c9dd44ecdd81358a8ef920fa371edd1bfc097c81678aa31b059b9218eba5c0ed2c209bd799a3ecab19e5e3b0e3d18029bf156b37e091969b4e5ae5024475b038b4d841e0e88580fd433154f606f1f7c14527f00509dd7448911e1ec44cb1e94f7dce59459e95438c4a245103d14fff047f97d14bf38f1802d84727b0f3aa98e02e8840892c629e303f76965e186de1d92263ec17e35aa224c33856d59095cf9195042ebfb5fd4703ef8add7ccf923640f266c22e432232f5c6b0873d99ebd509f9e66a77506eabef04ae1d9cf5edb40e13bc1cff39917da8b70203010001300d06092a864886f70d010105050003820202000069a29624d30983fdec4c4bf685f2f479214fda52e272a74ae8aee8bc7aae441ba79977cdd251cf5b21c56ee631dd1e17da28a2bd87d1190b4c1cc440140251e38af40aa694e6d3965c31b36ade9deccde0ca403639031f44f42e395b575a125cd210fd54e9ac760af1ed72c7b91f8f771074f6cafe0d28ab840510ee98a46eb84225be218ff6f90d036f47ec2e7dbfa067e9498cc633e5cab354ab86013b4d8047312643cdfbb6b3654dc26a87af0f4d83b2b0c6ad28d026483788daeda241c8e2631311e0e0d48c6f9284904cc4df114336c207e4c4f468f80f82f2d6917d8ec6b9e63fa2a0f126f668f8220667c92d26d55b5da7a4144b8693c0dec479a3c63b1d43eb96868eac1cb786e2f4b327bad553fc9ffe2dada3ab11bd6b1d7a623a92e821192b0dbcdabf0e4c361561bb5abb970d11e477050d56957fc8961106d2aaf1f209cbdde733a7a6e0577fd35d32f048e887b0e92c9415871e5b0d7458fe682256494b6c9443d04a076842d56374ee4c184a5c64a71c6818eafaa6dcbd66aae917907080d4895b7b0c941a4fae00be891666c0bdeb8b9331d0ff61d7ec2c26b80156aa64263e925dc9d84279bdb1e27e0403b57c14a1b2647a98c858ee20c92b967fb1eb963147fe390958e7c914fce69e1e2eb06139279b70a8eeabe99500ddf04223c3343e5c9b2722635856c65593aae9d2dbf3da704f79e8145f008e";

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
    aptoideAccountManager = AptoideAccountManager.getInstance(getContext(),
        Application.getConfiguration(), new SecureCoderDecoder.Builder(getContext().getApplicationContext()).create(),
        AccountManager.get(getContext().getApplicationContext()), new IdsRepositoryImpl(
            SecurePreferencesImplementation.getInstance(),
            getContext().getApplicationContext()));
    return true;
  }

  @Nullable @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sortOrder) {
    if (securityManager.checkSignature(Binder.getCallingUid(), BACKUP_SIGNATURE, BACKUP_PACKAGE)
        || securityManager.checkSignature(Binder.getCallingUid(), UPLOADER_SIGNATURE,
        UPLOADER_PACKAGE)) {
      switch (uriMatcher.match(uri)) {
        case TOKEN:
          final String accessToken = aptoideAccountManager.getAccessToken();
          if (accessToken != null) {
            final MatrixCursor tokenCursor = new MatrixCursor(new String[] { "userToken" }, 1);
            tokenCursor.addRow(new Object[] { accessToken });
            return tokenCursor;
          }
          throw new IllegalStateException("User not logged in.");
        case REFRESH_TOKEN:
          final String refreshedToken = aptoideAccountManager.getRefreshToken();

          if (refreshedToken != null) {
            final MatrixCursor tokenCursor =
                new MatrixCursor(new String[] { "userRefreshToken" }, 1);
            tokenCursor.addRow(new Object[] { refreshedToken });
            return tokenCursor;
          }
          throw new IllegalStateException("User not logged in.");
        case REPO:
          final UserCompleteData userCompleteData = aptoideAccountManager.getUserData();
          if (userCompleteData != null) {
            final MatrixCursor userRepoCursor = new MatrixCursor(new String[] { "userRepo" }, 1);
            userRepoCursor.addRow(new Object[] { userCompleteData.getUserRepo() });
            return userRepoCursor;
          }
          throw new IllegalStateException("User not logged in.");
        case PASSHASH:

          final AccountManager accountManager = AccountManager.get(getContext());
          final Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
          final LoginMode loginMode = aptoideAccountManager.getLoginMode();
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

          final LoginMode loginType = aptoideAccountManager.getLoginMode();
          if (loginType != null) {
            final MatrixCursor loginTypeCursor = new MatrixCursor(new String[] { "loginType" }, 1);
            loginTypeCursor.addRow(new String[] { loginType.name().toLowerCase(Locale.US) });
            return loginTypeCursor;
          }
          throw new IllegalStateException("User not logged in.");
        case LOGIN_NAME:

          final UserData userName = aptoideAccountManager.getUserData();
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
