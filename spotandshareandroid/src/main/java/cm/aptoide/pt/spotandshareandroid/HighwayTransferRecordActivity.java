package cm.aptoide.pt.spotandshareandroid;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.spotandshareandroid.analytics.SpotAndShareAnalyticsInterface;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HighwayTransferRecordActivity extends ActivityView
    implements HighwayTransferRecordView {

  private static final int SELECT_APPS_REQUEST_CODE = 53110;
  private static List<HighwayTransferRecordItem> listOfItems = new ArrayList<>();
  private String receivedFilePath;
  private PackageManager packageManager;
  private boolean isHotspot;
  private String targetIPAddress;
  private String nickname;
  //    private int porto = 1234;
  private int porto = 55555;
  private LinearLayout send;
  private LinearLayout clearHistory;
  private TextView welcomeText;
  private String joinMode;
  //como chegou a activity - para nao ter problemas ao recriar. - NEEDS OPTIMIZATION
  private HighwayTransferRecordCustomAdapter adapter;
  private boolean received;
  //p cada recebido lança o transferRecord, logo precisara deste bool, que sera actualizado para cada
  private TextView textView;
  private ListView receivedAppListView;
  private String nameOfTheApp;
  private String packageName;
  private String tmpFilePath;
  private boolean needReSend;
  private boolean isSent;
  private WifiManager wifimanager;
  private int positionToReSend;
  private ArrayList<HighwayTransferRecordItem> toRemoveList;
  private ArrayList<String> connectedClients;

  private List<String> pathsFromOutsideShare;
  private List<App> itemsFromOutside;
  //used a list so that in the future we can send more than one item.
  private boolean outsideShare;
  private TransferRecordPresenter presenter;
  private TransferRecordManager transferRecordManager;
  private ApplicationsManager applicationsManager;
  private ApplicationReceiver applicationReceiver;
  private ApplicationSender applicationSender;
  private Toolbar mToolbar;
  private SpotAndShareAnalyticsInterface analytics;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.highway_transfer_record_activity);
    System.out.println("Inside the transferRecord Activity");

    welcomeText = (TextView) findViewById(R.id.Transf_rec_firstRow);
    receivedAppListView = (ListView) findViewById(R.id.transferRecordListView);
    textView = (TextView) findViewById(R.id.noRecordsTextView);
    send = (LinearLayout) findViewById(R.id.TransferRecordSendLayout);
    clearHistory = (LinearLayout) findViewById(R.id.TransferRecordClearLayout);
    mToolbar = (Toolbar) findViewById(R.id.shareAppsToolbar);

    analytics = ShareApps.getAnalytics();

    setUpToolbar();

    if (getIntent().getAction() != null && getIntent().getAction()
        .equals("ShareFromOutsideRequest")) {
      outsideShare = true;
      Bundle b = getIntent().getBundleExtra("bundle");
      pathsFromOutsideShare = b.getStringArrayList("pathsFromOutsideShare");
    } else if (getIntent().getAction() != null && getIntent().getAction()
        .equals("ShareFromOutsideHotspot")) {
      outsideShare = true;
      Bundle b = getIntent().getBundleExtra("bundle");
      pathsFromOutsideShare = b.getStringArrayList("pathsFromOutsideShare");
      itemsFromOutside = new ArrayList<App>();
      readApkArchive(pathsFromOutsideShare);
      if (itemsFromOutside.size() > 0) {
        sendFilesFromOutside(itemsFromOutside);
      } else {
        System.out.println("No supported apps to be sent.");
      }
    }

    isHotspot = getIntent().getBooleanExtra("isAHotspot", false);
    targetIPAddress = getIntent().getStringExtra("targetIP");
    nickname = getIntent().getStringExtra("nickname");

    if (isHotspot) {
      setTransparencySend(true);
      welcomeText.setText(this.getResources().getString(R.string.created_group, nickname));
      setTextViewMessage(false);
    } else {
      welcomeText.setText(this.getResources().getString(R.string.joined_group, nickname));
      setTextViewMessage(true);
    }
    setTransparencyClearHistory(true);

    receivedAppListView.setVisibility(View.GONE);

    //        Intent receiveIntent = null;
    //        if (isHotspot) {
    //            receiveIntent = new Intent(getApplicationContext(), HighwayServerComm.class);
    ////            receiveIntent = new Intent(this.getApplicationContext(), HighwayServerService.class);
    //            DataHolder.getInstance().createConnectedClientsList();
    //        } else {
    //            String aux = calculateActualIP();
    //            if (!targetIPAddress.equals(aux)) {
    //                targetIPAddress = aux;
    //                System.out.println("Checked the ip Address again and now it was different. It is now : " + aux);
    //            }
    //            receiveIntent = new Intent(this, HighwayClientService.class);
    //            receiveIntent.putExtra("targetIP", targetIPAddress);
    //
    //        }
    //
    //        receiveIntent.putExtra("nickname", nickname);
    //        receiveIntent.putExtra("port", porto);
    //        receiveIntent.putExtra("isHotspot", isHotspot);
    //        receiveIntent.putExtra("isOutsideShare", outsideShare);
    //        receiveIntent.setAction("RECEIVE");
    //        startService(receiveIntent);

    //        setButtonListeners();

    applicationsManager = new ApplicationsManager(this);

    applicationReceiver =
        new ApplicationReceiver(getApplicationContext(), isHotspot, porto, targetIPAddress,
            nickname);
    applicationSender = ApplicationSender.getInstance(getApplicationContext(), isHotspot);
    transferRecordManager = new TransferRecordManager(applicationsManager);

    Disconnecter disconnecter = new Disconnecter(getApplicationContext());

    presenter = new TransferRecordPresenter(this, applicationReceiver, applicationSender,
        transferRecordManager, isHotspot, disconnecter,
        ConnectionManager.getInstance(this.getApplicationContext()), analytics);
    attachPresenter(presenter);
  }

  private void setUpToolbar() {
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayShowTitleEnabled(true);
      getSupportActionBar().setTitle(getResources().getString(R.string.spot_share));
    }
  }

  public void readApkArchive(List<String> list) {
    if (packageManager == null) {
      packageManager = getPackageManager();
    }
    for (int j = 0; j < list.size(); j++) {
      System.out.println("Inside the reaapkarchive list size is : " + list.size());
      String path = list.get(j);
      System.out.println("The path is : " + path);
      PackageInfo packageInfo = packageManager.getPackageArchiveInfo(path, 0);
      if (packageInfo != null) {
        packageInfo.applicationInfo.sourceDir = path;
        packageInfo.applicationInfo.publicSourceDir = path;
        Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
        String appName = (String) packageInfo.applicationInfo.loadLabel(packageManager);
        String packageName = packageInfo.applicationInfo.packageName;
        App tmp = new App(icon, appName, packageName, path,
            "outside");// received e o bool metido no intent.
        //check if has obbs
        String obbsFilePath = checkIfHasObb(packageName);
        //add obb path
        tmp.setObbsFilePath(obbsFilePath);
        itemsFromOutside.add(tmp);
      } else {
        String[] pathArray = path.split("/");
        String fileName = pathArray[pathArray.length - 1];
        Toast.makeText(this, getResources().getString(R.string.unsupportedApp, fileName),
            Toast.LENGTH_SHORT).show();
      }
    }
  }

  public void sendFilesFromOutside(List<App> list) {
    System.out.println(
        "I am here inside the sendFilesFromOutside the size of the list is : " + list.size());
    Intent sendIntent = null;
    if (isHotspot) {
      System.out.println("Send a file from outside - hotspot");
      sendIntent = new Intent(this, HighwayServerService.class);
    } else {
      System.out.println("Send a file from outside - not a hotspot");
      sendIntent = new Intent(this, HighwayClientService.class);
      sendIntent.putExtra("targetIP", targetIPAddress);
    }
    sendIntent.putExtra("port", porto);
    sendIntent.putExtra("isHotspot", isHotspot);

    Bundle tmp = new Bundle();
    tmp.putParcelableArrayList("listOfAppsToInstall", new ArrayList<Parcelable>(list));
    sendIntent.putExtra("bundle", tmp);
    sendIntent.setAction("SEND");
    startService(sendIntent);
  }

  public String checkIfHasObb(String appName) {
    boolean hasObb = false;
    String obbsFilePath = "noObbs";
    String obbPath = Environment.getExternalStoragePublicDirectory("/") + "/Android/Obb/";
    File obbFolder = new File(obbPath);
    File[] list = obbFolder.listFiles();
    if (list != null) {
      System.out.println("list lenght is : " + list.length);
      if (list.length > 0) {
        System.out.println("appName is : " + appName);
        for (int i = 0; i < list.length; i++) {
          System.out.println("List get name is : " + list[i].getName());
          if (list[i].getName().equals(appName)) {
            hasObb = true;
            obbsFilePath = list[i].getAbsolutePath();
          }
        }
      }
    }
    return obbsFilePath;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();
    //    todo add check for the right button
    onBackPressed();

    return super.onOptionsItemSelected(item);
  }

  @Override public void onBackPressed() {
    Dialog onBack = createOnBackDialog();
    onBack.show();
  }

  private Dialog createOnBackDialog() {
    if (listOfItems != null && listOfItems.size() >= 1) {
      listOfItems.clear();
      if (adapter != null) {
        adapter.clearListOfItems();
      }
    }

    if (isHotspot) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);

      builder.setTitle(this.getResources().getString(R.string.alert))
          .setMessage(this.getResources().getString(R.string.alertCreatorLeave))
          .setPositiveButton(this.getResources().getString(R.string.leave),
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  sendServerShutdownMessage();
                  presenter.listenToDisconnect();
                }
              })
          .setNegativeButton(this.getResources().getString(R.string.cancel),
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  // User cancelled the dialog
                }
              });
      return builder.create();
    } else {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);

      builder.setTitle(this.getResources().getString(R.string.alert))
          .setMessage(this.getResources().getString(R.string.alertClientLeave))
          .setPositiveButton(this.getResources().getString(R.string.leave),
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  presenter.listenToDisconnect();
                  sendDisconnectMessage();
                }
              })
          .setNegativeButton(this.getResources().getString(R.string.cancel),
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  // User cancelled the dialog
                }
              });
      return builder.create();
    }
  }


  private void sendServerShutdownMessage() {
    Intent shutdown = new Intent(this, HighwayServerService.class);
    shutdown.setAction("SHUTDOWN_SERVER");
    startService(shutdown);
  }

  private void sendDisconnectMessage() {
    Intent disconnect = new Intent(this, HighwayClientService.class);
    disconnect.setAction("DISCONNECT");
    startService(disconnect);
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    outsideShare = false;

    System.out.println("I am here in the new intent");

    setIntent(intent);
    isHotspot = getIntent().getBooleanExtra("isHotspot", false);

    if (intent.getAction() != null && intent.getAction()
        .equals("ShareFromOutsideHotspot")) {
      textView.setVisibility(View.GONE);
      receivedAppListView.setVisibility(View.VISIBLE);
      outsideShare = true;
      Bundle b = getIntent().getBundleExtra("bundle");
      pathsFromOutsideShare = b.getStringArrayList("pathsFromOutsideShare");
      itemsFromOutside = new ArrayList<App>();
      readApkArchive(pathsFromOutsideShare);
      if (itemsFromOutside.size() > 0) {
        sendFilesFromOutside(itemsFromOutside);
      } else {
        System.out.println("No supported apps to be sent.");
      }
    } else if (intent.getAction() != null && intent.getAction().equals("ReSendFromOutside")) {
      textView.setVisibility(View.GONE);
      receivedAppListView.setVisibility(View.VISIBLE);
      receivedFilePath = intent.getStringExtra("resSendFilePath");
      nameOfTheApp = getIntent().getStringExtra("nameOfTheApp");
      resendOutside(nameOfTheApp, receivedFilePath);
    } else if (intent.getAction() != null && intent.getAction().equals("SendFromOutside")) {

      textView.setVisibility(View.GONE);
      receivedAppListView.setVisibility(View.VISIBLE);

      nameOfTheApp = getIntent().getStringExtra("nameOfTheApp");
      receivedFilePath = getIntent().getStringExtra("sendFilePath");
      sendOutside(nameOfTheApp, receivedFilePath);
    } else if (getIntent().getAction() != null && getIntent().getAction()
        .equals("ShareFromOutsideConfirmed")) {

      itemsFromOutside = new ArrayList<App>();
      isHotspot = getIntent().getBooleanExtra("isAHotspot", false);
      readApkArchive(pathsFromOutsideShare);
      if (itemsFromOutside.size() > 0) {
        sendFilesFromOutside(itemsFromOutside);
      } else {
        System.out.println("No supported apps to be sent.");
      }
    } else if (getIntent().getAction() != null && getIntent().getAction().equals("Addedapps")) {
      //do nothing
      System.out.println("Added another app to the list of selected apps to send");
    } else {
      textView.setVisibility(View.GONE);// todo reestructure, this part of repeated code.
      receivedAppListView.setVisibility(View.VISIBLE);
      outsideShare = false;
    }

    if (adapter == null) {
      adapter = new HighwayTransferRecordCustomAdapter(this, listOfItems);
      receivedAppListView.setAdapter(adapter);
    } else {
      adapter.notifyDataSetChanged();
    }
  }

  public void resendOutside(String name,
      String filePath) {//file to deal with the re-send from outside

    PackageInfo packageInfo = packageManager.getPackageArchiveInfo(receivedFilePath, 0);
    if (packageInfo != null) {
      packageInfo.applicationInfo.sourceDir = receivedFilePath;
      packageInfo.applicationInfo.publicSourceDir = receivedFilePath;
      Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
      String appName = (String) packageInfo.applicationInfo.loadLabel(packageManager);
      String packageName = packageInfo.applicationInfo.packageName;
      String versionName = packageInfo.versionName;

      HighwayTransferRecordItem tmp =
          new HighwayTransferRecordItem(icon, appName, packageName, receivedFilePath, false,
              versionName);// received e o bool metido no intent.

      tmp.setNeedReSend(true);
      tmp.setSent(false);
      tmp.setFromOutside("outside");

      if (!listOfItems.contains(tmp)) {
        listOfItems.add(tmp);
        System.out.println("List of apps the size is : " + listOfItems.size());
      }
    }
  }

  private void sendOutside(String name, String filePath) {
    PackageInfo packageInfo = packageManager.getPackageArchiveInfo(filePath, 0);
    if (packageInfo != null) {
      packageInfo.applicationInfo.sourceDir = filePath;
      packageInfo.applicationInfo.publicSourceDir = filePath;
      Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
      String appName = (String) packageInfo.applicationInfo.loadLabel(packageManager);
      String packageName = packageInfo.applicationInfo.packageName;
      String versionName = packageInfo.versionName;

      HighwayTransferRecordItem tmp =
          new HighwayTransferRecordItem(icon, appName, packageName, receivedFilePath, false,
              versionName);// received e o bool metido no intent.

      tmp.setNeedReSend(false);
      tmp.setSent(false);
      tmp.setFromOutside("outside");

      if (!listOfItems.contains(tmp)) {
        listOfItems.add(tmp);
        System.out.println("List of apps the size is : " + listOfItems.size());
      }
    }
  }

  @Override protected void onDestroy() {
    presenter.onDestroy();
    super.onDestroy();
    ApplicationSender.reset();
    DataHolder.reset();
  }

  @Override public void setUpSendButtonListener() {
    send.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        presenter.clickedOnSendButton();
      }
    });
  }

  @Override public void setUpClearHistoryListener() {
    clearHistory.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        presenter.clickedOnClearHistoryButton();
      }
    });
  }

  @Override
  public void handleReceivedApp(boolean received, boolean needReSend, String tmpFilePath) {
    presenter.receivedAnApp(received, needReSend, tmpFilePath);
  }

  @Override public void showNewCard(HighwayTransferRecordItem item) {
    if (receivedAppListView.getVisibility() != View.VISIBLE
        && textView.getVisibility() == View.VISIBLE) {
      receivedAppListView.setVisibility(View.VISIBLE);
      textView.setVisibility(View.GONE);
    }

    if (adapter != null) {
      adapter.addTransferedItem(item);
      if (adapter.getCount() - 1 >= 0) {
        this.receivedAppListView.setSelection(adapter.getCount() - 1);
      }
    }
  }

  @Override public void updateItemStatus(int positionToReSend, boolean isSent, boolean needReSend) {
    if (adapter != null) {
      adapter.updateItem(positionToReSend, isSent, needReSend);
      adapter.notifyDataSetChanged();
    }
  }


  @Override public void showNoConnectedClientsToast() {
    Toast.makeText(HighwayTransferRecordActivity.this,
        HighwayTransferRecordActivity.this.getResources()
            .getString(R.string.reSendError_no_friends_in_group), Toast.LENGTH_SHORT).show();
  }

  @Override public void openAppSelectionView() {
    Intent appSelection = new Intent().setClass(HighwayTransferRecordActivity.this,
        HighwayAppSelectionActivity.class);
    Log.i("TransferRecordActivity ", "going to start the app selection activity");
    System.out.println("The is hotspot boolean is : " + isHotspot);
    appSelection.putExtra("isAHotspot", isHotspot);
    startActivityForResult(appSelection, SELECT_APPS_REQUEST_CODE);
  }

  @Override public void showNoRecordsToDeleteToast() {
    Toast.makeText(this,
        HighwayTransferRecordActivity.this.getResources().getString(R.string.noRecordsDelete),
        Toast.LENGTH_SHORT).show();
  }

  @Override public void showDeleteHistoryDialog() {
    Dialog d = createDialogToDelete();
    d.show();
  }

  public Dialog createDialogToDelete() {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle(this.getResources().getString(R.string.warning))
        .setMessage(this.getResources().getString(R.string.clear_history_warning))
        .setPositiveButton(this.getResources().getString(R.string.delete),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {

                setTransparencyClearHistory(true);
                presenter.deleteAllApps();
              }
            })
        .setNegativeButton(this.getResources().getString(R.string.cancel),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                System.out.println(
                    "TransferREcordsCustomAdapter : Person pressed the CANCEL BUTTON !!!!!!!! ");
              }
            });
    return builder.create();
  }

  @Override public void refreshAdapter(List<HighwayTransferRecordItem> toRemoveList) {
    if (adapter != null) {
      adapter.clearListOfItems(toRemoveList);
      adapter.notifyDataSetChanged();
    }
  }

  @Override public void hideReceivedAppMenu() {
    textView.setVisibility(View.VISIBLE);
    receivedAppListView.setVisibility(View.GONE);
  }

  @Override public void showInstallErrorDialog(String appName) {
    Dialog dialog = createInstallErrorDialog(appName);
    dialog.show();
  }

  public Dialog createInstallErrorDialog(String appName) {
    String message =
        String.format(getResources().getString(R.string.errorAppVersionNew), appName, appName);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setMessage(message);
    builder.setPositiveButton(this.getResources().getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            System.out.println("Pressed OK in the error of the app version");
          }
        });

    AlertDialog dialog = builder.create();
    return dialog;
  }

  @Override public void showDialogToInstall(String appName, String filePath, String packageName) {
    Dialog dialog = createDialogToInstall(appName, filePath, packageName);
    dialog.show();
  }

  public Dialog createDialogToInstall(final String appName, final String filePath,
      final String packageName) {
    String message = String.format(getResources().getString(R.string.alertInstallApp), appName);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle(getResources().getString(R.string.alert));
    builder.setMessage(message);
    builder.setPositiveButton(getResources().getString(R.string.install),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            presenter.installApp(filePath, packageName);
          }
        })
        .setNegativeButton(getResources().getString(R.string.cancel),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
              }
            });
    return builder.create();
  }

  @Override public void showDialogToDelete(HighwayTransferRecordItem item) {
    Dialog dialog = createDialogToDelete(item);
    dialog.show();
  }

  @Override public void setAdapterListeners(TransferRecordListener listener) {
    adapter.setListener(listener);
  }

  @Override public void notifyChanged() {
    if (adapter != null) {
      adapter.notifyDataSetChanged();
    }
  }

  @Override public void generateAdapter(List<HighwayTransferRecordItem> list) {
    if (adapter == null) {
      adapter = new HighwayTransferRecordCustomAdapter(this, listOfItems);
      receivedAppListView.setAdapter(adapter);
    }
  }

  @Override public void showGeneralErrorToast() {
    Toast.makeText(this, R.string.generalError, Toast.LENGTH_LONG).show();
  }

  @Override public void showRecoveringWifiStateToast() {
    Toast.makeText(this, this.getResources().getString(R.string.recoveringWifiState),
        Toast.LENGTH_SHORT).show();
  }

  @Override public void dismiss() {
    finish();
  }

  @Override public void showServerLeftMessage() {
    Toast.makeText(this, this.getResources().getString(R.string.groupCreatorLeft),
        Toast.LENGTH_SHORT).show();
  }

  @Override public void clearAdapter() {
    adapter.clearListOfItems();
  }

  public boolean getTransparencySend() {
    return send.isEnabled();
  }

  public void setTransparencySend(boolean transparent) {
    if (transparent) {
      send.setAlpha(0.3f);
      send.setEnabled(false);
    } else {
      send.setAlpha(1);
      send.setEnabled(true);
    }
  }

  public boolean getTransparencyClearHistory() {
    return clearHistory.isEnabled();
  }

  public void setTransparencyClearHistory(boolean transparent) {
    if (transparent) {
      clearHistory.setAlpha(.3f);
      clearHistory.setEnabled(false);
    } else {
      clearHistory.setAlpha(1);
      clearHistory.setEnabled(true);
    }
  }

  public void setTextViewMessage(boolean clientsAvailable) {
    if (clientsAvailable) {
      textView.setText(R.string.share_instruction);
    } else {
      textView.setText(R.string.waiting_group_friends);
    }
  }

  public void setInitialApConfig() {
    if (wifimanager == null) {
      wifimanager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
    }
    Method[] methods = wifimanager.getClass().getDeclaredMethods();
    WifiConfiguration wc = DataHolder.getInstance().getWcOnJoin();
    for (Method m : methods) {
      if (m.getName().equals("setWifiApConfiguration")) {

        try {
          Method setConfigMethod =
              wifimanager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
          System.out.println("Re-seting the wifiAp configuration to what it was before !!! ");
          setConfigMethod.invoke(wifimanager, wc);
        } catch (NoSuchMethodException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
      if (m.getName().equals("setWifiApEnabled")) {

        try {
          System.out.println("Desligar o hostpot ");
          m.invoke(wifimanager, wc, false);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private Dialog createDialogToDelete(final HighwayTransferRecordItem item) {

    String message =
        String.format(getResources().getString(R.string.alertDeleteApp), item.getAppName());
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle(getResources().getString(R.string.alert));
    builder.setMessage(message);
    builder.setPositiveButton(getResources().getString(R.string.delete),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {

            presenter.deleteAppFile(item);
          }
        })
        .setNegativeButton(getResources().getString(R.string.cancel),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
              }
            });
    return builder.create();
  }
}
