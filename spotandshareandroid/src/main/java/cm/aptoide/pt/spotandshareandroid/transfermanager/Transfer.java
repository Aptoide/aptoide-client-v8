package cm.aptoide.pt.spotandshareandroid.transfermanager;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import com.jakewharton.rxrelay.BehaviorRelay;
import lombok.Getter;

/**
 * Created by neuro on 11-07-2017.
 */
public abstract class Transfer<T extends Transfer> {

  protected final BehaviorRelay<T> behaviorRelay;

  @Getter protected State state;
  @Getter protected float progress;
  TransferManager transferManager;

  protected Transfer(State state, TransferManager transferManager) {
    this.state = state;
    this.transferManager = transferManager;

    behaviorRelay = BehaviorRelay.create();
  }

  public abstract AndroidAppInfo getAndroidAppInfo();

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
      transfer.state = State.RECEIVING;
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
