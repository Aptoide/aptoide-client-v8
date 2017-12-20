package cm.aptoide.pt.spotandshareandroid.util.service;

import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.List;

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

  public boolean startScan() {
    return this.wifiManager.startScan();
  }

  public void setTdlsEnabledWithMacAddress(String remoteMacAddress, boolean enable) {
    this.wifiManager.setTdlsEnabledWithMacAddress(remoteMacAddress, enable);
  }

  public void cancelWps(WifiManager.WpsCallback listener) {
    this.wifiManager.cancelWps(listener);
  }

  public boolean pingSupplicant() {
    return this.wifiManager.pingSupplicant();
  }

  public boolean enableNetwork(int netId, boolean disableOthers) {
    return this.wifiManager.enableNetwork(netId, disableOthers);
  }

  public boolean isTdlsSupported() {
    return this.wifiManager.isTdlsSupported();
  }

  public int getWifiState() {
    return this.wifiManager.getWifiState();
  }

  public void setTdlsEnabled(InetAddress remoteIPAddress, boolean enable) {
    this.wifiManager.setTdlsEnabled(remoteIPAddress, enable);
  }

  public List<WifiConfiguration> getConfiguredNetworks() {
    return this.wifiManager.getConfiguredNetworks();
  }

  public boolean reconnect() {
    return this.wifiManager.reconnect();
  }

  public WifiManager.WifiLock createWifiLock(int lockType, String tag) {
    return this.wifiManager.createWifiLock(lockType, tag);
  }

  public boolean isEnhancedPowerReportingSupported() {
    return this.wifiManager.isEnhancedPowerReportingSupported();
  }

  public List<ScanResult> getScanResults() {
    return this.wifiManager.getScanResults();
  }

  public boolean saveConfiguration() {
    return this.wifiManager.saveConfiguration();
  }

  public boolean removeNetwork(int netId) {
    return this.wifiManager.removeNetwork(netId);
  }

  public boolean reassociate() {
    return this.wifiManager.reassociate();
  }

  public boolean isPreferredNetworkOffloadSupported() {
    return this.wifiManager.isPreferredNetworkOffloadSupported();
  }

  public boolean setWifiEnabled(boolean enabled) {
    return this.wifiManager.setWifiEnabled(enabled);
  }

  public WifiManager.WifiLock createWifiLock(String tag) {
    return this.wifiManager.createWifiLock(tag);
  }

  public boolean is5GHzBandSupported() {
    return this.wifiManager.is5GHzBandSupported();
  }

  public boolean disconnect() {
    return this.wifiManager.disconnect();
  }

  public boolean isDeviceToApRttSupported() {
    return this.wifiManager.isDeviceToApRttSupported();
  }

  public WifiManager.MulticastLock createMulticastLock(String tag) {
    return this.wifiManager.createMulticastLock(tag);
  }

  public int addNetwork(WifiConfiguration config) {
    return this.wifiManager.addNetwork(config);
  }

  public boolean isScanAlwaysAvailable() {
    return this.wifiManager.isScanAlwaysAvailable();
  }

  public boolean isP2pSupported() {
    return this.wifiManager.isP2pSupported();
  }

  public int updateNetwork(WifiConfiguration config) {
    return this.wifiManager.updateNetwork(config);
  }

  public void startWps(WpsInfo config, WifiManager.WpsCallback listener) {
    this.wifiManager.startWps(config, listener);
  }

  public WifiInfo getConnectionInfo() {
    return this.wifiManager.getConnectionInfo();
  }

  public boolean isWifiEnabled() {
    return this.wifiManager.isWifiEnabled();
  }

  public boolean disableNetwork(int netId) {
    return this.wifiManager.disableNetwork(netId);
  }

  public DhcpInfo getDhcpInfo() {
    return this.wifiManager.getDhcpInfo();
  }
}
