package cm.aptoide.pt.spotandshareandroid.hotspotmanager.receiver.networkstate;

/**
 * Created by neuro on 07-07-2017.
 */
public class NetworkState {

  private final State state;
  private final String ssid;

  public NetworkState(State state, String ssid) {
    this.state = state;
    this.ssid = ssid;
  }

  public State getState() {
    return this.state;
  }

  public String getSsid() {
    return this.ssid;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof NetworkState)) return false;
    final NetworkState other = (NetworkState) o;
    if (!other.canEqual(this)) return false;
    final Object this$state = this.getState();
    final Object other$state = other.getState();
    if (this$state == null ? other$state != null : !this$state.equals(other$state)) return false;
    final Object this$ssid = this.getSsid();
    final Object other$ssid = other.getSsid();
    return this$ssid == null ? other$ssid == null : this$ssid.equals(other$ssid);
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $state = this.getState();
    result = result * PRIME + ($state == null ? 43 : $state.hashCode());
    final Object $ssid = this.getSsid();
    result = result * PRIME + ($ssid == null ? 43 : $ssid.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof NetworkState;
  }

  public String toString() {
    return "NetworkState(state=" + this.getState() + ", ssid=" + this.getSsid() + ")";
  }

  public enum State {
    CONNECTED, DISCONNECTED
  }
}
