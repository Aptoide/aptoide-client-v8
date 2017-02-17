package cm.aptoide.pt.shareapppsandroid;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 18-07-2016.
 */
public class HighwayActivity extends ActivityView implements HighwayView {

  private static final int PERMISSIONS_REQUEST_CODE = 6531;
  public String deviceName;
  public LinearLayout joinGroupButton;
  public LinearLayout createGroupButton;
  public HighwayRadarTextView radarTextView;
  public LinearLayout progressBarLayout;
  public String chosenHotspot = "";
  public LinearLayout groupButtonsLayout;

  private ProgressBar buttonsProgressBar;//progress bar for when user click the buttons

  private boolean outsideShare = false;
  private List<String> pathsFromOutsideShare;
  private boolean joinGroupFlag;
  private HighwayPresenter presenter;
  private ConnectionManager connectionManager;
  private AnalyticsManager analyticsManager;
  private GroupManager groupManager;
  private OutsideShareManager outsideShareManager;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //deviceName = getIntent().getStringExtra("deviceName");
    requestPermissions();

    deviceName = getIntent().getStringExtra("deviceName");
    connectionManager = new ConnectionManager(this);
    analyticsManager = new AnalyticsManager(getApplicationContext(), getIntent());
    groupManager = new GroupManager(connectionManager);

    setContentView(R.layout.highway_activity);

    HighwayRadarScan radar = (HighwayRadarScan) findViewById(R.id.radar);
    radarTextView = (HighwayRadarTextView) findViewById(R.id.hotspotTextView);

    progressBarLayout = (LinearLayout) findViewById(R.id.circular_progress_bar);
    groupButtonsLayout = (LinearLayout) findViewById(R.id.groupButtonsLayout);

    buttonsProgressBar = (ProgressBar) findViewById(R.id.buttonsProgressBar);
    joinGroupButton = (LinearLayout) findViewById(R.id.joinGroup);//send
    createGroupButton = (LinearLayout) findViewById(R.id.createGroup);//receive
    radarTextView.setActivity(this);

    presenter = new HighwayPresenter(this, deviceName, new DeactivateHotspotTask(connectionManager),
        connectionManager, analyticsManager, groupManager);
    attachPresenter(presenter);

    if (getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_SEND)) {
      //            pathsFromOutsideShare=new ArrayList<>();
      //            presenter.generateLocalyticsSettings();
      outsideShareManager = new OutsideShareManager();
      presenter.setOutsideShareManager(outsideShareManager);
      Uri uri = (Uri) getIntent().getExtras().get("android.intent.extra.STREAM");
      presenter.getAppFilePathFromOutside(uri);
    } else if (getIntent().getAction() != null && getIntent().getAction()
        .equals(Intent.ACTION_SEND_MULTIPLE)) {
      //            pathsFromOutsideShare=new ArrayList<>();
      //            presenter.generateLocalyticsSettings();
      outsideShareManager = new OutsideShareManager();
      presenter.setOutsideShareManager(outsideShareManager);
      ArrayList<Uri> uriList =
          (ArrayList<Uri>) getIntent().getExtras().get("android.intent.extra.STREAM");
      presenter.getMultipleAppFilePathsFromOutside(uriList);
    }
  }

  private void requestPermissions() {
    ActivityCompat.requestPermissions(this, new String[] {
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_SETTINGS,
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    }, PERMISSIONS_REQUEST_CODE);
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    outsideShare = false;

    if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)) {
      //            pathsFromOutsideShare=new ArrayList<>();
      //            presenter.generateLocalyticsSettings();
      //            String tmp = intent.getStringExtra(Intent.EXTRA_STREAM);
      //            outsideShare=true;
      outsideShareManager = new OutsideShareManager();
      presenter.setOutsideShareManager(outsideShareManager);
      Uri uri = (Uri) intent.getExtras().get("android.intent.extra.STREAM");
      presenter.getAppFilePathFromOutside(uri);
      //            String way = uri.getPath();
      //            pathsFromOutsideShare.add(way);
      //            System.out.println("way is : : : "+way);

    } else if (intent.getAction() != null && intent.getAction()
        .equals(Intent.ACTION_SEND_MULTIPLE)) {
      //            pathsFromOutsideShare=new ArrayList<>();
      //            presenter.generateLocalyticsSettings();
      //            outsideShare=true;
      outsideShareManager = new OutsideShareManager();
      presenter.setOutsideShareManager(outsideShareManager);
      ArrayList<Uri> uriList =
          (ArrayList<Uri>) intent.getExtras().get("android.intent.extra.STREAM");
      presenter.getMultipleAppFilePathsFromOutside(uriList);
      //            for(int i=0;i<uriList.size();i++){
      //                String way=uriList.get(i).getPath();
      //                System.out.println("way is : : : "+way);
      //                pathsFromOutsideShare.add(way);
      //            }
    }
  }

  @Override public void onBackPressed() {
    //        try{
    ////            unregisterReceiver(wifireceiver);
    ////            unregisterReceiver(wfr);
    //        }catch( IllegalArgumentException e){
    //            System.out.println("Tried to to unregister a receiver that was already unregistered");
    //        }

    super.onBackPressed();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Group group = new Group(chosenHotspot);

    presenter.onActivityResult(group);
  }

  @Override public void showConnections() {
    presenter.scanNetworks();
  }

  @Override public void enableButtons(boolean enable) {
    if (enable) {
      progressBarLayout.setVisibility(View.GONE);
      groupButtonsLayout.setVisibility(View.VISIBLE);
      System.out.println("Activating the buttons !!!!!!!!");
    } else {//disable
      progressBarLayout.setVisibility(View.VISIBLE);
      groupButtonsLayout.setVisibility(View.GONE);
    }
  }

  @Override public void setUpListeners() {
    joinGroupButton.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View v) {
        Group g = new Group(chosenHotspot);
        presenter.clickJoinGroup(g);
      }
    });

    createGroupButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        presenter.clickCreateGroup();
      }
    });
  }

  @Override public void showMobileDataDialog() {
    Dialog d = buildMobileDataDialog();
    d.show();
  }

  @Override public void showMobileDataToast() {
    Toast.makeText(HighwayActivity.this,
        HighwayActivity.this.getResources().getString(R.string.mDataJoinGroup), Toast.LENGTH_SHORT)
        .show();
  }

  @Override public void showJoinGroupResult(int result) {
    switch (result) {
      case ConnectionManager.ERROR_ON_RECONNECT:
        Toast.makeText(HighwayActivity.this,
            HighwayActivity.this.getResources().getString(R.string.errorJoiningNetwork),
            Toast.LENGTH_SHORT).show();
        //tag join unsuccess
        presenter.tagAnalyticsUnsuccessJoin();
        break;
      case ConnectionManager.ERROR_INVALID_GROUP:
        Toast.makeText(HighwayActivity.this,
            HighwayActivity.this.getResources().getString(R.string.noSelectedHotspot),
            Toast.LENGTH_SHORT).show();
        break;
      case ConnectionManager.ERROR_UNKNOWN:
        Toast.makeText(HighwayActivity.this,
            HighwayActivity.this.getResources().getString(R.string.unkownJoinNetError),
            Toast.LENGTH_SHORT).show();
        //tag join unsuccess
        presenter.tagAnalyticsUnsuccessJoin();
        break;
      case ConnectionManager.ERROR_MOBILE_DATA_ON_DIALOG:
        showMobileDataDialog();
        break;
      case ConnectionManager.ERROR_MOBILE_DATA_ON_TOAST:
        showMobileDataToast();
        break;
    }
  }

  private Dialog buildMobileDataDialog() {
    //        mobileDataDialog=true;

    AlertDialog.Builder builder;
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
      builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
    } else {
      builder = new AlertDialog.Builder(this);
    }
    builder.setTitle(this.getResources().getString(R.string.mobileDataTitle))
        .setMessage(this.getResources().getString(R.string.mobileDataMessage))
        .setPositiveButton(this.getResources().getString(R.string.turnOffButton),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
              }
            })
        .setNegativeButton(this.getResources().getString(R.string.cancel),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                System.out.println("Canceled the turn off data.");
              }
            });

    return builder.create();
  }

  @Override public void showCreateGroupResult(int result) {
    presenter.tagAnalyticsUnsuccessCreate();
    switch (result) {
      case ConnectionManager.FAILED_TO_CREATE_HOTSPOT:
        Toast.makeText(this, getResources().getString(R.string.couldNotCreateHotspot),
            Toast.LENGTH_SHORT).show();
        break;
      case ConnectionManager.ERROR_UNKNOWN:
        Toast.makeText(this, getResources().getString(R.string.unkownHotspotError),
            Toast.LENGTH_SHORT).show();
        break;
    }
  }

  @Override public void showEnablingWifiToast() {
    Toast.makeText(this, getResources().getString(R.string.enablingWifi), Toast.LENGTH_SHORT)
        .show();
  }

  @Override public void showInactivityToast() {
    Toast.makeText(this, getResources().getString(R.string.noHotspotYet), Toast.LENGTH_LONG).show();
  }

  @Override public void hideButtonsProgressBar() {
    buttonsProgressBar.setVisibility(View.INVISIBLE);
  }

  @Override public void openChatClient(String ipAddress, String deviceName,
      ArrayList<String> pathsFromOutsideShare) {
    System.out.println("Yes, wifi is connected.");
    Intent history =
        new Intent().setClass(HighwayActivity.this, HighwayTransferRecordActivity.class);
    Log.i("Highway Activity ", "Going to the list of Applications");
    history.putExtra("isAHotspot", false);
    history.putExtra("nickname", deviceName);
    //        IPAddress=intToIp(wifimanager.getDhcpInfo().serverAddress);
    System.out.println("this is the valor of the IPADDRESS : :::::::::::" + ipAddress);
    System.out.println("I am going to send this IP Address " + ipAddress);
    history.putExtra("targetIP", ipAddress);
    if (pathsFromOutsideShare != null) {
      //                    history.putExtra("pathFromOutsideShare", pathFromOutsideShare );
      Bundle tmp = new Bundle();
      tmp.putStringArrayList("pathsFromOutsideShare",
          pathsFromOutsideShare);//change listOfAppsToInstall to listOfAppsTOSend
      history.putExtra("bundle", tmp);
      history.setAction("ShareFromOutsideRequest");
    }
    //        try{
    ////            unregisterReceiver(this);
    ////            unregisterReceiver(wifireceiver);
    //        }catch (IllegalArgumentException e){
    //            System.out.println("There was an error while trying to unregister the wifireceiver and the wifireceiverforconnectingwifi");
    //        }
    startActivity(history);
  }

  @Override public void openChatHotspot(ArrayList<String> pathsFromOutsideShare) {
    Intent history =
        new Intent().setClass(HighwayActivity.this, HighwayTransferRecordActivity.class);
    System.out.println("Highway activity : going to start the transferRecordActivity !!!!");
    history.putExtra("isAHotspot", true);//vou precisar disto no appselection - e agora ?
    history.putExtra("nickname", deviceName);
    if (pathsFromOutsideShare != null) {
      Bundle tmp = new Bundle();
      tmp.putStringArrayList("pathsFromOutsideShare",
          pathsFromOutsideShare);//change listOfAppsToInstall to listOfAppsTOSend
      history.putExtra("bundle", tmp);
      history.setAction("ShareFromOutsideHotspot");
    }
    startActivity(history);
  }

  @Override public void refreshRadar(ArrayList<String> clients) {
    radarTextView.show(clients);
  }

  @Override public void refreshRadarLowerVersions(ArrayList<String> clients) {
    radarTextView.showForLowerVersions(clients);
  }

  public void joinSingleHotspot() {
    Group g = new Group(chosenHotspot);
    presenter.clickJoinGroup(g);
  }

  public String getChosenHotspot() {
    return chosenHotspot;
  }

  public void setChosenHotspot(String chosenHotspot) {
    this.chosenHotspot = chosenHotspot;
  }

  @Override protected void onResume() {
    presenter.onResume();
    //        registerReceiver(activateButtons,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    //        registerReceiver(wifireceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    //        registerReceiver(wfr,new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    super.onResume();
  }

  @Override protected void onDestroy() {
    connectionManager.cleanNetworks();
    presenter.onDestroy();
    //        try{
    ////            unregisterReceiver(wifireceiver);
    ////            unregisterReceiver(wfr);
    //        }catch( IllegalArgumentException e){
    //            System.out.println("Tried to to unregister a receiver that was already unregistered");
    //        }

    super.onDestroy();
  }

  public boolean isJoinGroupFlag() {
    return joinGroupFlag;
  }

  public void setJoinGroupFlag(boolean joinGroupFlag) {
    this.joinGroupFlag = joinGroupFlag;
  }
}
