package cm.aptoide.pt.spotandshare.socket.message;

import cm.aptoide.pt.spotandshare.socket.entities.Friend;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by neuro on 13-07-2017.
 */

public class FriendsManager {

  private final Map<Host, Friend> map;

  public FriendsManager() {
    map = new HashMap<>();
  }

  public void addFriend(Friend friend, Host host) {
    map.put(host, friend);
  }

  public void removeFriend(Host host) {

    if (!map.containsKey(host)) {
      throw new IllegalArgumentException("Host not among friends!");
    }

    map.remove(host);
  }
}
