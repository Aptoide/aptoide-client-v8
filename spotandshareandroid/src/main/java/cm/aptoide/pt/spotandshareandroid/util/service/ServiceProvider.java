package cm.aptoide.pt.spotandshareandroid.util.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.app.job.JobScheduler;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.HardwarePropertiesManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by neuro on 12-07-2017.
 */

public class ServiceProvider {

  private final Context applicationContext;

  public ServiceProvider(Context context) {
    this.applicationContext = context.getApplicationContext();
  }

  public WindowManager getWindowManager() {
    return (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
  }

  public LayoutInflater getLayoutInflater() {
    return (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  public ActivityManager getActivityManager() {
    return (ActivityManager) applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
  }

  public PowerManager getPowerManager() {
    return (PowerManager) applicationContext.getSystemService(Context.POWER_SERVICE);
  }

  public AlarmManager getAlarmManager() {
    return (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
  }

  public NotificationManager getNotificationManager() {
    return (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
  }

  public KeyguardManager getKeyguardManager() {
    return (KeyguardManager) applicationContext.getSystemService(Context.KEYGUARD_SERVICE);
  }

  public LocationManager getLocationManager() {
    return (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);
  }

  public SearchManager getSearchManager() {
    return (SearchManager) applicationContext.getSystemService(Context.SEARCH_SERVICE);
  }

  public SensorManager getSensorManager() {
    return (SensorManager) applicationContext.getSystemService(Context.SENSOR_SERVICE);
  }

  public StorageManager getStorageManager() {
    return (StorageManager) applicationContext.getSystemService(Context.STORAGE_SERVICE);
  }

  public Vibrator getVibrator() {
    return (Vibrator) applicationContext.getSystemService(Context.VIBRATOR_SERVICE);
  }

  public ConnectivityManager getConnectivityManager() {
    return (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
  }

  public WifiManager getWifiManager() {
    return (WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE);
  }

  public WifiManagerReflect getWifiManagerReflect() {
    return new WifiManagerReflect(getWifiManager());
  }

  public AudioManager getAudioManager() {
    return (AudioManager) applicationContext.getSystemService(Context.AUDIO_SERVICE);
  }

  public MediaRouter getMediaRouter() {
    return (MediaRouter) applicationContext.getSystemService(Context.MEDIA_ROUTER_SERVICE);
  }

  public TelephonyManager getTelephonyManager() {
    return (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
  }

  public SubscriptionManager getSubscriptionManager() {
    return (SubscriptionManager) applicationContext.getSystemService(
        Context.TELEPHONY_SUBSCRIPTION_SERVICE);
  }

  public CarrierConfigManager getCarrierConfigManager() {
    return (CarrierConfigManager) applicationContext.getSystemService(
        Context.CARRIER_CONFIG_SERVICE);
  }

  public InputMethodManager getInputMethodManager() {
    return (InputMethodManager) applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE);
  }

  public UiModeManager getUiModeManager() {
    return (UiModeManager) applicationContext.getSystemService(Context.UI_MODE_SERVICE);
  }

  public DownloadManager getDownloadManager() {
    return (DownloadManager) applicationContext.getSystemService(Context.DOWNLOAD_SERVICE);
  }

  public BatteryManager getBatteryManager() {
    return (BatteryManager) applicationContext.getSystemService(Context.BATTERY_SERVICE);
  }

  public JobScheduler getJobScheduler() {
    return (JobScheduler) applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
  }

  public NetworkStatsManager getNetworkStatsManager() {
    return (NetworkStatsManager) applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE);
  }

  public HardwarePropertiesManager getHardwarePropertiesManager() {
    return (HardwarePropertiesManager) applicationContext.getSystemService(
        Context.HARDWARE_PROPERTIES_SERVICE);
  }
}
