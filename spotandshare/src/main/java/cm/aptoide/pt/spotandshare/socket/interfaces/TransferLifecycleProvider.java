package cm.aptoide.pt.spotandshare.socket.interfaces;

/**
 * Created by neuro on 14-07-2017.
 */

public interface TransferLifecycleProvider<T> {

  TransferLifecycle<T> newTransferLifecycle();
}
