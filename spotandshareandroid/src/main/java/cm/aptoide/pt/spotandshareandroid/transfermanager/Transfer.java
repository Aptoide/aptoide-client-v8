package cm.aptoide.pt.spotandshareandroid.transfermanager;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.interfaces.TransferLifecycle;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.Accepter;
import com.jakewharton.rxrelay.BehaviorRelay;
import java.io.IOException;
import lombok.Getter;
import rx.Observable;

/**
 * Created by neuro on 11-07-2017.
 */
public class Transfer {

  private final Accepter<AndroidAppInfo> androidAppInfoAccepter;
  private final BehaviorRelay<Transfer> behaviorRelay;

  @Getter private State state;
  @Getter private float progress;
  private TransferManager transferManager;

  private Transfer(Accepter<AndroidAppInfo> androidAppInfoAccepter, State state,
      TransferManager transferManager) {
    this.androidAppInfoAccepter = androidAppInfoAccepter;
    this.state = state;
    this.transferManager = transferManager;

    behaviorRelay = BehaviorRelay.create();
  }

  Transfer(Accepter<AndroidAppInfo> androidAppInfoAccepter, TransferManager transferManager) {
    this(androidAppInfoAccepter, State.PENDING_ACCEPTION, transferManager);
  }

  public Observable<Transfer> accept() {
    if (state != State.PENDING_ACCEPTION) {
      throw new IllegalStateException("Transfer not pending acception!");
    }

    state = State.RECEIVING;
    androidAppInfoAccepter.accept(new TransferLifecycleRelay(this, transferManager));
    behaviorRelay.call(this);

    return behaviorRelay;
  }

  public AndroidAppInfo getAndroidAppInfo() {
    return androidAppInfoAccepter.getMeta();
  }

  public Observable<Float> getProgressObservable() {
    return behaviorRelay.filter(transfer -> transfer.getState() == State.RECEIVING)
        .map(Transfer::getProgress);
  }

  public enum State {
    PENDING_ACCEPTION, PENDING, RECEIVING, RECEIVED, ERROR, SERVING,
  }

  private static class TransferLifecycleRelay implements TransferLifecycle<AndroidAppInfo> {

    private final Transfer transfer;
    private final TransferManager transferManager;

    private TransferLifecycleRelay(Transfer transfer, TransferManager transferManager) {
      this.transfer = transfer;
      this.transferManager = transferManager;
    }

    private void notifyListeners() {
      transfer.behaviorRelay.call(transfer);
      transferManager.callRelay();
    }

    @Override public void onStartTransfer(AndroidAppInfo androidAppInfo) {
      transfer.state = State.PENDING;
      notifyListeners();
    }

    @Override public void onFinishTransfer(AndroidAppInfo androidAppInfo) {
      transfer.state = State.RECEIVED;
      notifyListeners();
    }

    @Override public void onProgressChanged(AndroidAppInfo androidAppInfo, float progress) {
      transfer.progress = progress;
      notifyListeners();
    }

    @Override public void onError(IOException e) {
      transfer.state = State.ERROR;
      notifyListeners();
    }
  }

  public boolean isPendingAcception() {
    return state == State.PENDING_ACCEPTION;
  }
}
