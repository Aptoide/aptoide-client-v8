package cm.aptoide.pt.shareapps.socket.entities;

import java.io.Serializable;
import java.net.Socket;
import lombok.Data;

/**
 * Created by neuro on 27-01-2017.
 */
@Data public class Host implements Serializable {
  private final String ip;
  private final int port;

  public Host(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  public static Host from(Socket socket) {
    return new Host(socket.getInetAddress().getHostAddress(), socket.getPort());
  }

  @Override public int hashCode() {
    int result = ip != null ? ip.hashCode() : 0;
    result = 31 * result + port;
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Host)) return false;

    Host host = (Host) o;

    if (port != host.port) return false;
    return ip != null ? ip.equals(host.ip) : host.ip == null;
  }
}
