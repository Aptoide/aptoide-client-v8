package cm.aptoide.pt.spotandshareandroid.hotspotmanager.receiver.networkstate;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by neuro on 07-07-2017.
 */
@ToString @EqualsAndHashCode public class NetworkState {

  private final State state;
  private final String ssid;

  public NetworkState(State state, String ssid) {
    this.state = state;
    this.ssid = ssid;
  }

  public enum State {
    CONNECTED, DISCONNECTED
  }
}
