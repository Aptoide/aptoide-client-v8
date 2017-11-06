package cm.aptoide.pt.spotandshare.socket.example;

import cm.aptoide.pt.spotandshare.socket.AptoideServerSocket;
import cm.aptoide.pt.spotandshare.socket.entities.FileInfo;
import cm.aptoide.pt.spotandshare.socket.file.AptoideFileClientSocket;
import cm.aptoide.pt.spotandshare.socket.file.AptoideFileServerSocket;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ExampleMultiFiles {

  public static final int PORT = 14321;
  public static final String FILE_PATH_1 = "/tmp/a/un.official.adsterra_1.0.apk";
  public static final String FILE_PATH_2 = "/tmp/a/NVIDIA-Linux-x86_64-367.44.run";
  public static final List<String> FILE_PATHS = Arrays.asList(FILE_PATH_1, FILE_PATH_2);
  //public static final List<String> FILE_PATHS = Arrays.asList(FILE_PATH_1);
  private static int THREADS = 1;
  private static AptoideServerSocket aptoideServerSocket;

  public static void main(String[] args) {
    startServer();
    for (int i = 0; i < THREADS; i++) {
      startClient();
    }

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    aptoideServerSocket.shutdown();
    aptoideServerSocket.shutdownExecutorService();
  }

  private static void startServer() {
    aptoideServerSocket = new AptoideFileServerSocket(PORT, FILE_PATHS, 5000, 3000);
    aptoideServerSocket.startAsync();
  }

  private static void startClient() {
    new AptoideFileClientSocket("localhost", PORT, getFilesToReceive(),
        Integer.MAX_VALUE).startAsync();
  }

  private static List<FileInfo> getFilesToReceive() {
    String destDir = "/tmp/a";
    LinkedList<FileInfo> fileInfos = new LinkedList<>();

    FileInfo fileInfo1 = new FileInfo(new File(FILE_PATH_1)).setParentDirectory(destDir);
    FileInfo fileInfo2 = new FileInfo(new File(FILE_PATH_2)).setParentDirectory(destDir);

    fileInfos.add(new FileInfo(fileInfo1.getFilePath() + "socket", fileInfo1.getSize()));
    fileInfos.add(new FileInfo(fileInfo2.getFilePath() + "socket", fileInfo2.getSize()));

    return fileInfos;
  }
}
