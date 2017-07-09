package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshareandroid.util.TaskQueue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import rx.Completable;
import rx.Single;

/**
 * Created by filipe on 22-06-2017.
 */

class CreateHotspotManager {

  private static final String TAG = CreateHotspotManager.class.getSimpleName();

  private static final int SUCCESS_HOTSPOT_CREATION = 6;
  private static final int FAILED_TO_CREATE_HOTSPOT = 7;

  private final WifiManager wifimanager;
  private final WifiConfigurationHelper wifiConfigurationHelper;

  private WifiConfiguration wifiConfiguration;

  private final TaskQueue taskQueue;

  CreateHotspotManager(WifiManager wifimanager, TaskQueue taskQueue) {
    this.wifimanager = wifimanager;
    this.taskQueue = taskQueue;
    this.wifiConfigurationHelper = new WifiConfigurationHelper();
  }

  public Completable enablePrivateHotspot(String SSID, String password_aptoide) {
    return enableHotspot(wifiConfigurationHelper.newPrivateWifi(SSID, password_aptoide)).flatMap(
        this::assertSuccessHotspotCreation)
        .toCompletable();
  }

  public Completable enableOpenHotspot(String SSID) {
    return enableHotspot(wifiConfigurationHelper.newPublicWifi(SSID)).flatMap(
        this::assertSuccessHotspotCreation)
        .toCompletable();
  }

  private Single<Void> assertSuccessHotspotCreation(int returnCode) {
    if (returnCode == SUCCESS_HOTSPOT_CREATION) {
      return Single.just(null);
    } else {
      return Single.error(new Exception("Failed to create hotspot"));
    }
  }

  private Single<Integer> enableHotspot(WifiConfiguration netConfig) {

    return taskQueue.submitTask(Single.fromCallable(() -> {
      if (wifimanager.isWifiEnabled()) {
        wifimanager.setWifiEnabled(false);
      }

      Method[] wmMethods = wifimanager.getClass()
          .getDeclaredMethods();   //Get all declared methods in WifiManager class
      for (Method method : wmMethods) {
        if (method.getName()
            .equals(ReflectionMethods.GET_WIFI_AP_CONFIGURATION)) {
          //Logger.d(TAG, "saving old ssid ");
          try {
            wifiConfiguration = (WifiConfiguration) method.invoke(wifimanager);
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.getCause()
                .printStackTrace();
            e.printStackTrace();
          }
        }
        if (method.getName()
            .equals(ReflectionMethods.SET_WIFI_AP_ENABLED)) {
          try {
            boolean apstatus = (Boolean) method.invoke(wifimanager, netConfig, true);
            for (Method isWifiApEnabledmethod : wmMethods) {
              if (isWifiApEnabledmethod.getName()
                  .equals(ReflectionMethods.IS_WIFI_AP_ENABLED)) {
                while (!(Boolean) isWifiApEnabledmethod.invoke(wifimanager)) {
                }
                for (Method method1 : wmMethods) {
                  if (method1.getName()
                      .equals(ReflectionMethods.GET_WIFI_AP_STATE)) {
                    int apstate = (Integer) method1.invoke(wifimanager);
                  }
                }
              }
            }
            if (apstatus) {
              //Logger.d(TAG, "created successful");
              return SUCCESS_HOTSPOT_CREATION;
            } else {
              //Logger.d(TAG, "FAILED TO CREATE");
              return FAILED_TO_CREATE_HOTSPOT;
            }
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.getCause()
                .printStackTrace();
            e.printStackTrace();
          }
        }
      }
      return HotspotManager.ERROR_UNKNOWN;
    }));
  }

  public Completable resetHotspot() {

    return taskQueue.submitTask(Completable.fromAction(() -> {
      if (wifiConfiguration == null) {
        throw new IllegalStateException("WifiConfiguration is null!");
      }

      Method[] wmMethods = wifimanager.getClass()
          .getDeclaredMethods();   //Get all declared methods in WifiManager class
      for (Method method : wmMethods) {
        if (method.getName()
            .equals(ReflectionMethods.SET_WIFI_AP_CONFIGURATION)) {

          try {
            Method setConfigMethod = wifimanager.getClass()
                .getMethod(ReflectionMethods.SET_WIFI_AP_CONFIGURATION, WifiConfiguration.class);
            //Logger.d(TAG, "Re-seting the wifiAp configuration to what it was before !!! ");
            setConfigMethod.invoke(wifimanager, wifiConfiguration);
          } catch (NoSuchMethodException e) {
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          }
        }
        if (method.getName()
            .equals(ReflectionMethods.SET_WIFI_AP_ENABLED)) {

          try {
            //Logger.d(TAG, "Desligar o hostpot ");
            method.invoke(wifimanager, wifiConfiguration, false);
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          }
        }
      }
    }));
  }

  private static class ReflectionMethods {
    private static final String SET_WIFI_AP_CONFIGURATION = "setWifiApConfiguration";
    private static final String SET_WIFI_AP_ENABLED = "setWifiApEnabled";
    private static final String GET_WIFI_AP_CONFIGURATION = "getWifiApConfiguration";
    private static final String IS_WIFI_AP_ENABLED = "isWifiApEnabled";
    private static final String GET_WIFI_AP_STATE = "getWifiApState";
  }
}
