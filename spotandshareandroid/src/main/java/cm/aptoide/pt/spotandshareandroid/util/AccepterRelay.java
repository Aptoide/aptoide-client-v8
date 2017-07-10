package cm.aptoide.pt.spotandshareandroid.util;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.Accepter;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.AndroidAppInfoAccepter;
import com.jakewharton.rxrelay.BehaviorRelay;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 10-07-2017.
 */
public class AccepterRelay {

  private final BehaviorRelay<List<Accepter<AndroidAppInfo>>> accepterBehaviorRelay;
  private final List<Accepter<AndroidAppInfo>> accepters;

  public AccepterRelay() {
    accepterBehaviorRelay = BehaviorRelay.create();
    accepters = new LinkedList<>();
  }

  private void add(Accepter<AndroidAppInfo> androidAppInfoAccepter) {
    accepters.add(androidAppInfoAccepter);
    accepterBehaviorRelay.call(accepters);
  }

  public BehaviorRelay<List<Accepter<AndroidAppInfo>>> asObservable() {
    return accepterBehaviorRelay;
  }

  public AndroidAppInfoAccepter getAccepter() {
    return this::add;
  }
}
