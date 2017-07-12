package cm.aptoide.pt.spotandshareandroid.transfermanager;

import com.jakewharton.rxrelay.BehaviorRelay;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 10-07-2017.
 */
class TransferListRelay {

  private final BehaviorRelay<List<Transfer>> transferListRelay;
  private final List<Transfer> transfers;

  TransferListRelay() {
    transferListRelay = BehaviorRelay.create();
    transfers = new LinkedList<>();
  }

  public void add(Transfer transfer) {
    transfers.add(transfer);
    callRelay();
  }

  void callRelay() {
    transferListRelay.call(transfers);
  }

  BehaviorRelay<List<Transfer>> asObservable() {
    return transferListRelay;
  }
}
