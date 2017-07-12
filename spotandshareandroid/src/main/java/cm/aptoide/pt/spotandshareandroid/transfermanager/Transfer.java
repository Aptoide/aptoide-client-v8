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

  public Transfer(Accepter<AndroidAppInfo> androidAppInfoAccepter, State state) {
    this.androidAppInfoAccepter = androidAppInfoAccepter;
    this.state = state;

    behaviorRelay = BehaviorRelay.create();
  }

  public Transfer(Accepter<AndroidAppInfo> androidAppInfoAccepter) {
    this(androidAppInfoAccepter, State.PENDING_ACCEPTION);
  }

  public Observable<Transfer> accept() {
    if (state != State.PENDING_ACCEPTION) {
      throw new IllegalStateException("Transfer not pending acception!");
    }

    state = State.RECEIVING;
    androidAppInfoAccepter.accept(new TransferLifecycleRelay(this));
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

    private TransferLifecycleRelay(Transfer transfer) {
      this.transfer = transfer;
    }

    @Override public void onStartTransfer(AndroidAppInfo androidAppInfo) {
      transfer.state = State.PENDING;
      transfer.behaviorRelay.call(transfer);
    }

    @Override public void onFinishTransfer(AndroidAppInfo androidAppInfo) {
      transfer.state = State.RECEIVED;
      transfer.behaviorRelay.call(transfer);
    }

    @Override public void onProgressChanged(AndroidAppInfo androidAppInfo, float progress) {
      transfer.progress = progress;
      transfer.behaviorRelay.call(transfer);
    }

    @Override public void onError(IOException e) {
      transfer.state = State.ERROR;
      transfer.behaviorRelay.call(transfer);
    }
  }

  public boolean isPendingAcception() {
    return state == State.PENDING_ACCEPTION;
  }
}
