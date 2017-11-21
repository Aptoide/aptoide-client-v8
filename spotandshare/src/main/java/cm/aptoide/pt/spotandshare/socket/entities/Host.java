package cm.aptoide.pt.spotandshare.socket.entities;

import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by neuro on 27-01-2017.
 */
public class Host implements Serializable {
  private String ip;
  private int port;

  public Host(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  public static Host from(Socket socket) {
    return new Host(socket.getInetAddress()
        .getHostAddress(), socket.getPort());
  }

  public static Host from(ServerSocket serverSocket) {
    return new Host(serverSocket.getInetAddress()
        .getHostAddress(), serverSocket.getLocalPort());
  }

  public static Host fromLocalhost(Socket socket) {
    return new Host(socket.getLocalAddress()
        .getHostAddress(), socket.getLocalPort());
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

  public String toString() {
    return "Host(ip=" + this.getIp() + ", port=" + this.getPort() + ")";
  }

  public String getIp() {
    return this.ip;
  }

  public Host setIp(String ip) {
    this.ip = ip;
    return this;
  }

  public int getPort() {
    return this.port;
  }

  public Host setPort(int port) {
    this.port = port;
    return this;
  }
}
