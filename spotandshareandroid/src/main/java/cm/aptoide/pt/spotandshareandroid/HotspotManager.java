package cm.aptoide.pt.spotandshareandroid;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.logger.Logger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by filipe on 17-05-2017.
 */

public class HotspotManager {

  private static final String TAG = HotspotManager.class.getSimpleName();
  ;
  private Context context;
  private WifiManager wifimanager;

  public HotspotManager(Context context) {
    this.context = context;
    wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
  }

  public void resetHotspot(boolean enable) {
    WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    WifiConfiguration wc = DataHolder.getInstance()
        .getWcOnJoin();

    Method[] wmMethods = wifimanager.getClass()
        .getDeclaredMethods();   //Get all declared methods in WifiManager class
    boolean methodFound = false;
    for (Method method : wmMethods) {
      if (method.getName()
          .equals("setWifiApEnabled")) {

        try {
          method.invoke(wifimanager, wc, enable);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public int enablePrivateHotspot(String SSID) {
    if (wifimanager == null) {
      wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
    if (wifimanager.isWifiEnabled()) {
      wifimanager.setWifiEnabled(false);
    }
    Method[] wmMethods = wifimanager.getClass()
        .getDeclaredMethods();   //Get all declared methods in WifiManager class
    boolean methodFound = false;
    for (Method method : wmMethods) {
      if (method.getName()
          .equals("getWifiApConfiguration")) {
        Logger.d(TAG, "saving old ssid ");
        try {
          WifiConfiguration config = (WifiConfiguration) method.invoke(wifimanager);
          DataHolder.getInstance()
              .setWcOnJoin(config);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.getCause()
              .printStackTrace();
          e.printStackTrace();
        }
      }
      if (method.getName()
          .equals("setWifiApEnabled")) {
        methodFound = true;
        WifiConfiguration netConfig = new WifiConfiguration();
        netConfig.SSID = SSID;
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        netConfig.preSharedKey = "passwordAptoide";
        netConfig.status = WifiConfiguration.Status.ENABLED;
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        try {
          boolean apstatus = (Boolean) method.invoke(wifimanager, netConfig, true);
          for (Method isWifiApEnabledmethod : wmMethods) {
            if (isWifiApEnabledmethod.getName()
                .equals("isWifiApEnabled")) {
              while (!(Boolean) isWifiApEnabledmethod.invoke(wifimanager)) {
              }
              for (Method method1 : wmMethods) {
                if (method1.getName()
                    .equals("getWifiApState")) {
                  int apstate;
                  apstate = (Integer) method1.invoke(wifimanager);
                }
              }
            }
          }
          if (apstatus) {
            return ConnectionManager.SUCCESS_HOTSPOT_CREATION;
          } else {
            return ConnectionManager.FAILED_TO_CREATE_HOTSPOT;
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
    return ConnectionManager.ERROR_UNKNOWN;
  }

  public int enableOpenHotspot(String SSID) {
    if (wifimanager == null) {
      wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
    if (wifimanager.isWifiEnabled()) {
      wifimanager.setWifiEnabled(false);
    }
    Method[] wmMethods = wifimanager.getClass()
        .getDeclaredMethods();   //Get all declared methods in WifiManager class
    boolean methodFound = false;
    for (Method method : wmMethods) {
      if (method.getName()
          .equals("getWifiApConfiguration")) {
        Logger.d(TAG, "saving old ssid ");
        try {
          WifiConfiguration config = (WifiConfiguration) method.invoke(wifimanager);
          DataHolder.getInstance()
              .setWcOnJoin(config);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.getCause()
              .printStackTrace();
          e.printStackTrace();
        }
      }
      if (method.getName()
          .equals("setWifiApEnabled")) {
        methodFound = true;
        WifiConfiguration netConfig = new WifiConfiguration();
        netConfig.SSID = SSID;
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        netConfig.status = WifiConfiguration.Status.ENABLED;
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        try {
          boolean apstatus = (Boolean) method.invoke(wifimanager, netConfig, true);
          for (Method isWifiApEnabledmethod : wmMethods) {
            if (isWifiApEnabledmethod.getName()
                .equals("isWifiApEnabled")) {
              while (!(Boolean) isWifiApEnabledmethod.invoke(wifimanager)) {
              }
              for (Method method1 : wmMethods) {
                if (method1.getName()
                    .equals("getWifiApState")) {
                  int apstate = (Integer) method1.invoke(wifimanager);
                }
              }
            }
          }
          if (apstatus) {
            Logger.d(TAG, "created successful");
            return ConnectionManager.SUCCESS_HOTSPOT_CREATION;
          } else {
            Logger.d(TAG, "FAILED TO CREATE");
            return ConnectionManager.FAILED_TO_CREATE_HOTSPOT;
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
    return ConnectionManager.ERROR_UNKNOWN;
  }

  public void stop() {
    this.wifimanager = null;
    this.context = null;
  }
}
