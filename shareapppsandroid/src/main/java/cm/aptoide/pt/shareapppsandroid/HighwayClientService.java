package cm.aptoide.pt.shareapppsandroid;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import cm.aptoide.pt.shareapps.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.shareapps.socket.entities.Host;
import cm.aptoide.pt.shareapps.socket.interfaces.FileServerLifecycle;
import cm.aptoide.pt.shareapps.socket.message.client.AptoideMessageClientController;
import cm.aptoide.pt.shareapps.socket.message.client.AptoideMessageClientSocket;
import cm.aptoide.pt.shareapps.socket.message.interfaces.StorageCapacity;
import cm.aptoide.pt.shareapps.socket.message.messages.RequestPermissionToSend;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 10-02-2017.
 */

public class HighwayClientService extends Service {

  private int port;
  private String serverIP;
  private ArrayList<App> listOfApps;
  private FileServerLifecycle fileServerLifecycle;
  private AptoideMessageClientController aptoideMessageController;
  private AptoideMessageClientSocket aptoideMessageClientSocket;

  @Override public void onCreate() {
    super.onCreate();
    System.out.println("Inside the onCreate of the service");

    fileServerLifecycle = new FileServerLifecycle() {
      @Override public void onStartSending(Object o) {
        System.out.println(" Started sending ");
        //generate intent, with info of the app. Name, filePath, size,, etc.
        //intent i=new Intent();
        //i.setAction("SENDAPP");
        //sendBroadcast(i);
      }

      @Override public void onFinishSending(Object o) {
        System.out.println(" Finished sending ");

      }
    };

  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {

    if (intent.getAction() != null && intent.getAction().equals("RECEIVE")) {
      serverIP = intent.getStringExtra("targetIP");
      port = intent.getIntExtra("port", 0);
      // TODO: 16-02-2017

      String externalStoragepath=intent.getStringExtra("ExternalStoragePath");
      long space=intent.getIntExtra("storage",0);

      StorageCapacity storageCapacity = new StorageCapacity() {//todo nao percebo
        @Override public boolean hasCapacity(long bytes) {
          return true;
        }
      };

      aptoideMessageController =
          new AptoideMessageClientController(externalStoragepath, storageCapacity, fileServerLifecycle);
      aptoideMessageClientSocket =
          new AptoideMessageClientSocket(serverIP, port, aptoideMessageController);
      aptoideMessageClientSocket.startAsync();



      System.out.println(" Connected ! ");

    } else if (intent.getAction() != null && intent.getAction().equals("SEND")) {
      Bundle b = intent.getBundleExtra("bundle");

      if (listOfApps == null || listOfApps.get(listOfApps.size() - 1)
          .isOnChat()) { //null ou ultimo elemento ja acabado de enviar.
        listOfApps = b.getParcelableArrayList("listOfAppsToInstall");
        String filePath=listOfApps.get(0).getFilePath();
        System.out.println(" Filepath from app 0 (test) is:  "+filePath);
        File apk = new File(filePath);
        AndroidAppInfo appInfo=new AndroidAppInfo(apk);

        Host host = aptoideMessageController.getHost();
        aptoideMessageController.send(
            new RequestPermissionToSend(aptoideMessageController.getMe(),appInfo));

      }else {
        List<App> tempList = b.getParcelableArrayList("listOfAppsToInstall");

        listOfApps.addAll(tempList);
      }
    }


        return START_STICKY;
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  /**
   * Method to be called after getting the callback of finishSending
   */

  public void finishedSending(String appName, String packageName) {
    Intent finishedSending = new Intent();
    finishedSending.setAction("SENDAPP");
    finishedSending.putExtra("isSent", false);
    finishedSending.putExtra("needReSend", false);
    finishedSending.putExtra("appName", appName);
    finishedSending.putExtra("packageName", packageName);
    finishedSending.putExtra("positionToReSend", 100000);
  }

  //    private void createSendNotification(){
  //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
  //            mBuilderSend =new Notification.Builder(this);
  //            ((Notification.Builder) mBuilderSend).setContentTitle(this.getResources().getString(R.string.shareApps) +" - "+ this.getResources().getString(R.string.send))
  //                    .setContentText( this.getResources().getString(R.string.preparingSend) )
  //                    .setSmallIcon(R.mipmap.lite);
  //        }
  //
  //
  //    }
  //
  //    @TargetApi(14)
  //    private void showSendProgress(){
  //        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){
  //            sendProgressTask = new TimerTask() {
  //                @Override
  //                public void run() {
  //                    int fileSize=(int)totalSizeSend;
  ////                int actual= (totalToSend*100)/fileSize;
  //                    int actual= totalToSend;
  ////                ((Notification.Builder) mBuilderSend).setCOntentTitle();
  //                    ((Notification.Builder) mBuilderSend).setContentText( HighwayClientComm.this.getResources().getString(R.string.sending) + " " + appName);
  //                    ((Notification.Builder) mBuilderSend).setProgress(fileSize, actual, false);
  //                    mNotifyManager.notify(id, ((Notification.Builder) mBuilderSend).getNotification());
  //                }
  //            };
  //            sendTimer.scheduleAtFixedRate(sendProgressTask,0,500);//delay, interval
  //        }
  //
  //    }
  //
  //    private void finishSendNotification(){
  //        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){
  //            ((Notification.Builder) mBuilderSend)
  //                    .setContentText( this.getResources().getString(R.string.transfCompleted) )
  //                    // Removes the progress bar
  //                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
  //                    .setProgress(0, 0, false)
  //                    .setAutoCancel(true);
  //            mNotifyManager.notify(id, ((Notification.Builder) mBuilderSend).getNotification());
  //            sendProgressTask.cancel();
  //        }
  //    }
  //
  //    private void createReceiveNotification(){
  //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
  //            mBuilderReceive=new Notification.Builder(this);
  //            ((Notification.Builder) mBuilderReceive).setContentTitle(this.getResources().getString(R.string.shareApps)+ " - "+ this.getResources().getString(R.string.receive))
  //                    .setContentText( this.getResources().getString(R.string.receiving) + " " + actualNameToReceive)
  //                    .setSmallIcon(R.mipmap.lite);
  //            //falta o intent para an action a tomar.
  //
  //            File f=new File(receivedAPKFilepath);
  //            Intent install = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(f),"application/vnd.android.package-archive");
  //            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, install, 0);
  //
  //            ((Notification.Builder) mBuilderReceive).setContentIntent(contentIntent);
  //        }
  //
  //    }
  //
  //    private void showReceiveProgress(){
  //        receiveProgressTask= new TimerTask() {
  //            @Override
  //            public void run() {
  //                int totalFileObbsSize=(int) totalSizeReceive;
  //                int actual= totalReceived;
  //
  //                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
  //                    ((Notification.Builder) mBuilderReceive).setContentText(HighwayClientComm.this.getResources().getString(R.string.receiving) + " " + actualNameToReceive);
  //                    ((Notification.Builder) mBuilderReceive).setProgress(totalFileObbsSize, actual, false);
  //                    mNotifyManager.notify(id, ((Notification.Builder) mBuilderReceive).getNotification());
  //                }
  //
  //            }
  //        };
  //
  //        receiveTimer.scheduleAtFixedRate(receiveProgressTask,0,500);//delay, interval
  //    }
  //
  //    private void finishReceiveNotification(){
  //        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){
  //            ((Notification.Builder) mBuilderReceive)
  //                    .setContentText(this.getResources().getString(R.string.transfCompleted))
  //                    // Removes the progress bar
  //                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
  //                    .setProgress(0, 0, false)
  //                    .setAutoCancel(true);
  //            mNotifyManager.notify(id, ((Notification.Builder) mBuilderReceive).getNotification());
  //        }
  //        receiveProgressTask.cancel();
  //    }
}
