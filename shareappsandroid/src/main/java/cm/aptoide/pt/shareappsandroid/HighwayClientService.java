package cm.aptoide.pt.shareappsandroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StatFs;
import android.support.annotation.Nullable;
import cm.aptoide.pt.shareapps.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.shareapps.socket.entities.Host;
import cm.aptoide.pt.shareapps.socket.interfaces.FileClientLifecycle;
import cm.aptoide.pt.shareapps.socket.interfaces.FileServerLifecycle;
import cm.aptoide.pt.shareapps.socket.message.client.AptoideMessageClientController;
import cm.aptoide.pt.shareapps.socket.message.client.AptoideMessageClientSocket;
import cm.aptoide.pt.shareapps.socket.message.interfaces.StorageCapacity;
import cm.aptoide.pt.shareapps.socket.message.messages.RequestPermissionToSend;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.R.attr.id;

/**
 * Created by filipegoncalves on 10-02-2017.
 */

public class HighwayClientService extends Service {

  private int port;
  private String serverIP;
  private ArrayList<App> listOfApps;
  private NotificationManager mNotifyManager;
  private Object mBuilderSend;
  private Object mBuilderReceive;
  private long lastTimestampReceive;
  private long lastTimestampSend;
  private FileServerLifecycle<AndroidAppInfo> fileServerLifecycle;
  private FileClientLifecycle<AndroidAppInfo> fileClientLifecycle;
  private AptoideMessageClientController aptoideMessageController;
  private AptoideMessageClientSocket aptoideMessageClientSocket;

  @Override public void onCreate() {
    super.onCreate();
    System.out.println("Inside the onCreate of the service");

    if (mNotifyManager == null) {
      mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    fileClientLifecycle = new FileClientLifecycle<AndroidAppInfo>() {
      @Override public void onError(IOException e) {
        e.printStackTrace();
      }

      @Override public void onStartReceiving(AndroidAppInfo androidAppInfo) {
        System.out.println(" Started receiving ");

        //show notification
        createReceiveNotification(androidAppInfo.getAppName());

        Intent i = new Intent();
        i.putExtra("FinishedReceiving",false);
        i.putExtra("appName",androidAppInfo.getAppName());
        i.setAction("RECEIVEAPP");
        sendBroadcast(i);
      }

      @Override public void onFinishReceiving(AndroidAppInfo androidAppInfo) {
        System.out.println(" Finished receiving ");


        finishReceiveNotification(androidAppInfo.getApk().getFilePath());

        Intent i = new Intent();
        i.putExtra("FinishedReceiving",true);
        i.putExtra("needReSend", false);
        i.putExtra("appName",androidAppInfo.getAppName());
        i.putExtra("packageName",androidAppInfo.getPackageName());
        i.putExtra("filePath",androidAppInfo.getApk().getFilePath());
        i.setAction("RECEIVEAPP");
        sendBroadcast(i);
      }

      @Override public void onProgressChanged(float progress) {
        //System.out.println("onProgressChanged() called with: " + "progress = [" + progress + "]");
        int actualProgress=Math.round(progress*100);
        showReceiveProgress("insertAppName",actualProgress);
      }
    };

    fileServerLifecycle = new FileServerLifecycle<AndroidAppInfo>() {
      @Override public void onStartSending(AndroidAppInfo o) {
        System.out.println(" Started sending ");

        Intent i = new Intent();
        i.putExtra("isSent",false);
        i.putExtra("needReSend", false);//add field with pos to resend and change its value only if it is != 100000 (onstartcommand)
        i.putExtra("appName",o.getAppName());
        i.putExtra("packageName",o.getPackageName());
        i.putExtra("positionToReSend",100000);
        i.setAction("SENDAPP");
        sendBroadcast(i);

        //create notification for the app.
        createSendNotification();
      }

      @Override public void onError(IOException e) {
        e.printStackTrace();
      }

      @Override public void onFinishSending(AndroidAppInfo o) {
        System.out.println(" Finished sending ");

        finishSendNotification();//todo get the right filePath

        Intent i = new Intent();
        i.putExtra("isSent",true);
        i.putExtra("needReSend", false);
        i.putExtra("appName",o.getAppName());
        i.putExtra("packageName",o.getPackageName());
        i.putExtra("positionToReSend",100000);
        i.setAction("SENDAPP");
        sendBroadcast(i);

      }

      @Override public void onProgressChanged(float progress) {
        //System.out.println("onProgressChanged() called with: progress = [" + progress + "]");
        if(BuildConfig.DEBUG){
          //showToast("onProgressChanged() called with: progress = [" + progress + "]");
        }
        int actualProgress = Math.round(progress * 100);
        showSendProgress("insertAppName", actualProgress);
      }


    };
  }


  private void createReceiveNotification(String receivingAppName) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      mBuilderReceive = new Notification.Builder(this);
      ((Notification.Builder) mBuilderReceive).setContentTitle(
          this.getResources().getString(R.string.shareApps) + " - " + this.getResources()
              .getString(R.string.receive))
          .setContentText(
              this.getResources().getString(R.string.receiving) + " " + receivingAppName)
          .setSmallIcon(R.mipmap.lite);
    }
  }

  private void finishReceiveNotification(String receivedApkFilePath) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      ((Notification.Builder) mBuilderReceive).setContentText(
          this.getResources().getString(R.string.transfCompleted))
          // Removes the progress bar
          .setSmallIcon(android.R.drawable.stat_sys_download_done)
          .setProgress(0, 0, false)
          .setAutoCancel(true);

      File f = new File(receivedApkFilePath);
      Intent install = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(f),
          "application/vnd.android.package-archive");
      PendingIntent contentIntent = PendingIntent.getActivity(this, 0, install, 0);

      ((Notification.Builder) mBuilderReceive).setContentIntent(contentIntent);
      if (mNotifyManager == null) {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      }
      mNotifyManager.notify(id, ((Notification.Builder) mBuilderReceive).getNotification());
    }
  }

  //  /**
  //   * Method to be called after getting the callback of finishSending
  //   */
  //
  //  public void finishedSending(String appName, String packageName) {
  //    Intent finishedSending = new Intent();
  //    finishedSending.setAction("SENDAPP");
  //    finishedSending.putExtra("isSent", false);
  //    finishedSending.putExtra("needReSend", false);
  //    finishedSending.putExtra("appName", appName);
  //    finishedSending.putExtra("packageName", packageName);
  //    finishedSending.putExtra("positionToReSend", 100000);
  //  }

  private void showReceiveProgress(String receivingAppName, int actual) {

    if (System.currentTimeMillis() - lastTimestampReceive > 1000 / 3) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        ((Notification.Builder) mBuilderReceive).setContentText(
            this.getResources().getString(R.string.receiving) + " " + receivingAppName);

        ((Notification.Builder) mBuilderReceive).setProgress(100, actual, false);
        if (mNotifyManager == null) {
          mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        mNotifyManager.notify(id, ((Notification.Builder) mBuilderReceive).getNotification());
      }
      lastTimestampReceive = System.currentTimeMillis();
    }
  }

  private void createSendNotification() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      mBuilderSend = new Notification.Builder(this);
      ((Notification.Builder) mBuilderSend).setContentTitle(
          this.getResources().getString(R.string.shareApps) + " - " + this.getResources()
              .getString(R.string.send))
          .setContentText(this.getResources().getString(R.string.preparingSend))
          .setSmallIcon(R.mipmap.lite);
    }
  }

  private void showSendProgress(String sendingAppName, int actual) {

    if (System.currentTimeMillis() - lastTimestampSend > 1000 / 3) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        ((Notification.Builder) mBuilderSend).setContentText(
            this.getResources().getString(R.string.sending) + " " + sendingAppName);
        ((Notification.Builder) mBuilderSend).setProgress(100, actual, false);
        if (mNotifyManager == null) {
          mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        mNotifyManager.notify(id, ((Notification.Builder) mBuilderSend).getNotification());
      }
      lastTimestampSend = System.currentTimeMillis();
    }
  }

  private void finishSendNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      ((Notification.Builder) mBuilderSend).setContentText(
          this.getResources().getString(R.string.transfCompleted))
          // Removes the progress bar
          .setSmallIcon(android.R.drawable.stat_sys_download_done)
          .setProgress(0, 0, false)
          .setAutoCancel(true);
      if (mNotifyManager == null) {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      }
      mNotifyManager.notify(id, ((Notification.Builder) mBuilderSend).getNotification());
    }
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {

    if (intent != null) {

      if (intent.getAction() != null && intent.getAction().equals("RECEIVE")) {
        serverIP = intent.getStringExtra("targetIP");
        port = intent.getIntExtra("port", 0);

        final String externalStoragepath=intent.getStringExtra("ExternalStoragePath");

        StorageCapacity storageCapacity = new StorageCapacity() {//todo nao percebo
          @Override public boolean hasCapacity(long bytes) {
            long availableSpace = -1L;
            StatFs stat = new StatFs(externalStoragepath);
            availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
            return availableSpace > bytes;

          }
        };

        aptoideMessageController =
            new AptoideMessageClientController(externalStoragepath, storageCapacity,
                fileServerLifecycle, fileClientLifecycle);
        aptoideMessageClientSocket =
                new AptoideMessageClientSocket(serverIP, port, aptoideMessageController);
        aptoideMessageClientSocket.startAsync();



        System.out.println(" Connected ! ");

      } else if (intent.getAction() != null && intent.getAction().equals("SEND")) {
        Bundle b = intent.getBundleExtra("bundle");

//        if (listOfApps == null || listOfApps.get(listOfApps.size() - 1)
//                .isOnChat()) { //null ou ultimo elemento ja acabado de enviar.
          listOfApps = b.getParcelableArrayList("listOfAppsToInstall");
          for(int i=0;i<listOfApps.size();i++){

            String filePath=listOfApps.get(i).getFilePath();
            String appName= listOfApps.get(i).getAppName();
            String packageName = listOfApps.get(i).getPackageName();
            String obbsFilePath = listOfApps.get(i).getObbsFilePath();

            System.out.println(" Filepath from app 0 (test) is:  "+filePath);
            File apk = new File(filePath);

            AndroidAppInfo appInfo = new AndroidAppInfo(appName, packageName, apk);

            if(!obbsFilePath.equals("noObbs")){


              // TODO: 22-02-2017
              //appInfo.setObbsFilePath(obbsFilePath);

            }

            Host host = aptoideMessageController.getHost();
            aptoideMessageController.send(
                new RequestPermissionToSend(aptoideMessageController.getLocalhost(), appInfo));
          }
      }

      // TODO create new action to disconnect from server
      //aptoideMessageClientController.exit();

    }



        return START_STICKY;
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }
}
