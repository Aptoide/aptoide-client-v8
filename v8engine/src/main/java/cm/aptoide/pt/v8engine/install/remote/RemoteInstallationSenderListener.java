package cm.aptoide.pt.v8engine.install.remote;

/**
 * Created by franciscoaleixo on 15/08/2016.
 */
public interface RemoteInstallationSenderListener {

  /**
   * Called when service discovery has started.
   */
  void onDiscoveryStarted();

  /**
   * Called when service discovery has stopped.
   */
  void onDiscoveryStopped();

  /**
   * Called when a service has been lost/removed.
   */
  void onAptoideTVServiceLost(ReceiverDevice device);

  /**
   * Called when a service has been discovered/found.
   */
  void onAptoideTVServiceFound(ReceiverDevice device);

  /**
   * Called when target device successfully received AppID/MD5.
   */
  void onAppSendSuccess();

  /**
   * Called when target device unsuccessfully received AppID/MD5.
   */
  void onAppSendUnsuccess();

  /**
   * Called when device is not connected to the network.
   */
  void onNoNetworkAccess();
}
