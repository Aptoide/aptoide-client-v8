package cm.aptoide.pt.shareappsandroid;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.widget.Toast;
import cm.aptoide.lite.localytics.AnalyticsLite;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by filipegoncalves on 08-08-2016.
 */
public class HighwayClientComm extends Service {
  private static final int TIME_OUT = 7000;
  public static int time = 1000;
  NotificationManager mNotifyManager;
  private Socket clientSocketToReceive;
  private ObjectInputStream inText;
  private ObjectOutputStream outText;
  private Socket clientSocketToSend;
  private ObjectInputStream inApk;
  private ObjectOutputStream outApk;
  private String appName;
  private String packageName;
  private String serverIP;
  private int porto;
  private String filePath;
  private File apkToSend;
  private List<App> listOfApps;
  //    private List<App> listOfAppsToSend;
  private String nickname;
  private String isHotspot;
  private String receivedAPKFilepath;
  private String actualNameToReceive;
  private int id = 1;
  private Object mBuilderSend = null;
  private Object mBuilderReceive = null;
  private Timer sendTimer = new Timer();
  private Timer receiveTimer = new Timer();
  private long apkToSendSize;
  private long fileSizeToReceive;
  private int totalToSend;
  private int totalReceived;
  private String disconnectMessage;
  private TimerTask receiveProgressTask;
  private TimerTask sendProgressTask;
  private boolean isRunning = false;
  private String sendersFilePath;
  //senders filepath so that i can answer him and he knows what app he has to install.
  private Handler clientH;
  private Context context;
  private boolean handshakeDone = false;
  private boolean existsServer = false;//to the checkHello
  private int positionToResend;
  private Timer retryConnection;
  private Timer checkHello;
  private Timer checkIfClientReceivedApk;
  private TimerTask retryConnectionTask;
  private boolean isOutsideShare = false;//true if service was started with a share from outside.
  private long totalSizeReceive;
  private long totalSizeSend;

  @Override public void onCreate() {
    super.onCreate();
    context = getApplicationContext();
    time = 1000;
    System.out.println("I am here inside the oncreate of the clientCommm");
  }

  /** The service is starting, due to a call to startService() */
  @Override public int onStartCommand(Intent intent, int flags, int startId) {

    if (intent != null) {
      time = 1000;
      if (mNotifyManager == null) {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      }
      if (clientH == null) {
        clientH = new Handler(Looper.getMainLooper());
      }
      serverIP = intent.getStringExtra("targetIP");
      porto = intent.getIntExtra("port", 0);
      filePath = intent.getStringExtra("filePath");
      appName = intent.getStringExtra("appName");
      //            nickname=intent.getStringExtra("nickname");//removed due to concurrency issues
      isHotspot = intent.getStringExtra("isHotspot");

      positionToResend =
          intent.getIntExtra("positionToReSend", 100000);//this default value will never exist
      System.out.println("The POSITION TO RESEND IS AT : " + positionToResend);
      DataHolder.getInstance().setHotspot(false);
      DataHolder.getInstance()
          .setServiceRunning(
              true);//not too safe.According to the actual implementation, it still works..
      System.out.println(
          "Highway client comm - got the ip: " + serverIP + " and the port " + porto);
      System.out.println("Highway client comm - the filePath that is here is : " + filePath);
      System.out.println("Highway client comm - going to create the client socket.");

      checkHello = new Timer();
      checkIfClientReceivedApk = new Timer();

      String action = intent.getAction();
      System.out.println("Action of this service is  ::::::::::::::: " + action);
      if (action.equals("RECEIVE")) {
        isOutsideShare = false;
        nickname = intent.getStringExtra("nickname");//p meter na msg de hello
        isOutsideShare = intent.getBooleanExtra("isOutsideShare", false);
        retryConnection = new Timer();

        ConnectionHandler conn = new ConnectionHandler();
        new Thread(conn).start();
      } else if (action.equals("SEND")) {
        Bundle b = intent.getBundleExtra("bundle");
        //                listOfApps=(List<App>)b.getSerializable("listOfAppsToInstall");

        if (listOfApps == null || listOfApps.get(listOfApps.size() - 1).isOnChat()) {
          listOfApps = b.getParcelableArrayList("listOfAppsToInstall");
          System.out.println(
              "nothing on the list (or everything already sent) getting the new one.");
          SendThread sendThread = new SendThread();
          new Thread(sendThread).start();
        } else {
          //get the new list and add those members to the already existing list (with the members that it is sending)
          List<App> tempList = b.getParcelableArrayList("listOfAppsToInstall");
          listOfApps.addAll(tempList);
          //todo will add to the list, vbut will get stuck on the view

          Intent addedOneMore =
              new Intent(HighwayClientComm.this, HighwayTransferRecordActivity.class);
          addedOneMore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          addedOneMore.putExtra("isHotspot", isHotspot);
          addedOneMore.setAction("Addedapps");
          startActivity(addedOneMore);
        }

        System.out.println(
            "clientcomm : Just received the list of Apps :  the list of apps size is  :"
                + listOfApps.size());
      } else if (action.equals("DISCONNECT")) {

        disconnectMessage = intent.getStringExtra("disconnectMessage");
        nickname = intent.getStringExtra("disconnectNickname");
        disconnectThread dt = new disconnectThread();
        new Thread(dt).start();
      }
    }

    return START_STICKY;
  }

  @Override public void onDestroy() {
    System.out.println("On Destroy of the service !!!! Service on destroy ! ");
    super.onDestroy();
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  private boolean checkIfFits(long apkSize) {
    boolean fits = false;
    long availableSpace = -1L;
    StatFs stat = new StatFs(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "");
    availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
    if (availableSpace > apkSize) {
      fits = true;
    }
    return fits;
  }

  private void requestSendApps(final int index) {
    new Thread(new Runnable() {
      @Override public void run() {
        System.out.println(
            "I am here inside the requestSendApps Method, trying to send something ! ");

        File apkToSend;

        createSendNotification();

        App tmpItem = listOfApps.get(index);
        filePath = tmpItem.getFilePath();
        appName = tmpItem.getAppName();
        packageName = tmpItem.getPackageName();
        String obbsFilePath = tmpItem.getObbsFilePath();

        System.out.println("Send Thread : here after getting elements from the list! ");
        System.out.println("The app name is: " + appName + " , the filepath is " + filePath);

        if (tmpItem.getFromOutside().equals("inside")) {
          startSendingIntent(appName, packageName, false);
        } else {
          //call method to send the intent from outside.
          startSendingIntentOutside(appName, filePath);
        }

        long obbsSize = 0;
        int numberOfObbFiles = 1;
        if (!obbsFilePath.equals("noObbs")) {
          File obbsToSend = new File(obbsFilePath);
          obbsSize = obbsToSend.length();
          File[] obbFolder = obbsToSend.listFiles();
          if (obbFolder != null) {
            numberOfObbFiles = obbFolder.length;
            System.out.println("O numero de obb files nesta folder e : " + numberOfObbFiles);
          }
        }

        apkToSend = new File(filePath);

        if (apkToSend.exists()) {//do i reallly need this if?

          createSendNotification();

          apkToSendSize = apkToSend.length();
          System.out.println("FileSize is : " + apkToSendSize);
          //                        outText.writeObject(""+appName +": "+ apkSize);//appName to send to have the name of the file

          existsServer = false;

          try {

            outText.writeObject("Do you have space:-:"
                + appName
                + ":-: "
                + apkToSendSize
                + ":-:"
                + filePath
                + ":-:"
                + obbsFilePath
                + ":-:"
                + obbsSize
                + ":-:"
                + numberOfObbFiles);//appName to send to have the name of the file
            System.out.println("SENT : Do you have space:-:"
                + appName
                + ":-: "
                + apkToSendSize
                + ":-:"
                + filePath
                + ":-:"
                + obbsFilePath
                + ":-:"
                + obbsSize
                + ":-:"
                + numberOfObbFiles);
          } catch (IOException e) {
            AnalyticsLite.transferClick("Send App", "Unsuccessful Send");
            e.printStackTrace();
            System.out.println("There was a problem writing the size and the app name");
          }

          System.out.println(
              "ClientComm send thread : going to send this " + appName + ": " + apkToSendSize);
        } else {
          System.out.println(
              "There was a problem with the file, the path was incorrect, probably.");
          clientH.post(new Runnable() {
            @Override public void run() {
              Toast.makeText(context,
                  context.getResources().getString(R.string.fileProblem) + " " + appName,
                  Toast.LENGTH_SHORT).show();
            }
          });
        }
      }
    }).start();
  }

  private void createSendNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      mBuilderSend = new Notification.Builder(this);
      ((Notification.Builder) mBuilderSend).setContentTitle(
          this.getResources().getString(R.string.shareApps) + " - " + this.getResources()
              .getString(R.string.send))
          .setContentText(this.getResources().getString(R.string.preparingSend))
          .setSmallIcon(R.mipmap.ic_launcher);
    }
  }

  private void startSendingIntent(String name, String packageName, boolean isSent) {
    Intent transfRec = new Intent(HighwayClientComm.this, HighwayTransferRecordActivity.class);
    transfRec.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    transfRec.putExtra("receivedFilePath",
        name + ".apk");//meter isto numa var?tmb e usado la em cima na criacao da file
    transfRec.putExtra("joinMode", "appselection");
    transfRec.putExtra("nameOfTheApp",
        name);//sem o .apk pq do outro lado tmb e assim p o packagemanager
    transfRec.putExtra("AppPackageName", packageName);
    System.out.println("Sending an intent with Pkacage Name : " + packageName);
    transfRec.putExtra("isSent", isSent);
    transfRec.putExtra("received", false);
    transfRec.putExtra("needReSend", false);
    System.out.println("ClientComm send thread O boolean do hotspot ta a : " + isHotspot);
    transfRec.putExtra("isHotspot", isHotspot);
    startActivity(transfRec);
  }

  private void startSendingIntentOutside(String name, String filePath) {
    Intent transfRec = new Intent(HighwayClientComm.this, HighwayTransferRecordActivity.class);
    transfRec.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    transfRec.putExtra("receivedFilePath",
        name + ".apk");//meter isto numa var?tmb e usado la em cima na criacao da file
    transfRec.putExtra("joinMode", "appselection");
    transfRec.putExtra("nameOfTheApp",
        name);//sem o .apk pq do outro lado tmb e assim p o packagemanager
    transfRec.putExtra("sendFilePath", filePath);
    System.out.println("ClientComm send thread O boolean do hotspot ta a : " + isHotspot);
    transfRec.putExtra("isHotspot", isHotspot);
    transfRec.setAction("SendFromOutside");
    startActivity(transfRec);
  }

  private void sendApps(final String name, final String filepathToSend, final String obbsFilePath) {

    new Thread(new Runnable() {
      @Override public void run() {

        try {

          File newapkToSend = new File(filepathToSend);
          System.out.println("FilePathtoSend is : " + filepathToSend);

          byte[] buffer =
              new byte[8192];//size do buffer deveria variar consoante o size da app ? podia estabelcer algo para ser mais variavel.
          BufferedInputStream bis = new BufferedInputStream(new FileInputStream(newapkToSend));
          int count;
          totalToSend = 0; // so para testes - para saber quanto enviei.

          showSendProgress();
          while ((count = bis.read(buffer)) != -1) {
            System.out.println("buffer values :  " + buffer[0]);

            outApk.write(buffer, 0, count);

            totalToSend += count;

            outApk.reset();
            System.out.println(
                "Client Comm send thread - already sent this ammount : " + totalToSend);
          }
          outApk.flush();
          bis.close();

          System.out.println("Just sent the app  : " + name);

          if (!obbsFilePath.equals("noObbs")) {
            //tem obbs espera pela instrucao
            //                        System.out.println("Waiting for permission to send the obbs");
            System.out.println("Will start sending the obbs");
            //                        String obbsAllowance = (String) inApk.readObject();
            //
            //                        String[] obbInfo=obbsAllowance.split(":-:");
            //                        // deal with the obb send
            //                        String appName=obbInfo[1];
            //                        String obbFilePath=obbInfo[2];
            //                        Long obbSize=Long.parseLong(obbInfo[3]);
            //                        int numberOfObbFiles=Integer.parseInt(obbInfo[4]);

            sendObbs(appName, obbsFilePath);
          }
        } catch (IOException e) {
          //todo error while sendingApp - unsuccessfull send - client
          AnalyticsLite.transferClick("Send App", "Unsuccessful Send");

          e.printStackTrace();
        }

        if (obbsFilePath.equals("noObbs")) {

          finishSendNotification();

          //postDelayed to wait and check if this item was already sent.
          checkIfClientReceivedApk.schedule(new TimerTask() {
            @Override public void run() {
              checkIfApkWasReceived(name);
            }
          }, TIME_OUT);
        }
      }

      private void checkIfApkWasReceived(String name) {
        for (int i = 0; i < listOfApps.size(); i++) {
          if (listOfApps.get(i).getAppName().equals(name) && !listOfApps.get(i).isOnChat()) {
            System.out.println("Didn'checkHello receive confirmation from the app " + name);

            //todo unsuccessfull receive - server
            AnalyticsLite.transferClick("Receive App", "Unsuccessful received");

            //assume server is not there anymore.

            Intent noServer = new Intent(HighwayClientComm.this, HighwayActivity.class);
            noServer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            DataHolder.getInstance().setHotspot(false);
            DataHolder.getInstance().setServiceRunning(false);
            startActivity(noServer);
          }
        }
      }
    }).start();
  }

  @TargetApi(14) private void showSendProgress() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      sendProgressTask = new TimerTask() {
        @Override public void run() {
          int fileSize = (int) totalSizeSend;
          //                int actual= (totalToSend*100)/fileSize;
          int actual = totalToSend;
          //                ((Notification.Builder) mBuilderSend).setCOntentTitle();
          ((Notification.Builder) mBuilderSend).setContentText(
              HighwayClientComm.this.getResources().getString(R.string.sending) + " " + appName);
          ((Notification.Builder) mBuilderSend).setProgress(fileSize, actual, false);
          mNotifyManager.notify(id, ((Notification.Builder) mBuilderSend).getNotification());
        }
      };
      sendTimer.scheduleAtFixedRate(sendProgressTask, 0, 500);//delay, interval
    }
  }

  private void sendObbs(final String appName, final String obbFilePath) {
    new Thread(new Runnable() {
      @Override public void run() {
        try {
          //send file1
          File obbdir = new File(obbFilePath);
          for (File obbFile : obbdir.listFiles()) {//assuming there are no folders inside obb files. There are just the two files.

            String obbfileName = obbFile.getName();
            long obbfileSize = obbFile.length();//bytes
            outApk.writeObject("Will send ObbFile:-:" + obbfileName + ":-:" + obbfileSize);
            System.out.println("Will send the file : " + obbfileName);
            byte[] buffer = new byte[8192];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(obbFile));
            int count;
            while ((count = bis.read(buffer)) != -1) {

              outApk.write(buffer, 0, count);

              totalToSend += count;

              outApk.reset();
            }
            outApk.flush();
            bis.close();
          }
        } catch (IOException e) {
          System.out.println("It is trowing the output exception");
          System.out.println("There was an error sending ! I am on the catch of the write");
          AnalyticsLite.transferClick("Send App", "Unsuccessful Send");
          e.printStackTrace();
        }

        finishSendNotification();

        //postDelayed to wait and check if this item was already sent.
        checkIfClientReceivedApk.schedule(new TimerTask() {
          @Override public void run() {
            checkIfApkWasReceived(appName);
          }
        }, TIME_OUT);
      }

      private void checkIfApkWasReceived(String name) {
        for (int i = 0; i < listOfApps.size(); i++) {
          if (listOfApps.get(i).getAppName().equals(name) && !listOfApps.get(i).isOnChat()) {
            System.out.println("Didn'checkHello receive confirmation from the app " + name);

            //todo unsuccessfull receive - server
            AnalyticsLite.transferClick("Receive App", "Unsuccessful received");

            //assume server is not there anymore.

            Intent noServer = new Intent(HighwayClientComm.this, HighwayActivity.class);
            noServer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            DataHolder.getInstance().setHotspot(false);
            DataHolder.getInstance().setServiceRunning(false);
            startActivity(noServer);
          }
        }
      }
    }).start();
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
      sendProgressTask.cancel();
    }
  }

  private void receive(final String receivedApkName, final String obbsFilePath, final long obbsSize,
      final int numberOfObbFiles) {

    new Thread(new Runnable() {
      @Override public void run() {

        try {

          File apkReceived = new File(receivedAPKFilepath);
          //                            File apkReceived = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+receivedApkName + ".apk");
          int i = 1;
          while (apkReceived.exists()) {
            actualNameToReceive = receivedApkName + "(" + i + ")" + ".apk";
            receivedAPKFilepath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "/"
                    + actualNameToReceive;
            apkReceived = new File(receivedAPKFilepath);
            i++;
          }

          createReceiveNotification();
          i = 1;

          byte[] buffer = new byte[8192];
          FileOutputStream fos = new FileOutputStream(apkReceived);

          int count = 0;
          totalReceived = 0;//so apra ir vendo quanto recebi.
          showReceiveProgress();

          while (totalReceived <= fileSizeToReceive && (count = inApk.read(buffer)) != -1) {
            fos.write(buffer, 0, count);
            totalReceived += count;
            System.out.println(
                "Server Comm receive thread - already received this ammount : " + totalReceived);
            if (totalReceived == fileSizeToReceive) {
              System.out.println("Reached the end of the file  !");
              totalReceived++;

              AnalyticsLite.transferClick("Receive App", "Successful received");
              //todo call successfull receive  - client
            }
          }

          fos.flush();
          fos.close();

          if (!obbsFilePath.equals("noObbs")) {

            //                        outApk.writeObject("Send me the obbs:-:"+receivedApkName+":-:"+obbsFilePath+":-:"+obbsSize+":-:"+numberOfObbFiles);
            String[] obbFolderArray = obbsFilePath.split("/");
            String insideFolder = obbFolderArray[obbFolderArray.length - 1];
            String obbsFolder =
                Environment.getExternalStoragePublicDirectory("/") + "/Android/Obb/" + insideFolder;

            new File(obbsFolder).mkdir();

            for (int x = 0; x < numberOfObbFiles; x++) {

              buffer = new byte[8192];

              String obbMessage = (String) inApk.readObject();
              String[] obbInfo = obbMessage.split(":-:");
              String obbFileName = obbInfo[1];
              Long obbFileSize = Long.parseLong(obbInfo[2]);

              count = 0;
              totalReceived = 0;
              //                                            File obbFile=new File(obbFileName);
              FileOutputStream fout =
                  new FileOutputStream(new File(obbsFolder + "/" + obbFileName));

              if (obbMessage.contains("Will send ObbFile")) {
                while (totalReceived <= obbFileSize && (count = inApk.read(buffer)) != -1) {

                  fout.write(buffer, 0, count);
                  totalReceived += count;
                  System.out.println(
                      "Client Comm receive thread obbs - already received this ammount : "
                          + totalReceived);

                  if (totalReceived == obbFileSize) {
                    System.out.println("Reached the end of the file  !");
                    totalReceived++;
                  }
                }
              }
            }
          }
          outText.writeObject("OKreceived:-:" + receivedApkName);

          finishReceiveNotification();
          System.out.println("Already read everything !");

          //check if it is a valid apk file ? Signed ? How ?

          startReceivingIntent(receivedApkName, true);
        } catch (IOException e) {
          e.printStackTrace();
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private void createReceiveNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      mBuilderReceive = new Notification.Builder(this);
      ((Notification.Builder) mBuilderReceive).setContentTitle(
          this.getResources().getString(R.string.shareApps) + " - " + this.getResources()
              .getString(R.string.receive))
          .setContentText(
              this.getResources().getString(R.string.receiving) + " " + actualNameToReceive)
          .setSmallIcon(R.mipmap.ic_launcher);
      //falta o intent para an action a tomar.

      File f = new File(receivedAPKFilepath);
      Intent install = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(f),
          "application/vnd.android.package-archive");
      PendingIntent contentIntent = PendingIntent.getActivity(this, 0, install, 0);

      ((Notification.Builder) mBuilderReceive).setContentIntent(contentIntent);
    }
  }

  private void showReceiveProgress() {
    receiveProgressTask = new TimerTask() {
      @Override public void run() {
        int totalFileObbsSize = (int) totalSizeReceive;
        int actual = totalReceived;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
          ((Notification.Builder) mBuilderReceive).setContentText(
              HighwayClientComm.this.getResources().getString(R.string.receiving)
                  + " "
                  + actualNameToReceive);
          ((Notification.Builder) mBuilderReceive).setProgress(totalFileObbsSize, actual, false);
          mNotifyManager.notify(id, ((Notification.Builder) mBuilderReceive).getNotification());
        }
      }
    };

    receiveTimer.scheduleAtFixedRate(receiveProgressTask, 0, 500);//delay, interval
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
    receiveProgressTask.cancel();
  }

  private void startReceivingIntent(String receivedApkName, boolean isSent) {
    Intent transfRec = new Intent(HighwayClientComm.this, HighwayTransferRecordActivity.class);
    transfRec.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    transfRec.putExtra("joinMode", "appselection");
    transfRec.putExtra("receivedFilePath", actualNameToReceive);//actualname e local e tem o (i).
    transfRec.putExtra("nameOfTheApp",
        receivedApkName);//sem o .apk pq do outro lado tmb e assim p o packagemanager
    System.out.println("ClientComm receive thread O boolean do hotspot ta a : " + isHotspot);
    transfRec.putExtra("isHotspot", isHotspot);
    transfRec.putExtra("needReSend", false);
    transfRec.putExtra("received", true);
    transfRec.putExtra("isSent", true);
    startActivity(transfRec);
  }

  public class ConnectionHandler implements Runnable {

    @Override public void run() {
      try {
        customConnect();
      } catch (SocketException e) {
        System.out.println(" Error happened . Will try to reconnect ! ");

        if (retryConnectionTask == null) {
          createRetryConnectionTM();
        } else {
          retryConnectionTask.cancel();
          retryConnection.purge();
          createRetryConnectionTM();
        }

        System.out.println("Cleared tasks");
        if (time <= 64000) {//64 seconds - 7 trys
          retryConnection.schedule(retryConnectionTask, time);
          time = time * 2;
        }
        System.out.println("scheduled a new task; time : " + time);
      }
    }

    private void customConnect() throws SocketException {
      try {
        System.out.println("Executing the customConnect() method ( again) .");
        clientSocketToReceive = new Socket(serverIP, porto);

        clientSocketToSend = new Socket(serverIP, porto);

        outText = new ObjectOutputStream(clientSocketToReceive.getOutputStream());
        inText = new ObjectInputStream(clientSocketToReceive.getInputStream());

        //                clientSocketToSend= new Socket(serverIP, porto);

        outApk = new ObjectOutputStream(clientSocketToSend.getOutputStream());
        inApk = new ObjectInputStream(clientSocketToSend.getInputStream());

        System.out.println("client comm receive thread - doing the Initial handshake");
        System.out.println(
            "CLientComm connection handler : sending my initial message hello  - my nickname is : "
                + nickname);
        outText.writeObject("Nickname:-:" + nickname);
        System.out.println("ClientComm : Just sent my initial message.");

        try {
          String message = (String) inText.readObject();

          System.out.println("ClientComm :_ just read : " + message);
          System.out.println(
              "Client Comm receive thread - receive the handshake message from the server. Handshake is done.");
          handshakeDone = true;

          ReceiveThread receiveThread = new ReceiveThread();
          //                receiveThread.run();
          new Thread(receiveThread).start();

          if (isOutsideShare) {
            Intent canSend =
                new Intent(HighwayClientComm.this, HighwayTransferRecordActivity.class);
            canSend.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            canSend.putExtra("isAHotspot", isHotspot);
            canSend.setAction("ShareFromOutsideConfirmed");
            startActivity(canSend);
          }

          if (retryConnectionTask != null) {
            retryConnectionTask.cancel();
            retryConnection.purge();
          }
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
      } catch (IOException e) {
        e.printStackTrace();
        if (e instanceof SocketException) {
          if (retryConnectionTask != null) {
            retryConnectionTask.cancel();
            retryConnection.purge();
          }
          throw new SocketException();
        }
      }
    }

    private void createRetryConnectionTM() {
      retryConnectionTask = new TimerTask() {
        @Override public void run() {
          try {
            customConnect();
          } catch (SocketException e) {
            e.printStackTrace();
          }
        }
      };

      System.out.println("Started a new task");
    }
  }

  public class SendThread implements Runnable {

    @Override public void run() {
      try {

        AnalyticsLite.transferClick("Send App", "Send app");
        //todo call localytics send app - client
        System.out.println(
            "Highway client comm, i am here inText the send thread, going to create the outputstream object");

        if (outText != null) {
          outText.writeObject("Hello from the client");

          if (checkHello != null) {
            checkHello.schedule(new TimerTask() {
              @Override public void run() {
                checkIfServerAnswered();
              }
            }, TIME_OUT);
          }
        } else {
          Intent transfRec =
              new Intent(HighwayClientComm.this, HighwayTransferRecordActivity.class);
          transfRec.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

          System.out.println(
              "There was an eror and it was something related with the client connection.");
          System.out.println("The listOfApps.size() is : " + listOfApps.size());
          if (listOfApps != null && listOfApps.size() > 0) {

            for (int i = 0; i < listOfApps.size(); i++) {

              App tmpItem = listOfApps.get(i);
              filePath = tmpItem.getFilePath();
              appName = tmpItem.getAppName();
              packageName = tmpItem.getPackageName();
              System.out.println("his filepath to send is : " + filePath);
              System.out.println("his appname to send is : " + appName);

              startSendingIntent(appName, packageName, false);
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void checkIfServerAnswered() {

      if (!existsServer) {
        clientH.post(new Runnable() {
          @Override public void run() {
            //todo unsuccessfull receive - server
            AnalyticsLite.transferClick("Receive App", "Unsuccessful received");
            Toast.makeText(context, context.getResources().getString(R.string.hotspotLeft),
                Toast.LENGTH_LONG).show();
          }
        });

        Intent noServer = new Intent(HighwayClientComm.this, HighwayActivity.class);
        noServer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DataHolder.getInstance().setHotspot(false);
        DataHolder.getInstance().setServiceRunning(false);
        startActivity(noServer);
      }
    }
  }

  public class ReceiveThread implements Runnable {

    @Override public void run() {
      try {
        System.out.println("Client Comm Receive Thread - i am here inText the run method.");

        while (!clientSocketToReceive.isClosed()) {
          try {

            System.out.println(
                "Highway client comm receive thread - INSIDE THE WHILE CICLE, before the inText.readObject");

            String fileSizeAsString = (String) inText.readObject();

            if (fileSizeAsString.equals("Hello message from server")) {
              System.out.println("Received hello message from server");
              outText.writeObject("Ready to receive");

              //create Message - receive and add it to the queue
            } else if (fileSizeAsString.equals("Ready to receive")) {//server is ready to receive

              existsServer = true;
              checkHello.cancel();
              requestSendApps(0);
            } else if (fileSizeAsString.contains("There is no available space")) {

              String[] txt = fileSizeAsString.split(":-:");
              String app = txt[1];
              System.out.println("The server has no space available for " + app);
            } else if (fileSizeAsString.contains("AppFits")) {

              System.out.println("received a message that has appfits");
              String[] txt = fileSizeAsString.split(":-:");
              String name = txt[1];
              String filepathToSend = txt[2];
              String obbsFilePath = txt[3];

              totalSizeSend = Long.parseLong(txt[4]);

              System.out.println("the message receievd appfits has the name: "
                  + name
                  + " and the filepath : "
                  + filepathToSend);

              sendApps(name, filepathToSend, obbsFilePath);
              //todo call method that will to the sending starting on buffer.

            } else if (fileSizeAsString.contains("OKreceived")) {
              //o outro lado recebeu bem a file

              String[] txt = fileSizeAsString.split(":-:");
              String app = txt[1];

              System.out.println(
                  "Received the confirmation that the client received the app " + app);

              int positionToRemove = 0;

              for (int i = 0; i < listOfApps.size(); i++) {
                if (listOfApps.get(i).getAppName().equals(app) && !listOfApps.get(i).isOnChat()) {
                  positionToRemove = i;
                  System.out.println(
                      "GOing to remove this position of the list of apps and try toi send the next one.");
                  listOfApps.get(i).setOnChat(true);
                  startSendingIntent(app, packageName, true);
                }
              }
              if (listOfApps.size() > positionToRemove + 1) {
                //send the next one
                requestSendApps(positionToRemove + 1);
              }

              AnalyticsLite.transferClick("Send App", "Successful Send");
              //todo call localytics event send successful - client
            } else {
              System.out.println(
                  "Received this initial message with the size: " + fileSizeAsString);
              String[] valor = fileSizeAsString.split(":-:");

              if (valor[0].contains("Do you have space")) {

                //                            String receivedApkName=valor[0].trim(); //usar para o nome do ficheiro !!! IMP !!!!!
                final String receivedApkName =
                    valor[1]; //duplicated line pq acho que o nome da app n devia sofrer trim, nao vale a pena.
                fileSizeToReceive = Long.parseLong(valor[2].trim());
                System.out.println(" ClientComm receive thread : FileSize is at : "
                    + fileSizeToReceive
                    + " o nome da app e  : "
                    + receivedApkName);
                sendersFilePath = valor[3];
                String obbsFilePath = valor[4];
                long obbsSize = Long.parseLong(valor[5].trim());
                int numberOfObbFiles = Integer.parseInt(valor[6]);

                System.out.println(
                    "THe filepath from the sender that i just received is : " + sendersFilePath);

                if (obbsFilePath.equals("noObbs")) {
                  totalSizeReceive = fileSizeToReceive + obbsSize;
                } else {
                  totalSizeReceive = fileSizeToReceive;
                }
                boolean fits = checkIfFits(totalSizeReceive);

                if (!fits) {
                  System.out.println("There is no available space ");
                  outText.writeObject("There is no available space :-:" + receivedApkName);
                  System.out.println(
                      "cLIENT Comm just sent the message that there is no available space");

                  clientH.post(new Runnable() {
                    @Override public void run() {
                      Toast.makeText(context,
                          context.getResources().getString(R.string.noSpaceForApp)
                              + " "
                              + receivedApkName, Toast.LENGTH_LONG).show();
                    }
                  });
                } else {//se houver espaco suficiente

                  outText.writeObject("AppFits:-:"
                      + receivedApkName
                      + ":-:"
                      + sendersFilePath
                      + ":-:"
                      + obbsFilePath
                      + ":-:"
                      + totalSizeReceive);
                  System.out.println("Just send the message of AppFits");//ready to receive.

                  actualNameToReceive = receivedApkName + ".apk";
                  receivedAPKFilepath =
                      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                          + "/"
                          + actualNameToReceive;

                  receive(receivedApkName, obbsFilePath, obbsSize, numberOfObbFiles);

                  //                                    fos.flush();//todo testar se estes podem estar aqui. Faz sentido fechar.
                  //                                    fos.close();

                }
              }
            }
          } catch (ClassNotFoundException e) {

            //                        AnalyticsLite.transferClick("Receive App", "Unsuccessful received");
            //                       Log.e("Error receive","USED TO TAG UNSSUCESSFULL RECEIVE on client ------------ UNSUCCESS RECEIVE !!! ");
            e.printStackTrace();
          }
        }
      } catch (IOException e) {

        //                AnalyticsLite.transferClick("Receive App", "Unsuccessful received");
        //not sure if the right place
        //                Log.e("error receive ","TAG UNSSUCESSFULL RECEIVE on client ------------ UNSUCCESS RECEIVE !!! ");

        e.printStackTrace();
      }
    }
  }

  public class disconnectThread implements Runnable {

    @Override public void run() {
      System.out.println("going to send disconnect message");
      try {
        if (outText != null) {
          outText.writeObject("" + nickname + ":-:" + disconnectMessage);
          System.out.println("Sent the disconnect Message");
        } else {
          System.out.println("Tried disconnecting but the outputstream was empty");
        }
      } catch (IOException e) {
        System.out.println("There was an error on disconnect message");
        e.printStackTrace();
      }
    }
  }
}
