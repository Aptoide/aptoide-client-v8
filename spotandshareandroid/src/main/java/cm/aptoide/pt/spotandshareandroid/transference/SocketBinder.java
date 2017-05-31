package cm.aptoide.pt.spotandshareandroid.transference;

import android.net.Network;
import android.os.Build;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by filipe on 30-05-2017.
 */

public class SocketBinder {

  private Network network;

  public SocketBinder(Network network) {
    this.network = network;
  }

  public void bind(Socket socket) {
    if (network != null) {
      try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          network.bindSocket(socket);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
