package cm.aptoide.pt.shareapppsandroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import cm.aptoide.pt.shareapps.socket.message.client.AptoideMessageClientController;
import cm.aptoide.pt.shareapps.socket.message.client.AptoideMessageClientSocket;
import cm.aptoide.pt.shareapps.socket.message.interfaces.StorageCapacity;
import cm.aptoide.pt.shareapps.socket.message.server.AptoideMessageServerSocket;
import java.io.File;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by filipegoncalves on 10-02-2017.
 */

public class HighwayServerService extends Service {

  private int port;

  private NotificationManager mNotifyManager;
  private Object mBuilderSend;
  private Object mBuilderReceive;
  private long lastTimestampReceive;
  private long lastTimestampSend;

  private List<App> listOfApps;

  @Override public void onCreate() {
    super.onCreate();

    System.out.println(" Inside the service of the server");
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if(intent!=null){
      lastTimestampReceive = System.currentTimeMillis();
      System.out.println("inside of startcommand in the service");
      if (intent.getAction() != null && intent.getAction().equals("RECEIVE")) {
        //port = intent.getIntExtra("port", 0);
        System.out.println("Going to start serving");
        (new AptoideMessageServerSocket(55555, 500000)).startAsync();
        String s =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        StorageCapacity storageCapacity = new StorageCapacity() {
          @Override public boolean hasCapacity(long bytes) {
            return true;
          }
        };

        AptoideMessageClientController aptoideMessageClientController =
                new AptoideMessageClientController(s, storageCapacity, null);
        (new AptoideMessageClientSocket("localhost", 55555, aptoideMessageClientController
        )).startAsync();

        System.out.println("Connected 342");



      } else if (intent.getAction() != null && intent.getAction().equals("SEND")) {
        //read parcelable
        Bundle b = intent.getBundleExtra("bundle");
        if (listOfApps == null || listOfApps.get(listOfApps.size() - 1)
                .isOnChat()) { //null ou ultimo elemento ja acabado de enviar.
          listOfApps = b.getParcelableArrayList("listOfAppsToInstall");
          System.out.println(
                  "serverComm : Just received the list of Apps :  the list of apps size is  :"
                          + listOfApps.size());

          //create the mesage and send it.

        } else {
          List<App> tempList = b.getParcelableArrayList("listOfAppsToInstall");

          listOfApps.addAll(tempList);

          //                Intent addedOneMore = new Intent(HighwayServerComm.this, HighwayTransferRecordActivity.class);
          //                addedOneMore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          //                addedOneMore.putExtra("isHotspot", isHotspot);
          //                addedOneMore.setAction("Addedapps");
          //                startActivity(addedOneMore);

        }
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

  private void showSendProgress(String sendingAppName, int total, int actual) {

    if (System.currentTimeMillis() - lastTimestampSend > 1000 / 3) {
      System.out.println("Inside the timertask of the sendPRogressTask");
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        ((Notification.Builder) mBuilderSend).setContentText(
            this.getResources().getString(R.string.sending) + " " + sendingAppName);
        ((Notification.Builder) mBuilderSend).setProgress(total, actual, false);
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
      mNotifyManager.notify(id, ((Notification.Builder) mBuilderSend).getNotification());
    }
  }

  private void createReceiveNotification(String receivingAppName, String receivingApkFilePath) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      mBuilderReceive = new Notification.Builder(this);
      ((Notification.Builder) mBuilderReceive).setContentTitle(
          this.getResources().getString(R.string.shareApps) + " - " + this.getResources()
              .getString(R.string.receive))
          .setContentText(
              this.getResources().getString(R.string.receiving) + " " + receivingAppName)
          .setSmallIcon(R.mipmap.lite);

      File f = new File(receivingApkFilePath);
      Intent install = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(f),
          "application/vnd.android.package-archive");
      PendingIntent contentIntent = PendingIntent.getActivity(this, 0, install, 0);

      ((Notification.Builder) mBuilderReceive).setContentIntent(contentIntent);
    }
  }

  private void showReceiveProgress(String receivingAppName, int total, int actual) {

    if (System.currentTimeMillis() - lastTimestampReceive > 1000 / 3) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        ((Notification.Builder) mBuilderReceive).setContentText(
            this.getResources().getString(R.string.receiving) + " " + receivingAppName);

        ((Notification.Builder) mBuilderReceive).setProgress(total, actual, false);
        mNotifyManager.notify(id, ((Notification.Builder) mBuilderReceive).getNotification());
      }
      lastTimestampReceive = System.currentTimeMillis();
    }
  }

  private void finishReceiveNotification() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      ((Notification.Builder) mBuilderReceive).setContentText(
          this.getResources().getString(R.string.transfCompleted))
          // Removes the progress bar
          .setSmallIcon(android.R.drawable.stat_sys_download_done)
          .setProgress(0, 0, false)
          .setAutoCancel(true);
      mNotifyManager.notify(id, ((Notification.Builder) mBuilderReceive).getNotification());
    }
  }
}
