package cm.aptoide.pt.spotandshare.socket.message;

import cm.aptoide.pt.spotandshare.socket.entities.Friend;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import com.jakewharton.rxrelay.BehaviorRelay;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;

/**
 * Created by neuro on 13-07-2017.
 */

public class FriendsManager {

  private final BehaviorRelay<Collection<Friend>> behaviorRelay;
  private final Map<Host, Friend> map;

  public FriendsManager() {
    this.map = new HashMap<>();
    this.behaviorRelay = BehaviorRelay.create();
  }

  public void addFriend(Friend friend, Host host) {
    map.put(host, friend);
    behaviorRelay.call(Collections.unmodifiableCollection(map.values()));
  }

  public void removeFriend(Host host) {

    if (!map.containsKey(host)) {
      throw new IllegalArgumentException("Host not among friends!");
    }

    map.remove(host);
  }

  public Observable<Collection<Friend>> observe() {
    return behaviorRelay;
  }
}
