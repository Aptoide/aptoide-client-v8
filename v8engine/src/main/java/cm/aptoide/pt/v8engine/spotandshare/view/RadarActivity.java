package cm.aptoide.pt.v8engine.spotandshare.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
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
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.spotandshare.DataHolder;
import cm.aptoide.pt.v8engine.spotandshare.PermissionListener;
import cm.aptoide.pt.v8engine.spotandshare.PermissionManager;
import cm.aptoide.pt.v8engine.spotandshare.ShareApps;
import cm.aptoide.pt.v8engine.spotandshare.analytics.SpotAndShareAnalyticsInterface;
import cm.aptoide.pt.v8engine.spotandshare.connection.ConnectionManager;
import cm.aptoide.pt.v8engine.spotandshare.connection.DeactivateHotspotTask;
import cm.aptoide.pt.v8engine.spotandshare.group.Group;
import cm.aptoide.pt.v8engine.spotandshare.group.GroupManager;
import cm.aptoide.pt.v8engine.spotandshare.group.GroupNameProvider;
import cm.aptoide.pt.v8engine.spotandshare.presenter.RadarPresenter;
import cm.aptoide.pt.v8engine.spotandshare.presenter.RadarView;
import cm.aptoide.pt.v8engine.spotandshare.transference.ApplicationSender;
import cm.aptoide.pt.v8engine.spotandshare.view.radar.RadarScan;
import cm.aptoide.pt.v8engine.spotandshare.view.radar.RadarTextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 18-07-2016.
 */
public class RadarActivity extends SpotAndShareActivityView implements RadarView, PermissionManager {

  public static final String HOTSPOT_NAME = "HOTSPOT_NAME";
  private static final int PERMISSION_REQUEST_CODE = 6531;
  private static final int WRITE_SETTINGS_REQUEST_CODE = 5;
  private static final int LOCATION_REQUEST_CODE = 789;
  public LinearLayout createGroupButton;
  public RadarTextView radarTextView;
  public LinearLayout progressBarLayout;
  public LinearLayout groupButtonsLayout;
  private TextView searchGroupsTextview;
  private Toolbar mToolbar;
  private ProgressBar buttonsProgressBar;//progress bar for when user click the buttons
  private TextView shareAptoideApkButton;

  private RadarPresenter presenter;
  private SpotAndShareAnalyticsInterface analytics;
  private GroupManager groupManager;
  private PermissionListener permissionListener;
  private String autoShareFilepath;
  private String autoShareAppName;

  public static Intent buildIntent(Context context, String filepath, String appNameToShare) {
    Intent intent = new Intent(context, RadarActivity.class);
    intent.setAction("APPVIEW_SHARE");
    intent.putExtra("APPVIEW_SHARE_FILEPATH", filepath);
    intent.putExtra("APPVIEW_SHARE_APPNAME", appNameToShare);
    return intent;
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ApplicationSender.reset();
    DataHolder.reset();
    GroupNameProvider groupNameProvider = ((V8Engine) getApplication()).getGroupNameProvider();
    ConnectionManager connectionManager =
        ConnectionManager.getInstance(this.getApplicationContext());
    analytics = ShareApps.getAnalytics();
    groupManager = new GroupManager(connectionManager);

    setContentView(R.layout.activity_spot_and_share_radar);

    bindViews();
    setupViews();

    Intent intent = getIntent();
    if (intent.getAction() != null && intent.getAction()
        .equals("APPVIEW_SHARE")) {
      enableButtons(false);
      autoShareAppName = intent.getStringExtra("APPVIEW_SHARE_APPNAME");
      autoShareFilepath = intent.getStringExtra("APPVIEW_SHARE_FILEPATH");
      presenter =
          new RadarPresenter(this, groupNameProvider, new DeactivateHotspotTask(connectionManager),
              connectionManager, analytics, groupManager, this, autoShareAppName, autoShareFilepath,
              false);
    } else {
      presenter =
          new RadarPresenter(this, groupNameProvider, new DeactivateHotspotTask(connectionManager),
              connectionManager, analytics, groupManager, this, true);
    }

    attachPresenter(presenter);
  }

  private void bindViews() {
    mToolbar = (Toolbar) findViewById(R.id.shareAppsToolbar);
    setUpToolbar();
    RadarScan radar = (RadarScan) findViewById(R.id.radar);
    radarTextView = (RadarTextView) findViewById(R.id.hotspotTextView);
    searchGroupsTextview = (TextView) findViewById(R.id.searching_groups);
    progressBarLayout = (LinearLayout) findViewById(R.id.circular_progress_bar);
    groupButtonsLayout = (LinearLayout) findViewById(R.id.groupButtonsLayout);

    buttonsProgressBar = (ProgressBar) findViewById(R.id.buttonsProgressBar);
    createGroupButton = (LinearLayout) findViewById(R.id.createGroup);

    shareAptoideApkButton = (TextView) findViewById(R.id.share_aptoide_apk_button);
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

  private void forgetAPTXVNetwork() {
    presenter.forgetAPTXVNetwork();
  }

  private void recoverNetworkState() {
    presenter.recoverNetworkState();
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

    builder.setTitle(this.getResources()
        .getString(R.string.spotandshare_title_warning_dialog))
        .setMessage(this.getResources()
            .getString(R.string.locationDialog))
        .setPositiveButton(this.getResources()
            .getString(R.string.turn_on), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            requestLocationPermission();
          }
        })
        .setNegativeButton(this.getResources()
                .getString(R.string.spotandshare_button_cancel_option_dialog),
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
      progressBarLayout.setVisibility(android.view.View.GONE);
      groupButtonsLayout.setVisibility(android.view.View.VISIBLE);
    } else {//disable
      progressBarLayout.setVisibility(android.view.View.VISIBLE);
      groupButtonsLayout.setVisibility(android.view.View.GONE);
    }
  }

  @Override public void setupViews() {
    radarTextView.setOnHotspotClickListener(new RadarTextView.HotspotClickListener() {
      @Override public void onGroupClicked(Group group) {
        presenter.clickedOnGroup(group);
      }
    });

    createGroupButton.setOnClickListener(new android.view.View.OnClickListener() {
      @Override public void onClick(android.view.View v) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
          hideSearchGroupsTextview(true);
          presenter.clickCreateGroup();
        } else {
          showNougatErrorToast();
        }
      }
    });

    shareAptoideApkButton.setOnClickListener(new android.view.View.OnClickListener() {
      @Override public void onClick(android.view.View v) {
        presenter.clickShareAptoide();
      }
    });
  }

  @Override public void showJoinGroupResult(int result) {
    switch (result) {
      case ConnectionManager.ERROR_ON_RECONNECT:
        Toast.makeText(RadarActivity.this, RadarActivity.this.getResources()
            .getString(R.string.errorJoiningNetwork), Toast.LENGTH_SHORT)
            .show();
        //tag join unsuccess
        presenter.tagAnalyticsUnsuccessJoin();
        break;
      case ConnectionManager.ERROR_INVALID_GROUP:
        Toast.makeText(RadarActivity.this, RadarActivity.this.getResources()
            .getString(R.string.noSelectedHotspot), Toast.LENGTH_SHORT)
            .show();
        break;
      case ConnectionManager.ERROR_UNKNOWN:
        Toast.makeText(RadarActivity.this, RadarActivity.this.getResources()
            .getString(R.string.unkownJoinNetError), Toast.LENGTH_SHORT)
            .show();
        //tag join unsuccess
        presenter.tagAnalyticsUnsuccessJoin();
        break;
    }
  }

  @Override public void showCreateGroupResult(int result) {
    presenter.tagAnalyticsUnsuccessCreate();
    switch (result) {
      case ConnectionManager.FAILED_TO_CREATE_HOTSPOT:
        Toast.makeText(this, getResources().getString(R.string.couldNotCreateHotspot),
            Toast.LENGTH_SHORT)
            .show();
        break;
      case ConnectionManager.ERROR_UNKNOWN:
        Toast.makeText(this, getResources().getString(R.string.unkownHotspotError),
            Toast.LENGTH_SHORT)
            .show();
        break;
    }
  }

  @Override public void showInactivityToast() {
    Toast.makeText(this, getResources().getString(R.string.noHotspotYet), Toast.LENGTH_LONG)
        .show();
  }

  @Override public void hideButtonsProgressBar() {
    buttonsProgressBar.setVisibility(android.view.View.INVISIBLE);
  }

  @Override public void openChatClient(String ipAddress, String deviceName,
      ArrayList<String> pathsFromOutsideShare) {
    Intent history = new Intent().setClass(RadarActivity.this, TransferRecordActivity.class);
    history.putExtra("isAHotspot", false);
    history.putExtra("nickname", deviceName);
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
    Intent history = new Intent().setClass(RadarActivity.this, TransferRecordActivity.class);
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

  @Override public void refreshRadar(ArrayList<Group> clients) {
    radarTextView.show(clients);
  }

  @Override public void showRecoveringWifiStateToast() {
    Toast.makeText(this, this.getResources()
        .getString(R.string.recoveringWifiState), Toast.LENGTH_SHORT)
        .show();
  }

  @Override public void dismiss() {
    finish();
  }

  @Override public void hideSearchGroupsTextview(boolean hide) {
    if (hide) {
      searchGroupsTextview.setVisibility(android.view.View.GONE);
    } else {
      searchGroupsTextview.setVisibility(android.view.View.VISIBLE);
    }
  }

  @Override public void openChatFromAppViewShare(String deviceName, String appFilepath) {
    Intent intent = new Intent().setClass(RadarActivity.this, TransferRecordActivity.class);
    intent.putExtra("isAHotspot", true);
    intent.putExtra("nickname", deviceName);
    intent.putExtra("autoShareFilePath", appFilepath);
    intent.setAction("APPVIEW_SHARE");
    startActivity(intent);
    finish();
  }

  @Override public void paintSelectedGroup(Group group) {
    radarTextView.selectGroup(group);
  }

  @Override public void deselectHotspot(Group group) {
    radarTextView.deselectHotspot(group);
  }

  @Override public void openShareAptoide(String Ssid) {
    Intent intent = new Intent(RadarActivity.this, ShareAptoideActivity.class);
    intent.putExtra(HOTSPOT_NAME, Ssid);
    startActivity(intent);
    finish();
  }

  @Override public void showShareAptoideApk() {
    shareAptoideApkButton.setVisibility(android.view.View.VISIBLE);
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

  @Override public void registerListener(PermissionListener listener) {
    this.permissionListener = listener;
  }

  @Override public void removeListener() {
    this.permissionListener = null;
  }

  private void showNougatErrorToast() {
    Toast.makeText(this, this.getResources()
        .getString(R.string.spotandshare_message_create_group_error_for_nougat), Toast.LENGTH_SHORT)
        .show();
  }

  @Override protected void onResume() {
    presenter.onResume();
    super.onResume();
  }

  @Override protected void onDestroy() {
    presenter.onDestroy();
    super.onDestroy();
    radarTextView.stop();
    radarTextView = null;
  }
}
