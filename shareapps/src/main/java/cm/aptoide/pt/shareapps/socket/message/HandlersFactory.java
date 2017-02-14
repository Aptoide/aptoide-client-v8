package cm.aptoide.pt.shareapps.socket.message;

import cm.aptoide.pt.shareapps.socket.entities.FileInfo;
import cm.aptoide.pt.shareapps.socket.entities.Host;
import cm.aptoide.pt.shareapps.socket.file.AptoideFileClientSocket;
import cm.aptoide.pt.shareapps.socket.file.AptoideFileServerSocket;
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
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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
      StorageCapacity storageCapacity) {
    List<MessageHandler<? extends Message>> messageHandlers = new LinkedList<>();

    messageHandlers.add(new SendApkHandler());
    messageHandlers.add(new ReceiveApkHandler(rootDir, storageCapacity));

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

    public SendApkHandler() {
      super(SendApk.class);
    }

    @Override public void handleMessage(SendApk sendApkMessage, Sender<Message> messageSender) {
      List<String> filesList = sendApkMessage.getAndroidAppInfo().getFilesPathsList();
      new AptoideFileServerSocket(sendApkMessage.getServerPort(), filesList).startAsync();
      messageSender.send(new AckMessage(messageSender.getHost()));
      // TODO: 03-02-2017 neuro maybe a good ideia to stop the server somewhat :)
    }
  }

  private static class ReceiveApkHandler extends MessageHandler<ReceiveApk> {

    static AtomicInteger dir = new AtomicInteger('a');
    private final String root;
    private final StorageCapacity storageCapacity;

    public ReceiveApkHandler(String root, StorageCapacity storageCapacity) {
      super(ReceiveApk.class);
      this.root = root;
      this.storageCapacity = storageCapacity;
    }

    @Override public void handleMessage(ReceiveApk receiveApk, Sender<Message> messageSender) {
      AckMessage ackMessage = new AckMessage(messageSender.getHost());
      if (storageCapacity.hasCapacity(receiveApk.getAndroidAppInfo().getFilesSize())) {
        ackMessage.setSuccess(true);

        messageSender.send(ackMessage);
        Host host = receiveApk.getHost();

        changeFilesRootDir(receiveApk.getAndroidAppInfo().getFiles());

        // TODO: 03-02-2017 neuro apagar
        temp(receiveApk.getAndroidAppInfo().getFiles());

        new AptoideFileClientSocket(host.getIp(), host.getPort(),
            receiveApk.getAndroidAppInfo().getFiles()).start();
      } else {
        messageSender.send(ackMessage);
      }
    }

    private void temp(List<FileInfo> files) {
      Random random = new Random();
      char r = (char) random.nextInt(255);
      String parentDirectory = "/tmp/a/" + dir.incrementAndGet();
      new File(parentDirectory).mkdirs();
      for (FileInfo file : files) {
        file.setParentDirectory(parentDirectory);
      }
    }

    private void changeFilesRootDir(List<FileInfo> fileInfos) {
      for (FileInfo fileInfo : fileInfos) {
        fileInfo.setParentDirectory(root);
      }
    }
  }

  private static class ExitMessageHandler extends MessageHandler<ExitMessage> {

    private final AptoideMessageServerSocket aptoideMessageServerSocket;

    public ExitMessageHandler(AptoideMessageServerSocket aptoideMessageServerSocket) {
      super(ExitMessage.class);
      this.aptoideMessageServerSocket = aptoideMessageServerSocket;
    }

    @Override public void handleMessage(ExitMessage message, Sender<Message> messageSender) {
      aptoideMessageServerSocket.removeHost(message.getHost());
    }
  }
}
