package cm.aptoide.pt.spotandshareandroid;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.spotandshareandroid.analytics.SpotAndShareAnalyticsInterface;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 18-07-2016.
 */
public class HighwayActivity extends ActivityView implements HighwayView, PermissionManager {

  public static final int MOBILE_DATA_REQUEST_CODE = 0010;
  private static final int PERMISSION_REQUEST_CODE = 6531;
  private static final int WRITE_SETTINGS_REQUEST_CODE = 5;
  private static final int LOCATION_REQUEST_CODE = 789;
  public String deviceName;
  public LinearLayout createGroupButton;
  public HighwayRadarTextView radarTextView;
  public LinearLayout progressBarLayout;
  public String chosenHotspot = "";
  public LinearLayout groupButtonsLayout;
  private TextView searchGroupsTextview;
  private Toolbar mToolbar;
  private ProgressBar buttonsProgressBar;//progress bar for when user click the buttons

  private boolean outsideShare = false;
  private List<String> pathsFromOutsideShare;
  private boolean joinGroupFlag;
  private HighwayPresenter presenter;
  private ConnectionManager connectionManager;
  private SpotAndShareAnalyticsInterface analytics;
  private GroupManager groupManager;
  private OutsideShareManager outsideShareManager;
  private PermissionListener permissionListener;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ApplicationSender.reset();
    DataHolder.reset();

    deviceName = getIntent().getStringExtra("deviceName");
    connectionManager = ConnectionManager.getInstance(this.getApplicationContext());
    analytics = ShareApps.getAnalytics();
    groupManager = new GroupManager(connectionManager);

    setContentView(R.layout.highway_activity);

    mToolbar = (Toolbar) findViewById(R.id.shareAppsToolbar);
    setUpToolbar();
    HighwayRadarScan radar = (HighwayRadarScan) findViewById(R.id.radar);
    radarTextView = (HighwayRadarTextView) findViewById(R.id.hotspotTextView);
    searchGroupsTextview = (TextView) findViewById(R.id.searching_groups);
    progressBarLayout = (LinearLayout) findViewById(R.id.circular_progress_bar);
    groupButtonsLayout = (LinearLayout) findViewById(R.id.groupButtonsLayout);

    buttonsProgressBar = (ProgressBar) findViewById(R.id.buttonsProgressBar);
    createGroupButton = (LinearLayout) findViewById(R.id.createGroup);//receive
    radarTextView.setActivity(this);

    presenter = new HighwayPresenter(this, deviceName, new DeactivateHotspotTask(connectionManager),
        connectionManager, analytics, groupManager, this);
    attachPresenter(presenter);

    if (getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_SEND)) {

      outsideShareManager = new OutsideShareManager();
      presenter.setOutsideShareManager(outsideShareManager);
      Uri uri = (Uri) getIntent().getExtras().get("android.intent.extra.STREAM");
      presenter.getAppFilePathFromOutside(uri);
    } else if (getIntent().getAction() != null && getIntent().getAction()
        .equals(Intent.ACTION_SEND_MULTIPLE)) {

      outsideShareManager = new OutsideShareManager();
      presenter.setOutsideShareManager(outsideShareManager);
      ArrayList<Uri> uriList =
          (ArrayList<Uri>) getIntent().getExtras().get("android.intent.extra.STREAM");
      presenter.getMultipleAppFilePathsFromOutside(uriList);
    }
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

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();

    finish();

    return super.onOptionsItemSelected(item);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == WRITE_SETTINGS_REQUEST_CODE) {
      if (checkSpecialSettingsPermission()) {
        analytics.specialSettingsGranted();
        if (checkLocationPermission()) {
          onPermissionsGranted();
        } else {
          askForLocationPermission();
        }
      } else {
        analytics.specialSettingsDenied();
        onPermissionsDenied();
      }
    } else if (requestCode == MOBILE_DATA_REQUEST_CODE) {
      Group group = new Group(chosenHotspot);
      presenter.onActivityResult(group);
    } else if (requestCode == LOCATION_REQUEST_CODE) {
      if (checkLocationPermission()) {
        onPermissionsGranted();
      } else {
        onPermissionsDenied();
      }
    }
  }

  @Override public void onBackPressed() {

    recoverNetworkState();
    super.onBackPressed();
  }

  private void recoverNetworkState() {
    presenter.recoverNetworkState();
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    outsideShare = false;

    if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)) {

      outsideShareManager = new OutsideShareManager();
      presenter.setOutsideShareManager(outsideShareManager);
      Uri uri = (Uri) intent.getExtras().get("android.intent.extra.STREAM");
      presenter.getAppFilePathFromOutside(uri);
    } else if (intent.getAction() != null && intent.getAction()
        .equals(Intent.ACTION_SEND_MULTIPLE)) {
      outsideShareManager = new OutsideShareManager();
      presenter.setOutsideShareManager(outsideShareManager);
      ArrayList<Uri> uriList =
          (ArrayList<Uri>) intent.getExtras().get("android.intent.extra.STREAM");
      presenter.getMultipleAppFilePathsFromOutside(uriList);
    } else if (intent.getAction() != null && intent.getAction().equals("LEAVINGSHAREAPPSCLIENT")) {
      recoverNetworkState();
      forgetAPTXVNetwork();
    }
  }

  private void forgetAPTXVNetwork() {
    presenter.forgetAPTXVNetwork();
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case PERMISSION_REQUEST_CODE: {

        if (checkNormalPermissions()) {
          if (!checkSpecialSettingsPermission()) {
            requestSpecialSettingsPermission();
          } else if (!checkLocationPermission()) {
            askForLocationPermission();
          } else {
            onPermissionsGranted();
          }
        } else {
          onPermissionsDenied();
        }
        break;
      }
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private boolean checkNormalPermissions() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        != PackageManager.PERMISSION_GRANTED) {
      return false;
    }

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      return false;
    }

    return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        == PackageManager.PERMISSION_GRANTED;
  }

  private void requestSpecialSettingsPermission() {
    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
    intent.setData(Uri.parse("package:" + getPackageName()));
    startActivityForResult(intent, WRITE_SETTINGS_REQUEST_CODE);
  }

  @TargetApi(23) private boolean checkSpecialSettingsPermission() {
    return Settings.System.canWrite(this);
  }

  private boolean checkLocationPermission() {
    int locationMode = 0;
    String locationProviders;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      try {
        locationMode =
            Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
      } catch (Settings.SettingNotFoundException e) {
        e.printStackTrace();
        return false;
      }

      return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    } else {
      locationProviders = Settings.Secure.getString(this.getContentResolver(),
          Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
      return !TextUtils.isEmpty(locationProviders);
    }
  }

  private void onPermissionsGranted() {
    if (permissionListener != null) {
      permissionListener.onPermissionGranted();
    }
  }

  private void askForLocationPermission() {//old requestLocationPermission
    Dialog d = buildLocationPermissionDialog();
    d.show();
  }

  private void onPermissionsDenied() {
    if (permissionListener != null) {
      permissionListener.onPermissionDenied();
    }
  }

  private Dialog buildLocationPermissionDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle(this.getResources().getString(R.string.warning))
        .setMessage(this.getResources().getString(R.string.locationDialog))
        .setPositiveButton(this.getResources().getString(R.string.turn_on),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                requestLocationPermission();
              }
            })
        .setNegativeButton(this.getResources().getString(R.string.cancel),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                onPermissionsDenied();
              }
            });
    return builder.create();
  }

  private void requestLocationPermission() {
    Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    startActivityForResult(locationIntent, LOCATION_REQUEST_CODE);
  }

  @Override public void showConnections() {
    presenter.scanNetworks();
  }

  @Override public void enableButtons(boolean enable) {
    if (enable) {
      progressBarLayout.setVisibility(View.GONE);
      groupButtonsLayout.setVisibility(View.VISIBLE);
      System.out.println("Activating the buttons !");
    } else {//disable
      progressBarLayout.setVisibility(View.VISIBLE);
      groupButtonsLayout.setVisibility(View.GONE);
    }
  }

  @Override public void setUpListeners() {

    createGroupButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
          hideSearchGroupsTextview(true);
          presenter.clickCreateGroup();
        } else {
          showNougatErrorToast();
        }
      }
    });
  }

  private void showNougatErrorToast() {
    Toast.makeText(this, this.getResources().getString(R.string.hotspotCreationErrorNougat),
        Toast.LENGTH_SHORT).show();
  }

  @Override public boolean checkPermissions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (!checkNormalPermissions()) {
        return false;
      }

      if (!checkSpecialSettingsPermission()) {
        return false;
      }

      if (!checkLocationPermission()) {
        return false;
      }
    }
    return true;
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

    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle(this.getResources().getString(R.string.mobileDataTitle))
        .setMessage(this.getResources().getString(R.string.mobileDataMessage))
        .setPositiveButton(this.getResources().getString(R.string.turnOffButton),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_SETTINGS),
                    MOBILE_DATA_REQUEST_CODE);
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
    System.out.println("this is the valor of the IPADDRESS : :::::::::::" + ipAddress);
    System.out.println("I am going to send this IP Address " + ipAddress);
    history.putExtra("targetIP", ipAddress);
    if (pathsFromOutsideShare != null) {
      Bundle tmp = new Bundle();
      tmp.putStringArrayList("pathsFromOutsideShare", pathsFromOutsideShare);
      history.putExtra("bundle", tmp);
      history.setAction("ShareFromOutsideRequest");
    }

    startActivity(history);

    finish();
  }

  @Override
  public void openChatHotspot(ArrayList<String> pathsFromOutsideShare, String deviceName) {
    Intent history =
        new Intent().setClass(HighwayActivity.this, HighwayTransferRecordActivity.class);
    System.out.println("Highway activity : going to start the transferRecordActivity !!!!");
    history.putExtra("isAHotspot", true);
    history.putExtra("nickname", deviceName);
    if (pathsFromOutsideShare != null) {
      Bundle tmp = new Bundle();
      tmp.putStringArrayList("pathsFromOutsideShare", pathsFromOutsideShare);
      history.putExtra("bundle", tmp);
      history.setAction("ShareFromOutsideHotspot");
    }
    startActivity(history);
    finish();
  }

  @Override public void refreshRadar(ArrayList<String> clients) {
    radarTextView.show(clients);
  }

  @Override public void refreshRadarLowerVersions(ArrayList<String> clients) {
    radarTextView.showForLowerVersions(clients);
  }

  @Override public void showRecoveringWifiStateToast() {
    Toast.makeText(this, this.getResources().getString(R.string.recoveringWifiState),
        Toast.LENGTH_SHORT).show();
  }

  @Override public void dismiss() {
    finish();
  }

  @Override public void hideSearchGroupsTextview(boolean hide) {
    if (hide) {
      searchGroupsTextview.setVisibility(View.GONE);
    } else {
      searchGroupsTextview.setVisibility(View.VISIBLE);
    }
  }

  public void joinSingleHotspot() {
    hideSearchGroupsTextview(true);
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
    super.onResume();
  }

  @Override protected void onDestroy() {
    presenter.onDestroy();
    super.onDestroy();
  }

  public boolean isJoinGroupFlag() {
    return joinGroupFlag;
  }

  @Override public void requestPermissions() {
    if (!checkPermissions() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      final List<String> missingPermissions = new ArrayList<>();

      if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
          != PackageManager.PERMISSION_GRANTED) {

        missingPermissions.add(Manifest.permission.READ_PHONE_STATE);
      }

      if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
          != PackageManager.PERMISSION_GRANTED) {
        missingPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
      }

      if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
          != PackageManager.PERMISSION_GRANTED) {

        missingPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
      }

      if (missingPermissions.size() > 0) {

        String[] permissionsToRequest = new String[missingPermissions.size()];
        permissionsToRequest = missingPermissions.toArray(permissionsToRequest);

        ActivityCompat.requestPermissions(this, permissionsToRequest, PERMISSION_REQUEST_CODE);
      } else {
        // All normal permissions granted
        if (!checkSpecialSettingsPermission()) {
          requestSpecialSettingsPermission();
        } else if (!checkLocationPermission()) {
          askForLocationPermission();
        } else {
          onPermissionsGranted();
        }
      }
    } else {
      onPermissionsGranted();
    }
  }

  public void setJoinGroupFlag(boolean joinGroupFlag) {
    this.joinGroupFlag = joinGroupFlag;
  }

  @Override public void registerListener(PermissionListener listener) {
    this.permissionListener = listener;
  }

  @Override public void removeListener() {
    this.permissionListener = null;
  }


}
