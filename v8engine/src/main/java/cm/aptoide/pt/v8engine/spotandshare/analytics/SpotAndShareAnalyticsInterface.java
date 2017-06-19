package cm.aptoide.pt.v8engine.spotandshare.analytics;

/**
 * Created by pedroribeiro on 03/03/17.
 */

public interface SpotAndShareAnalyticsInterface {

  void joinGroupSuccess();

  void createGroupSuccess();

  void createGroupFailed();

  void joinGroupFailed();

  void sendApkSuccess();

  void sendApkFailed();

  void receiveApkSuccess();

  void receiveApkFailed();

  void specialSettingsDenied();

  void specialSettingsGranted();
}
