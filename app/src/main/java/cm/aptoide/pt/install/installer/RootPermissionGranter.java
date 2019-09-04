package cm.aptoide.pt.install.installer;

import java.util.ArrayList;
import java.util.Arrays;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RootPermissionGranter {

  private static final String TAG = "RootPermissionGranter";
  private static String ROOT_COMMAND_GRANT_PERMISSION = "pm grant ";
  private ArrayList<String> googlePlayServicesPermissions;
  private ArrayList<String> googleContactsPermissions;
  private ArrayList<String> googleCalendarPermissions;
  private ArrayList<String> googleServicesFrameworkPermissions;
  private ArrayList<String> googlePlayStorePermissions;

  public RootPermissionGranter() {
    buildPermissionsLists();
  }

  private void buildPermissionsLists() {
    this.googlePlayServicesPermissions = getGooglePlayServicesPermissions();
    this.googleContactsPermissions = getGoogleContactsPermissions();
    this.googleCalendarPermissions = getGoogleCalendarPermissions();
    this.googleServicesFrameworkPermissions = getGoogleFrameworkPermissions();
    this.googlePlayStorePermissions = getGooglePlayStorePermissions();
  }

  private ArrayList<String> getGooglePlayStorePermissions() {
    String[] playStore = {
        "android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS",
        "android.permission.GET_ACCOUNTS", "android.permission.READ_PHONE_STATE",
        "android.permission.CALL_PHONE", "android.permission.READ_CALL_LOG",
        "android.permission.WRITE_CALL_LOG", "com.android.voicemail.permission.ADD_VOICEMAIL",
        "android.permission.USE_SIP", "android.permission.PROCESS_OUTGOING_CALLS",
        "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.SEND_SMS", "android.permission.RECEIVE_SMS",
        "android.permission.READ_SMS", "android.permission.RECEIVE_WAP_PUSH",
        "android.permission.RECEIVE_MMS", "android.permission.READ_CELL_BROADCASTS",
        "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"
    };
    return new ArrayList<>(Arrays.asList(playStore));
  }

  private ArrayList<String> getGoogleFrameworkPermissions() {
    String[] framework = {
        "android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS",
        "android.permission.GET_ACCOUNTS", "android.permission.READ_PHONE_STATE",
        "android.permission.CALL_PHONE", "android.permission.READ_CALL_LOG",
        "android.permission.WRITE_CALL_LOG", "com.android.voicemail.permission.ADD_VOICEMAIL",
        "android.permission.USE_SIP", "android.permission.PROCESS_OUTGOING_CALLS"
    };
    return new ArrayList<>(Arrays.asList(framework));
  }

  private ArrayList<String> getGoogleCalendarPermissions() {
    String[] calendar = { "android.permission.READ_CALENDAR", "android.permission.WRITE_CALENDAR" };
    return new ArrayList<>(Arrays.asList(calendar));
  }

  private ArrayList<String> getGoogleContactsPermissions() {
    String[] contacts = {
        "android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS",
        "android.permission.GET_ACCOUNTS"
    };
    return new ArrayList<>(Arrays.asList(contacts));
  }

  private ArrayList<String> getGooglePlayServicesPermissions() {
    String[] playServicePermissionSingletest = { "android.permission.READ_EXTERNAL_STORAGE" };

    String[] playServicesPermissions = {
        "android.permission.ACCESS_BACKGROUND_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_NETWORK_CONDITIONS", "android.permission.ACCESS_NETWORK_STATE",
        "android.permission.ACCESS_NOTIFICATION_POLICY", "android.permission.ACCESS_WIFI_STATE",
        "android.permission.ACTIVITY_EMBEDDING", "android.permission.ACTIVITY_RECOGNITION",
        "android.permission.ALLOCATE_AGGRESSIVE", "android.permission.AUTHENTICATE_ACCOUNTS",
        "android.permission.BACKUP", "android.permission.BLUETOOTH",
        "android.permission.BLUETOOTH_ADMIN", "android.permission.BLUETOOTH_PRIVILEGED",
        "android.permission.BODY_SENSORS", "android.permission.CALL_PHONE",
        "android.permission.CALL_PRIVILEGED", "android.permission.CAMERA",
        "android.permission.CAPTURE_AUDIO_HOTWORD", "android.permission.CAPTURE_AUDIO_OUTPUT",
        "android.permission.CAPTURE_SECURE_VIDEO_OUTPUT", "android.permission.CAPTURE_VIDEO_OUTPUT",
        "android.permission.CHANGE_DEVICE_IDLE_TEMP_WHITELIST",
        "android.permission.CHANGE_NETWORK_STATE", "android.permission.CHANGE_WIFI_MULTICAST_STATE",
        "android.permission.CHANGE_WIFI_STATE",
        "android.permission.CONNECTIVITY_USE_RESTRICTED_NETWORKS",
        "android.permission.CONTROL_DISPLAY_SATURATION",
        "android.permission.CONTROL_INCALL_EXPERIENCE",
        "android.permission.CONTROL_KEYGUARD_SECURE_NOTIFICATIONS",
        "android.permission.DISABLE_KEYGUARD", "android.permission.DISPATCH_PROVISIONING_MESSAGE",
        "android.permission.DOWNLOAD_WITHOUT_NOTIFICATION", "android.permission.FLASHLIGHT",
        "android.permission.FOREGROUND_SERVICE", "android.permission.GET_ACCOUNTS",
        "android.permission.GET_APP_OPS_STATS", "android.permission.GET_PACKAGE_SIZE",
        "android.permission.GET_TASKS", "android.permission.INTENT_FILTER_VERIFICATION_AGENT",
        "android.permission.INTERACT_ACROSS_USERS", "android.permission.INTERNET",
        "android.permission.LOCAL_MAC_ADDRESS", "android.permission.LOCATION_HARDWARE",
        "android.permission.LOCK_DEVICE", "android.permission.MANAGE_ACCOUNTS",
        "android.permission.MANAGE_ACTIVITY_STACKS", "android.permission.MANAGE_DEVICE_ADMINS",
        "android.permission.MANAGE_SOUND_TRIGGER", "android.permission.MANAGE_SUBSCRIPTION_PLANS",
        "android.permission.MANAGE_USB", "android.permission.MANAGE_VOICE_KEYPHRASES",
        "android.permission.MODIFY_AUDIO_ROUTING", "android.permission.MODIFY_AUDIO_SETTINGS",
        "android.permission.MODIFY_DAY_NIGHT_MODE", "android.permission.MODIFY_NETWORK_ACCOUNTING",
        "android.permission.MODIFY_PHONE_STATE", "android.permission.NFC",
        "android.permission.NOTIFY_PENDING_SYSTEM_UPDATE",
        "android.permission.OBSERVE_GRANT_REVOKE_PERMISSIONS",
        "android.permission.OVERRIDE_WIFI_CONFIG", "android.permission.PACKAGE_USAGE_STATS",
        "android.permission.PRE_FACTORY_RESET", "android.permission.PROCESS_OUTGOING_CALLS",
        "android.permission.PROVIDE_RESOLVER_RANKER_SERVICE",
        "android.permission.PROVIDE_TRUST_AGENT", "android.permission.READ_CALENDAR",
        "android.permission.READ_CALL_LOG", "android.permission.READ_CONTACTS",
        "android.permission.READ_DEVICE_CONFIG", "android.permission.READ_DREAM_STATE",
        "android.permission.READ_EXTERNAL_STORAGE", "android.permission.READ_LOGS",
        "android.permission.READ_MEDIA_AUDIO", "android.permission.READ_MEDIA_IMAGES",
        "android.permission.READ_MEDIA_VIDEO", "android.permission.READ_OEM_UNLOCK_STATE",
        "android.permission.READ_PHONE_STATE", "android.permission.READ_PRIVILEGED_PHONE_STATE",
        "android.permission.READ_PROFILE", "android.permission.READ_SMS",
        "android.permission.READ_SYNC_SETTINGS", "android.permission.READ_WIFI_CREDENTIAL",
        "android.permission.REAL_GET_TASKS", "android.permission.RECEIVE_BOOT_COMPLETED",
        "android.permission.RECEIVE_DATA_ACTIVITY_CHANGE", "android.permission.RECEIVE_MMS",
        "android.permission.RECEIVE_SMS", "android.permission.RECORD_AUDIO",
        "android.permission.RECOVER_KEYSTORE", "android.permission.RECOVERY",
        "android.permission.REGISTER_CALL_PROVIDER", "android.permission.REMOTE_DISPLAY_PROVIDER",
        "android.permission.REORDER_TASKS", "android.permission.REQUEST_DELETE_PACKAGES",
        "android.permission.RESET_PASSWORD", "android.permission.SCORE_NETWORKS",
        "android.permission.SEND_SMS", "android.permission.SEND_SMS_NO_CONFIRMATION",
        "android.permission.SET_TIME_ZONE", "android.permission.START_ACTIVITIES_FROM_BACKGROUND",
        "android.permission.START_TASKS_FROM_RECENTS", "android.permission.SUBSCRIBED_FEEDS_READ",
        "android.permission.SUBSCRIBED_FEEDS_WRITE",
        "android.permission.SUBSTITUTE_NOTIFICATION_APP_NAME",
        "android.permission.SUBSTITUTE_SHARE_TARGET_APP_NAME_AND_ICON",
        "android.permission.SYSTEM_ALERT_WINDOW", "android.permission.TETHER_PRIVILEGED",
        "android.permission.UPDATE_APP_OPS_STATS", "android.permission.USE_CREDENTIALS",
        "android.permission.USE_FINGERPRINT", "android.permission.USER_ACTIVITY",
        "android.permission.VIBRATE", "android.permission.WAKE_LOCK",
        "android.permission.WRITE_CALL_LOG", "android.permission.WRITE_CONTACTS",
        "android.permission.WRITE_DEVICE_CONFIG", "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.WRITE_PROFILE", "android.permission.WRITE_SETTINGS",
        "android.permission.WRITE_SYNC_SETTINGS",
        "com.android.launcher.permission.INSTALL_SHORTCUT",
        "com.android.vending.INTENT_VENDING_ONLY", "com.android.vending.setup.PLAY_SETUP_SERVICE",
        "com.android.voicemail.permission.ADD_VOICEMAIL",
        "com.android.voicemail.permission.READ_VOICEMAIL",
        "com.felicanetworks.mfc.mfi.permission.MFI_ACCESS",
        "com.felicanetworks.mfc.permission.MFC_ACCESS",
        "com.google.android.apps.enterprise.dmagent.permission.AutoSyncPermission",
        "com.google.android.apps.now.OPT_IN_WIZARD",
        "com.google.android.clockwork.settings.SHOW_FACTORY_RESET_CONFIRMATION",
        "com.google.android.finsky.permission.ACCESS_INSTANT_APP_NOTIFICATION_ENFORCEMENT",
        "com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE",
        "com.google.android.finsky.permission.GEARHEAD_SERVICE",
        "com.google.android.finsky.permission.INSTANT_APP_STATE",
        "com.google.android.gm.permission.READ_GMAIL",
        "com.google.android.gms.auth.api.signin.permission.REVOCATION_NOTIFICATION",
        "com.google.android.gms.auth.authzen.permission.DEVICE_SYNC_FINISHED",
        "com.google.android.gms.auth.authzen.permission.GCM_DEVICE_PROXIMITY",
        "com.google.android.gms.auth.authzen.permission.KEY_REGISTRATION_FINISHED",
        "com.google.android.gms.auth.cryptauth.permission.KEY_CHANGE",
        "com.google.android.gms.auth.permission.FACE_UNLOCK",
        "com.google.android.gms.auth.permission.GOOGLE_ACCOUNT_CHANGE",
        "com.google.android.gms.auth.permission.POST_SIGN_IN_ACCOUNT",
        "com.google.android.gms.auth.proximity.permission.SMS_CONNECT_SETUP_REQUESTED",
        "com.google.android.gms.carsetup.DRIVING_MODE_MANAGER",
        "com.google.android.gms.chimera.permission.CONFIG_CHANGE",
        "com.google.android.gms.chromesync.permission.CONTENT_PROVIDER_ACCESS",
        "com.google.android.gms.chromesync.permission.METADATA_UPDATED",
        "com.google.android.gms.cloudsave.BIND_EVENT_BROADCAST",
        "com.google.android.gms.common.internal.SHARED_PREFERENCES_PERMISSION",
        "com.google.android.gms.contextmanager.CONTEXT_MANAGER_RESTARTED_BROADCAST",
        "com.google.android.gms.DRIVE",
        "com.google.android.gms.game.notifications.permission.WRITE",
        "com.google.android.gms.googlehelp.LAUNCH_SUPPORT_SCREENSHARE",
        "com.google.android.gms.learning.permission.LAUNCH_IN_APP_PROXY",
        "com.google.android.gms.magictether.permission.CLIENT_TETHERING_PREFERENCE_CHANGED",
        "com.google.android.gms.magictether.permission.CONNECTED_HOST_CHANGED",
        "com.google.android.gms.magictether.permission.DISABLE_SOFT_AP",
        "com.google.android.gms.magictether.permission.SCANNED_DEVICE",
        "com.google.android.gms.permission.ACTIVITY_RECOGNITION",
        "com.google.android.gms.permission.APPINDEXING",
        "com.google.android.gms.permission.BIND_NETWORK_TASK_SERVICE",
        "com.google.android.gms.permission.BROADCAST_TO_GOOGLEHELP",
        "com.google.android.gms.permission.C2D_MESSAGE", "com.google.android.gms.permission.CAR",
        "com.google.android.gms.permission.CAR_SPEED",
        "com.google.android.gms.permission.CHECKIN_NOW",
        "com.google.android.gms.permission.CONTACTS_SYNC_DELEGATION",
        "com.google.android.gms.permission.GAMES_DEBUG_SETTINGS",
        "com.google.android.gms.permission.GOOGLE_PAY",
        "com.google.android.gms.permission.GRANT_WALLPAPER_PERMISSIONS",
        "com.google.android.gms.permission.INTERNAL_BROADCAST",
        "com.google.android.gms.permission.NEARBY_START_DISCOVERER",
        "com.google.android.gms.permission.PHENOTYPE_OVERRIDE_FLAGS",
        "com.google.android.gms.permission.PHENOTYPE_UPDATE_BROADCAST",
        "com.google.android.gms.permission.READ_VALUABLES_IMAGES",
        "com.google.android.gms.permission.REPORT_TAP",
        "com.google.android.gms.permission.REQUEST_SCREEN_LOCK_COMPLEXITY",
        "com.google.android.gms.permission.SAFETY_NET",
        "com.google.android.gms.permission.SEND_ANDROID_PAY_DATA",
        "com.google.android.gms.permission.SHOW_PAYMENT_CARD_DETAILS",
        "com.google.android.gms.permission.SHOW_TRANSACTION_RECEIPT",
        "com.google.android.gms.permission.SHOW_WARM_WELCOME_TAPANDPAY_APP",
        "com.google.android.gms.permission.TRANSFER_WIFI_CREDENTIAL",
        "com.google.android.gms.trustagent.framework.model.DATA_ACCESS",
        "com.google.android.gms.trustagent.framework.model.DATA_CHANGE_NOTIFICATION",
        "com.google.android.gms.trustagent.permission.TRUSTAGENT_STATE",
        "com.google.android.googlequicksearchbox.permission.PAUSE_HOTWORD",
        "com.google.android.hangouts.START_HANGOUT",
        "com.google.android.launcher.permission.RECEIVE_LAUNCH_BROADCASTS",
        "com.google.android.permission.ACCESSIBILITY_SCAN_SERVICE",
        "com.google.android.providers.gsf.permission.READ_GSERVICES",
        "com.google.android.providers.settings.permission.READ_GSETTINGS",
        "com.google.android.providers.settings.permission.WRITE_GSETTINGS",
        "com.google.android.setupwizard.SETUP_COMPAT_SERVICE",
        "com.google.android.vending.verifier.ACCESS_VERIFIER",
        "com.google.android.wearable.READ_SETTINGS", "com.google.android.wearable.WRITE_SETTINGS",
        "com.google.firebase.auth.api.gms.permission.LAUNCH_FEDERATED_SIGN_IN"
    };

    googlePlayServicesPermissions = new ArrayList<>(Arrays.asList(playServicesPermissions));

    return googlePlayServicesPermissions;
  }

  public Observable<String> grantMultiplePermissions(String packageName) {

    return Observable.from(getPermissionsListForPackage(packageName))
        .flatMap(permission -> grantPermission(permission, packageName))
        .doOnError(throwable -> throwable.printStackTrace());
  }

  private Observable<String> grantPermission(String permission, String packageName) {
    return Observable.create(new RootCommandPermissionOnSubscribe(packageName,
        ROOT_COMMAND_GRANT_PERMISSION + packageName + " " + permission))
        .subscribeOn(Schedulers.computation())
        .map(__ -> "teste");
  }

  private ArrayList<String> getPermissionsListForPackage(String packageName) {
    ArrayList<String> listOfPermissions;
    switch (packageName) {
      case "com.google.android.syncadapters.contacts":
        listOfPermissions = googleContactsPermissions;
        break;
      case "com.google.android.syncadapters.calendar":
        listOfPermissions = googleCalendarPermissions;
        break;
      case "com.google.android.gsf":
        listOfPermissions = googleServicesFrameworkPermissions;
        break;
      case "com.google.android.gms":
        listOfPermissions = googlePlayServicesPermissions;
        break;
      case "com.android.vending":
        listOfPermissions = googlePlayStorePermissions;
        break;
      default:
        listOfPermissions = googlePlayServicesPermissions;
        break;
    }
    return listOfPermissions;
  }
}
