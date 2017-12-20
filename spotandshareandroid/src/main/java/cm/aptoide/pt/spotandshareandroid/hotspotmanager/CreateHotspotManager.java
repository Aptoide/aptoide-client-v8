package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshareandroid.util.TaskQueue;
import cm.aptoide.pt.spotandshareandroid.util.service.WifiManagerReflect;
import rx.Completable;
import rx.Single;

/**
 * Created by filipe on 22-06-2017.
 */

class CreateHotspotManager {

  private static final String TAG = CreateHotspotManager.class.getSimpleName();
  private static final int SUCCESS_HOTSPOT_CREATION = 6;
  private static final int FAILED_TO_CREATE_HOTSPOT = 7;
  private final WifiManagerReflect wifiManagerReflect;
  private final WifiConfigurationHelper wifiConfigurationHelper;
  private final TaskQueue taskQueue;

  private WifiConfiguration storedWifiConfiguration;

  CreateHotspotManager(WifiManager wifimanager, TaskQueue taskQueue) {
    this.taskQueue = taskQueue;
    this.wifiConfigurationHelper = new WifiConfigurationHelper();
    this.wifiManagerReflect = new WifiManagerReflect(wifimanager);
  }

  public Completable enablePrivateHotspot(String SSID, String password) {
    return enableHotspot(wifiConfigurationHelper.newPrivateWifi(SSID, password)).flatMap(
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

  private Single<Integer> enableHotspot(WifiConfiguration wifiConfiguration) {

    return taskQueue.submitTask(Single.fromCallable(() -> {

      if (wifiManagerReflect.isWifiEnabled()) {
        wifiManagerReflect.setWifiEnabled(false);
      }

      storedWifiConfiguration = wifiManagerReflect.getWifiApConfiguration();

      boolean setWifiApEnabledResult = wifiManagerReflect.setWifiApEnabled(wifiConfiguration, true);

      while (!wifiManagerReflect.isWifiApEnabled()) {
      }

      int wifiApState = wifiManagerReflect.getWifiApState();

      if (setWifiApEnabledResult) {
        return SUCCESS_HOTSPOT_CREATION;
      } else {
        return FAILED_TO_CREATE_HOTSPOT;
      }
    }));
  }

  public Completable resetHotspot() {

    return taskQueue.submitTask(Completable.fromAction(() -> {

      if (storedWifiConfiguration == null) {
        throw new IllegalStateException("StoredWifiConfiguration is null!");
      }

      wifiManagerReflect.setWifiApConfiguration(storedWifiConfiguration);
      wifiManagerReflect.setWifiApEnabled(storedWifiConfiguration, false);
    }));
  }

  public Single<Boolean> isConfigurationStored() {
    return Single.just(storedWifiConfiguration != null);
  }
}
