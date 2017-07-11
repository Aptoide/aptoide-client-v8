package cm.aptoide.pt.spotandshare.socket.message.interfaces;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.interfaces.TransferLifecycle;

/**
 * Created by neuro on 10-07-2017.
 */

public interface Accepter<T> {

  T getMeta();

  void accept(TransferLifecycle<AndroidAppInfo> fileClientLifecycle);
}
