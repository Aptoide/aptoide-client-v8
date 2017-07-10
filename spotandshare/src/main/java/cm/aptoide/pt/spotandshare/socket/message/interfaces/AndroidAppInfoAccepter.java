package cm.aptoide.pt.spotandshare.socket.message.interfaces;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import rx.functions.Action1;

/**
 * Created by neuro on 10-07-2017.
 */

public interface AndroidAppInfoAccepter extends Action1<Accepter<AndroidAppInfo>> {
}
