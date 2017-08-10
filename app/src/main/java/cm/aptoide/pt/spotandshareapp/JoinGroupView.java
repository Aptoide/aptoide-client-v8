package cm.aptoide.pt.spotandshareapp;

import cm.aptoide.pt.spotandshareandroid.SpotAndShareSender;
import rx.functions.Action1;

/**
 * Created by filipe on 18-07-2017.
 */

public interface JoinGroupView {

  void joinGroup();

  void registerJoinGroupSuccessCallback(Action1<SpotAndShareSender> onSuccess);

  void unregisterJoinGroupSuccessCallback();
}
