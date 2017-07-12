package cm.aptoide.pt.spotandshareandroid.reflect;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by neuro on 12-07-2017.
 */
public class WifiManagerReflect {

  private final WifiManager wifiManager;

  private Method setWifiApConfiguration;
  private Method setWifiApEnabled;
  private Method getWifiApConfiguration;
  private Method isWifiApEnabled;
  private Method getWifiApState;

  private Method[] declaredMethods;

  public WifiManagerReflect(WifiManager wifiManager) {
    this.wifiManager = wifiManager;
  }

  private Method findMethods(String methodName) {

    if (declaredMethods == null) {
      declaredMethods = wifiManager.getClass()
          .getDeclaredMethods();
    }

    Method[] wmMethods = declaredMethods;
    for (Method method : wmMethods) {
      if (method.getName()
          .equals(methodName)) {
        return method;
      }
    }

    throw new RuntimeException("Couldn't find method!");
  }

  private Object invokeWithReturn(Method method, Object... objects) {
    try {
      return method.invoke(wifiManager, objects);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    throw new IllegalArgumentException("invokeWithReturn failed for method " + method.getName());
  }

  private void invoke(Method method, Object... objects) {
    try {
      method.invoke(wifiManager, objects);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  public boolean setWifiApEnabled(WifiConfiguration wifiConfiguration, boolean enable) {
    if (setWifiApEnabled == null) {
      setWifiApEnabled = findMethods("setWifiApEnabled");
    }
    return (boolean) invokeWithReturn(setWifiApEnabled, wifiConfiguration, enable);
  }

  public WifiConfiguration getWifiApConfiguration() {
    if (getWifiApConfiguration == null) {
      getWifiApConfiguration = findMethods("getWifiApConfiguration");
    }
    return (WifiConfiguration) invokeWithReturn(getWifiApConfiguration);
  }

  public void setWifiApConfiguration(WifiConfiguration wifiConfiguration) {
    if (setWifiApConfiguration == null) {
      setWifiApConfiguration = findMethods("setWifiApConfiguration");
    }

    invoke(setWifiApConfiguration, wifiConfiguration);
  }

  public boolean isWifiApEnabled() {
    if (isWifiApEnabled == null) {
      isWifiApEnabled = findMethods("isWifiApEnabled");
    }
    return (boolean) invokeWithReturn(isWifiApEnabled);
  }

  public int getWifiApState() {
    if (getWifiApState == null) {
      getWifiApState = findMethods("getWifiApState");
    }
    return (int) invokeWithReturn(getWifiApState);
  }
}
