package cm.aptoide.pt.spotandshareandroid.transfermanager;

import android.content.Context;
import android.net.ConnectivityManager;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Friend;
import cm.aptoide.pt.spotandshare.socket.interfaces.HostsChangedCallback;
import cm.aptoide.pt.spotandshare.socket.interfaces.TransferLifecycle;
import cm.aptoide.pt.spotandshare.socket.interfaces.TransferLifecycleProvider;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.AndroidAppInfoAccepter;
import cm.aptoide.pt.spotandshareandroid.SpotAndShareMessageServer;
import cm.aptoide.pt.spotandshareandroid.util.MessageServerConfiguration;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import rx.Observable;

/**
 * Created by neuro on 11-07-2017.
 */

public class TransferManager {

  private final TransferListRelay transferListRelay;
  private final SpotAndShareMessageServer spotAndShareMessageServer;
  @Getter private final AndroidAppInfoAccepter androidAppInfoAccepter;

  public TransferManager(SpotAndShareMessageServer spotAndShareMessageServer) {
    this.spotAndShareMessageServer = spotAndShareMessageServer;
    transferListRelay = new TransferListRelay();

    androidAppInfoAccepter = androidAppInfoAccepter1 -> transferListRelay.add(
        new TransferReceiving(androidAppInfoAccepter1, this));
  }

  public Observable<List<Transfer>> observeTransfers() {
    return transferListRelay.asObservable();
  }

  void callRelay() {
    transferListRelay.callRelay();
  }

  public void startClient(Context applicationContext, ConnectivityManager connectivityManager,
      Friend friend) {
    spotAndShareMessageServer.startClient(
        new MessageServerConfiguration(applicationContext, Throwable::printStackTrace,
            getAndroidAppInfoAccepter(), connectivityManager), friend,
        createTransferLifecycleProvider());
  }

  private TransferLifecycleProvider<AndroidAppInfo> createTransferLifecycleProvider() {
    return () -> new TransferLifecycle<AndroidAppInfo>() {

      private TransferSending transferSending;

      @Override public void onStartTransfer(AndroidAppInfo androidAppInfo) {
        transferSending = new TransferSending(androidAppInfo, TransferManager.this);
        transferListRelay.add(transferSending);
      }

      @Override public void onFinishTransfer(AndroidAppInfo androidAppInfo) {
        // TODO: 14-07-2017 neuro martelado
        transferSending.state = Transfer.State.ERROR;
        transferSending.behaviorRelay.call(transferSending);
      }

      @Override public void onProgressChanged(AndroidAppInfo androidAppInfo, float progress) {

      }

      @Override public void onError(IOException e) {
        transferSending.state = Transfer.State.ERROR;
        transferSending.behaviorRelay.call(transferSending);
      }
    };
  }

  public void startServer(HostsChangedCallback hostsChangedCallback) {
    spotAndShareMessageServer.startServer(hostsChangedCallback);
  }

  public void sendApp(AndroidAppInfo androidAppInfo) {
    spotAndShareMessageServer.sendApp(androidAppInfo);
  }

  public void sendApps(List<AndroidAppInfo> appsList) {
    spotAndShareMessageServer.sendApps(appsList);
  }

  public void shutdown() {
    spotAndShareMessageServer.exit();
  }

  public Observable<Collection<Friend>> observeFriends() {
    return spotAndShareMessageServer.observeFriends();
  }
}
