package cm.aptoide.pt.spotandshareandroid.transfermanager;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;

/**
 * Created by neuro on 11-07-2017.
 */
public class TransferSending extends Transfer<TransferSending> {

  private final AndroidAppInfo androidAppInfo;

  protected TransferSending(AndroidAppInfo androidAppInfo, TransferManager transferManager) {
    super(State.SERVING, transferManager);
    this.androidAppInfo = androidAppInfo;
  }

  @Override public AndroidAppInfo getAndroidAppInfo() {
    return androidAppInfo;
  }
}
