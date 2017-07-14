package cm.aptoide.pt.spotandshareandroid.transfermanager;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.interfaces.TransferLifecycle;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.Accepter;
import java.io.IOException;
import rx.Observable;

/**
 * Created by neuro on 11-07-2017.
 */
public class TransferReceiving extends Transfer<TransferReceiving> {

  private final Accepter<AndroidAppInfo> androidAppInfoAccepter;

  private TransferReceiving(Accepter<AndroidAppInfo> androidAppInfoAccepter, State state,
      TransferManager transferManager) {
    super(state, transferManager);
    this.androidAppInfoAccepter = androidAppInfoAccepter;
  }

  TransferReceiving(Accepter<AndroidAppInfo> androidAppInfoAccepter,
      TransferManager transferManager) {
    this(androidAppInfoAccepter, State.PENDING_ACCEPTION, transferManager);
  }

  public Observable<TransferReceiving> accept() {
    if (state != State.PENDING_ACCEPTION) {
      throw new IllegalStateException("Transfer not pending acception!");
    }

    state = State.RECEIVING;
    androidAppInfoAccepter.accept(new TransferLifecycleRelay(this, transferManager));
    behaviorRelay.call(this);
    transferManager.callRelay();

    return behaviorRelay;
  }

  public AndroidAppInfo getAndroidAppInfo() {
    return androidAppInfoAccepter.getMeta();
  }

  public Observable<Float> getProgressObservable() {
    return behaviorRelay.filter(transfer -> transfer.getState() == State.RECEIVING)
        .map(transfer -> getProgress());
  }

  public boolean isPendingAcception() {
    return state == State.PENDING_ACCEPTION;
  }

  static class TransferLifecycleRelay implements TransferLifecycle<AndroidAppInfo> {

    private final TransferReceiving transferReceiving;
    private final TransferManager transferManager;

    TransferLifecycleRelay(TransferReceiving transferReceiving, TransferManager transferManager) {
      this.transferReceiving = transferReceiving;
      this.transferManager = transferManager;
    }

    private void notifyListeners() {
      transferReceiving.behaviorRelay.call(transferReceiving);
      transferManager.callRelay();
    }

    @Override public void onStartTransfer(AndroidAppInfo androidAppInfo) {
      transferReceiving.state = State.PENDING;
      notifyListeners();
    }

    @Override public void onFinishTransfer(AndroidAppInfo androidAppInfo) {
      transferReceiving.state = State.RECEIVED;
      notifyListeners();
    }

    @Override public void onProgressChanged(AndroidAppInfo androidAppInfo, float progress) {
      transferReceiving.progress = progress;
      notifyListeners();
    }

    @Override public void onError(IOException e) {
      transferReceiving.state = State.ERROR;
      notifyListeners();
    }
  }
}
