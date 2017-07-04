package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import rx.Single;

/**
 * Created by filipe on 22-06-2017.
 */

class CreateHotspotManager {

  private static final int SUCCESS_HOTSPOT_CREATION = 6;
  private static final int FAILED_TO_CREATE_HOTSPOT = 7;
  private WifiManager wifimanager;
  private WifiConfiguration wifiConfiguration;

  // TODO: 22-06-2017 neuro busy not implemented.. hmmm...
  private boolean busy = false;

  public CreateHotspotManager(WifiManager wifimanager) {
    this.wifimanager = wifimanager;
  }

  public Single<Void> enablePrivateHotspot(String SSID, String password_aptoide) {
    return enableHotspot(
        new WifiConfigurationHelper().newPrivateWifi(SSID, password_aptoide)).flatMap(integer -> {
      if (integer == SUCCESS_HOTSPOT_CREATION) {
        return Single.just(null);
      } else {
        return Single.error(new Exception("Failed to create hotspot"));
      }
    });

    //return enableHotspot(new WifiConfigurationHelper().newPrivateWifi(SSID, password_aptoide)).map(
    //    integer -> integer == SUCCESS_HOTSPOT_CREATION);
  }

  public Single<Boolean> enableOpenHotspot(String SSID) {
    return enableHotspot(new WifiConfigurationHelper().newPublicWifi(SSID)).map(
        integer -> integer == SUCCESS_HOTSPOT_CREATION);
  }

  private Single<Integer> enableHotspot(WifiConfiguration netConfig) {

    return Single.fromCallable(() -> {
      if (wifimanager.isWifiEnabled()) {
        wifimanager.setWifiEnabled(false);
      }

      Method[] wmMethods = wifimanager.getClass()
          .getDeclaredMethods();   //Get all declared methods in WifiManager class
      for (Method method : wmMethods) {
        if (method.getName()
            .equals(CreateHotspotManager.ReflectionMethods.GET_WIFI_AP_CONFIGURATION)) {
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
            .equals(CreateHotspotManager.ReflectionMethods.SET_WIFI_AP_ENABLED)) {
          try {
            boolean apstatus = (Boolean) method.invoke(wifimanager, netConfig, true);
            for (Method isWifiApEnabledmethod : wmMethods) {
              if (isWifiApEnabledmethod.getName()
                  .equals(CreateHotspotManager.ReflectionMethods.IS_WIFI_AP_ENABLED)) {
                while (!(Boolean) isWifiApEnabledmethod.invoke(wifimanager)) {
                }
                for (Method method1 : wmMethods) {
                  if (method1.getName()
                      .equals(CreateHotspotManager.ReflectionMethods.GET_WIFI_AP_STATE)) {
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
    });

  }

  public Single<Void> resetHotspot() {

    return Single.fromCallable(() -> {
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

      return null;
    });
  }

  private static class ReflectionMethods {
    private static final String SET_WIFI_AP_CONFIGURATION = "setWifiApConfiguration";
    private static final String SET_WIFI_AP_ENABLED = "setWifiApEnabled";
    private static final String GET_WIFI_AP_CONFIGURATION = "getWifiApConfiguration";
    private static final String IS_WIFI_AP_ENABLED = "isWifiApEnabled";
    private static final String GET_WIFI_AP_STATE = "getWifiApState";
  }
}
