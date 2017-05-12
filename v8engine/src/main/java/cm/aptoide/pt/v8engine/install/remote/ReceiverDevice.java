package cm.aptoide.pt.v8engine.install.remote;

import java.net.InetAddress;

/**
 * Created by franciscoaleixo on 15/08/2016.
 */

public class ReceiverDevice {

  private String deviceName;
  private InetAddress address;
  private int port;

  public ReceiverDevice(String deviceName, InetAddress address, int port) {
    this.deviceName = deviceName;
    this.address = address;
    this.port = port;
  }

  public int getPort() {
    return port;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public boolean isSameDevice(ReceiverDevice device) {
    if (address.getHostAddress()
        .equals(device.getAddress()
            .getHostAddress())) {
      return true;
    }
    return false;
  }

  public InetAddress getAddress() {
    return address;
  }

  public void setAddress(InetAddress address) {
    this.address = address;
  }
}
