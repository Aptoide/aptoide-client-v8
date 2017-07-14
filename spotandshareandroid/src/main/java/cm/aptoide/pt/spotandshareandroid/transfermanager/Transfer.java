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

  public boolean isPendingAcception() {
    return state == State.PENDING_ACCEPTION;
  }
}
