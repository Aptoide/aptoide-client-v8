package cm.aptoide.pt.v8engine.spotandshare;

import android.os.Build;
import cm.aptoide.pt.spotandshare.socket.interfaces.SocketBinder;
import java.io.IOException;

/**
 * Created by jandrade on 21-08-2015.
 */
public class Utils {

  public static class Socket {

    public static SocketBinder newDefaultSocketBinder() {
      return new SocketBinder() {
        @Override public void bind(java.net.Socket socket) {
          if (DataHolder.getInstance().network != null) {
            try {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DataHolder.getInstance().network.bindSocket(socket);
              }
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      };
    }
  }
}
