package cm.aptoide.pt.shareapps.socket.file;

import cm.aptoide.pt.shareapps.socket.AptoideServerSocket;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by neuro on 27-01-2017.
 */
public class AptoideFileServerSocket extends AptoideServerSocket {

  private final List<String> filePaths;

  public AptoideFileServerSocket(int port, List<String> filePaths) {
    super(port);
    this.filePaths = filePaths;
  }

  public AptoideFileServerSocket(int bufferSize, int port, List<String> filePaths) {
    super(bufferSize, port);
    this.filePaths = filePaths;
  }

  @Override protected void onNewClient(Socket socket) {
    InputStream in = null;

    try {
      for (String filePath : filePaths) {
        System.out.println(Thread.currentThread().getId() + ": Started sending " + filePath);
        in = new FileInputStream(filePath);
        copy(in, socket.getOutputStream());
        System.out.println(Thread.currentThread().getId() + ": Finished sending " + filePath);
        in.close();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
