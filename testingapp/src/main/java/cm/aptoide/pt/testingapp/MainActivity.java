package cm.aptoide.pt.testingapp;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.aptoidesdk.Ad;
import cm.aptoide.pt.aptoidesdk.Aptoide;
import cm.aptoide.pt.aptoidesdk.entities.util.SyncEndlessController;
import cm.aptoide.pt.aptoidesdk.entities.App;
import cm.aptoide.pt.aptoidesdk.entities.SearchResult;
import cm.aptoide.pt.aptoidesdk.entities.misc.Group;
import cm.aptoide.pt.imageloader.ImageLoader;
import java.io.File;
import java.util.List;

/**
 * Created by neuro on 28-10-2016.
 */
public class MainActivity extends AppCompatActivity {

  private TextView tv;
  private DownloadManager downloadManager;
  private View app1;
  private View app2;
  private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

    @Override public void onReceive(Context context, Intent intent) {

      //check if the broadcast message is for our enqueued download
      long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

      if (true) {

        Toast toast =
            Toast.makeText(MainActivity.this, "Image Download Complete", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 25, 400);
        toast.show();

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(referenceId);
        Cursor cursor = downloadManager.query(query);

        cursor.moveToFirst();
        String filePath = cursor.getString(1);
        cursor.close();

        startInstallIntent(MainActivity.this, new File(filePath));
        unregisterReceiver(downloadReceiver);
      }
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    bindViews();

    tv = (TextView) findViewById(R.id.tv);

    Aptoide.integrate(this, "dummyoem");
    Aptoide.setDebug(true);

    IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    registerReceiver(downloadReceiver, filter);
  }

  private void bindViews() {
    app1 = findViewById(R.id.app1);
    app2 = findViewById(R.id.app2);
  }

  public void adsClick(View view) {

    List<Ad> l = Aptoide.getAds(3);
    if (l == null || l.size() == 0) {
      tv.setText("ad response: empty");
    } else {
      setupApp(app1, l.get(0), false);
      setupApp(app2, l.get(1), true);
    }
  }

  private void setupApp(final View app, final Ad ad, final boolean sponsored) {
    TextView appName = (TextView) app.findViewById(R.id.app_name);
    ImageView appIcon = (ImageView) app.findViewById(R.id.app_icon);
    TextView sponsoredOrNot = (TextView) app.findViewById(R.id.app_sponsored);

    appName.setText(ad.getName());
    ImageLoader.load(ad.getIconPath(), appIcon);
    sponsoredOrNot.setText(sponsored ? "sponsored" : "");

    app.setOnClickListener(view -> {

      System.out.println("clicked on " + ad.getName());
      if (sponsored) {
        downloadData(Aptoide.getApp(ad).getFile().getPath());
      } else {
        downloadData(Aptoide.getApp(ad.getAppId()).getFile().getPath());
      }
    });

    app.setVisibility(View.VISIBLE);
  }

  public void searchClick(View view) {

    List<SearchResult> l = Aptoide.searchApps("facebook", "apps");
    if (l == null || l.size() == 0) {
      tv.setText("search response: empty");
    } else {
      StringBuilder sb = new StringBuilder();
      for (SearchResult i : l) {
        sb.append("search: app name: " + i.getName() + "\n");
      }
      tv.setText(sb.toString());
    }
  }

  public void appsClick(View view) {

    App l = Aptoide.getApp("cm.aptoide.pt", "apps");
    if (l == null) {
      tv.setText("app response: empty");
    } else {
      tv.setText("app name: " + l.getName());
    }
  }

  SyncEndlessController<App> appEndlessController;

  public void listAppsClick(View view) {

    if (appEndlessController == null) {
      appEndlessController = Aptoide.listApps(Group.GAMES);
    }
    List<App> first = appEndlessController.loadMore();
    if (first.size() == 0) {
      tv.setText("ListApps response: empty");
    } else {
      tv.setText("app name: " + first.get(0).getName());
    }
  }

  private void DownloadStatus(Cursor cursor, long DownloadId) {

    //column for download  status
    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
    int status = cursor.getInt(columnIndex);
    //column for reason code if the download failed or paused
    int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
    int reason = cursor.getInt(columnReason);
    //get the download filename
    int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
    String filename = cursor.getString(filenameIndex);

    String statusText = "";
    String reasonText = "";

    switch (status) {
      case DownloadManager.STATUS_FAILED:
        statusText = "STATUS_FAILED";
        switch (reason) {
          case DownloadManager.ERROR_CANNOT_RESUME:
            reasonText = "ERROR_CANNOT_RESUME";
            break;
          case DownloadManager.ERROR_DEVICE_NOT_FOUND:
            reasonText = "ERROR_DEVICE_NOT_FOUND";
            break;
          case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
            reasonText = "ERROR_FILE_ALREADY_EXISTS";
            break;
          case DownloadManager.ERROR_FILE_ERROR:
            reasonText = "ERROR_FILE_ERROR";
            break;
          case DownloadManager.ERROR_HTTP_DATA_ERROR:
            reasonText = "ERROR_HTTP_DATA_ERROR";
            break;
          case DownloadManager.ERROR_INSUFFICIENT_SPACE:
            reasonText = "ERROR_INSUFFICIENT_SPACE";
            break;
          case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
            reasonText = "ERROR_TOO_MANY_REDIRECTS";
            break;
          case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
            reasonText = "ERROR_UNHANDLED_HTTP_CODE";
            break;
          case DownloadManager.ERROR_UNKNOWN:
            reasonText = "ERROR_UNKNOWN";
            break;
        }
        break;
      case DownloadManager.STATUS_PAUSED:
        statusText = "STATUS_PAUSED";
        switch (reason) {
          case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
            reasonText = "PAUSED_QUEUED_FOR_WIFI";
            break;
          case DownloadManager.PAUSED_UNKNOWN:
            reasonText = "PAUSED_UNKNOWN";
            break;
          case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
            reasonText = "PAUSED_WAITING_FOR_NETWORK";
            break;
          case DownloadManager.PAUSED_WAITING_TO_RETRY:
            reasonText = "PAUSED_WAITING_TO_RETRY";
            break;
        }
        break;
      case DownloadManager.STATUS_PENDING:
        statusText = "STATUS_PENDING";
        break;
      case DownloadManager.STATUS_RUNNING:
        statusText = "STATUS_RUNNING";
        break;
      case DownloadManager.STATUS_SUCCESSFUL:
        statusText = "STATUS_SUCCESSFUL";
        reasonText = "Filename:\n" + filename;
        break;
    }

    //if(DownloadId == Music_DownloadId) {
    //
    //  Toast toast = Toast.makeText(MainActivity.this,
    //      "Music Download Status:" + "\n" + statusText + "\n" +
    //          reasonText,
    //      Toast.LENGTH_LONG);
    //  toast.setGravity(Gravity.TOP, 25, 400);
    //  toast.show();
    //
    //}
    //else {

    Toast toast =
        Toast.makeText(MainActivity.this, "Image Download Status:" + "\n" + statusText + "\n" +
            reasonText, Toast.LENGTH_LONG);
    toast.setGravity(Gravity.TOP, 25, 400);
    toast.show();

    // Make a delay of 3 seconds so that next toast (Music Status) will not merge with this one.
    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override public void run() {
      }
    }, 3000);

    //}

  }

  private void startInstallIntent(Context context, File file) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  private long downloadData(String filePath) {

    Uri uri = Uri.parse(filePath);

    long downloadReference;

    // Create request for android download manager
    downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    DownloadManager.Request request = new DownloadManager.Request(uri);

    //Setting title of request
    request.setTitle("Data Download");

    //Setting description of request
    request.setDescription("Android Data download using DownloadManager.");

    //Set the local destination for the downloaded file to a path within the application's external files directory
    //if(v.getId() == R.id.DownloadMusic)
    request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS,
        "AndroidTutorialPoint.mp3");
    //else if(v.getId() == R.id.DownloadImage)
    //  request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS,"AndroidTutorialPoint.jpg");

    //Enqueue download and save into referenceId
    downloadReference = downloadManager.enqueue(request);

    //Button DownloadStatus = (Button) findViewById(R.id.DownloadStatus);
    //DownloadStatus.setEnabled(true);
    //Button CancelDownload = (Button) findViewById(R.id.CancelDownload);
    //CancelDownload.setEnabled(true);

    return downloadReference;
  }
}
