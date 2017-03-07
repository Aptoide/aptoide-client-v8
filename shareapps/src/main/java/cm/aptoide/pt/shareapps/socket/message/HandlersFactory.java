package cm.aptoide.pt.shareapps.socket.message;

import cm.aptoide.pt.shareapps.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.shareapps.socket.entities.FileInfo;
import cm.aptoide.pt.shareapps.socket.entities.Host;
import cm.aptoide.pt.shareapps.socket.file.ShareAppsFileClientSocket;
import cm.aptoide.pt.shareapps.socket.file.ShareAppsFileServerSocket;
import cm.aptoide.pt.shareapps.socket.interfaces.FileClientLifecycle;
import cm.aptoide.pt.shareapps.socket.interfaces.FileServerLifecycle;
import cm.aptoide.pt.shareapps.socket.message.interfaces.Sender;
import cm.aptoide.pt.shareapps.socket.message.interfaces.StorageCapacity;
import cm.aptoide.pt.shareapps.socket.message.messages.AckMessage;
import cm.aptoide.pt.shareapps.socket.message.messages.AndroidAppInfoMessage;
import cm.aptoide.pt.shareapps.socket.message.messages.ExitMessage;
import cm.aptoide.pt.shareapps.socket.message.messages.ReceiveApk;
import cm.aptoide.pt.shareapps.socket.message.messages.RequestPermissionToSend;
import cm.aptoide.pt.shareapps.socket.message.messages.SendApk;
import cm.aptoide.pt.shareapps.socket.message.server.AptoideMessageServerSocket;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 02-02-2017.
 */

public class HandlersFactory {

  @Deprecated public static List<MessageHandler<? extends Message>> newDefaultServerHandlersList(
      AptoideMessageServerSocket aptoideMessageServerSocket) {
    List<MessageHandler<? extends Message>> messageHandlers = new LinkedList<>();

    MessageHandler<AndroidAppInfoMessage> messageHandler =
        new MessageHandler<AndroidAppInfoMessage>(AndroidAppInfoMessage.class) {
          @Override
          public void handleMessage(AndroidAppInfoMessage message, Sender<Message> messageSender) {
          }
        };

    messageHandlers.add(messageHandler);
    messageHandlers.add(new RequestPermissionToSendHandler(aptoideMessageServerSocket));
    messageHandlers.add(new ExitMessageHandler(aptoideMessageServerSocket));

    return messageHandlers;
  }

  public static List<MessageHandler<? extends Message>> newDefaultClientHandlersList(String rootDir,
      StorageCapacity storageCapacity, FileServerLifecycle<AndroidAppInfo> serverLifecycle,
      FileClientLifecycle<AndroidAppInfo> fileClientLifecycle) {
    List<MessageHandler<? extends Message>> messageHandlers = new LinkedList<>();

    messageHandlers.add(new SendApkHandler(serverLifecycle));
    messageHandlers.add(new ReceiveApkHandler(rootDir, storageCapacity, fileClientLifecycle));

    return messageHandlers;
  }

  private static class RequestPermissionToSendHandler
      extends MessageHandler<RequestPermissionToSend> {

    private final AptoideMessageServerSocket messageServerSocket;

    public RequestPermissionToSendHandler(AptoideMessageServerSocket messageServerSocket) {
      super(RequestPermissionToSend.class);
      this.messageServerSocket = messageServerSocket;
    }

    @Override public void handleMessage(RequestPermissionToSend requestPermissionToSend,
        Sender<Message> messageSender) {
      messageServerSocket.requestPermissionToSendApk(requestPermissionToSend);
    }
  }

  private static class SendApkHandler extends MessageHandler<SendApk> {

    private final FileServerLifecycle<AndroidAppInfo> fileServerLifecycle;

    public SendApkHandler(FileServerLifecycle<AndroidAppInfo> fileServerLifecycle) {
      super(SendApk.class);
      this.fileServerLifecycle = fileServerLifecycle;
    }

    @Override public void handleMessage(SendApk sendApkMessage, Sender<Message> messageSender) {
      ShareAppsFileServerSocket shareAppsFileServerSocket =
          new ShareAppsFileServerSocket(sendApkMessage.getServerPort(),
              sendApkMessage.getAndroidAppInfo(), 5000);
      shareAppsFileServerSocket.startAsync();
      shareAppsFileServerSocket.setFileServerLifecycle(sendApkMessage.getAndroidAppInfo(),
          fileServerLifecycle);
      messageSender.send(new AckMessage(messageSender.getHost()));
      // TODO: 03-02-2017 neuro maybe a good ideia to stop the server somewhat :)
    }
  }

  private static class ReceiveApkHandler extends MessageHandler<ReceiveApk> {

    private final String root;
    private final StorageCapacity storageCapacity;
    private final FileClientLifecycle<AndroidAppInfo> fileClientLifecycle;

    public ReceiveApkHandler(String root, StorageCapacity storageCapacity,
        FileClientLifecycle<AndroidAppInfo> fileClientLifecycle) {
      super(ReceiveApk.class);
      this.root = root;
      this.storageCapacity = storageCapacity;
      this.fileClientLifecycle = fileClientLifecycle;
    }

    @Override public void handleMessage(ReceiveApk receiveApk, Sender<Message> messageSender) {
      AckMessage ackMessage = new AckMessage(messageSender.getHost());
      AndroidAppInfo androidAppInfo = receiveApk.getAndroidAppInfo();
      if (storageCapacity.hasCapacity(androidAppInfo.getFilesSize())) {
        ackMessage.setSuccess(true);

        messageSender.send(ackMessage);
        Host receiveApkServerHost = receiveApk.getServerHost();

        String generatedRoot = changeFilesRootDir(androidAppInfo);
        boolean mkdirs = new File(generatedRoot).mkdirs();

        ShareAppsFileClientSocket shareAppsFileClientSocket =
            new ShareAppsFileClientSocket(receiveApkServerHost.getIp(),
                receiveApkServerHost.getPort(), androidAppInfo.getFiles());
        if (fileClientLifecycle != null) {
          shareAppsFileClientSocket.setFileClientLifecycle(androidAppInfo, fileClientLifecycle);
        }
        shareAppsFileClientSocket.startAsync();
      } else {
        messageSender.send(ackMessage);
      }
    }

    private String changeFilesRootDir(AndroidAppInfo androidAppInfo) {
      String packageName = androidAppInfo.getPackageName();
      String rootToFiles = root + File.separatorChar + packageName;

      for (FileInfo fileInfo : androidAppInfo.getFiles()) {
        fileInfo.setParentDirectory(rootToFiles);
      }

      return rootToFiles;
    }
  }

  private static class ExitMessageHandler extends MessageHandler<ExitMessage> {

    private final AptoideMessageServerSocket aptoideMessageServerSocket;

    public ExitMessageHandler(AptoideMessageServerSocket aptoideMessageServerSocket) {
      super(ExitMessage.class);
      this.aptoideMessageServerSocket = aptoideMessageServerSocket;
    }

    @Override public void handleMessage(ExitMessage message, Sender<Message> messageSender) {
      aptoideMessageServerSocket.removeHost(message.getLocalHost());
    }
  }
}
