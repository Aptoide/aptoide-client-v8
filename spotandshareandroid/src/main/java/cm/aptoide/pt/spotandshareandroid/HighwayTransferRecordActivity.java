package cm.aptoide.pt.spotandshareandroid;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HighwayTransferRecordActivity extends ActivityView
    implements HighwayTransferRecordView {

  private static final int SELECT_APPS_REQUEST_CODE = 53110;
  private static List<HighwayTransferRecordItem> listOfItems = new ArrayList<>();
  private boolean isHotspot;
  private String targetIPAddress;
  private String nickname;
  private int porto = 55555;
  private LinearLayout send;
  private LinearLayout clearHistory;
  private TextView welcomeText;
  private HighwayTransferRecordCustomAdapter adapter;
  private TextView textView;
  private ListView receivedAppListView;
  private WifiManager wifimanager;
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

    welcomeText = (TextView) findViewById(R.id.Transf_rec_firstRow);
    receivedAppListView = (ListView) findViewById(R.id.transferRecordListView);
    textView = (TextView) findViewById(R.id.noRecordsTextView);
    send = (LinearLayout) findViewById(R.id.TransferRecordSendLayout);
    clearHistory = (LinearLayout) findViewById(R.id.TransferRecordClearLayout);
    mToolbar = (Toolbar) findViewById(R.id.shareAppsToolbar);

    analytics = ShareApps.getAnalytics();

    setUpToolbar();

    isHotspot = getIntent().getBooleanExtra("isAHotspot", false);
    targetIPAddress = getIntent().getStringExtra("targetIP");
    nickname = getIntent().getStringExtra("nickname");

    if (isHotspot) {
      setTransparencySend(true);
      welcomeText.setText(this.getResources()
          .getString(R.string.created_group, nickname));
      setTextViewMessage(false);
    } else {
      welcomeText.setText(this.getResources()
          .getString(R.string.joined_group, nickname));
      setTextViewMessage(true);
    }
    setTransparencyClearHistory(true);

    receivedAppListView.setVisibility(View.GONE);

    applicationsManager = new ApplicationsManager(this);

    if ("APPVIEW_SHARE".equals(getIntent().getAction())) {
      String filepath = getIntent().getStringExtra("autoShareFilePath");
      applicationReceiver =
          new ApplicationReceiver(getApplicationContext(), isHotspot, porto, targetIPAddress,
              nickname, filepath);
    } else {
      applicationReceiver =
          new ApplicationReceiver(getApplicationContext(), isHotspot, porto, targetIPAddress,
              nickname);
    }

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

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    setIntent(intent);
    isHotspot = getIntent().getBooleanExtra("isHotspot", false);

    textView.setVisibility(View.GONE);
    receivedAppListView.setVisibility(View.VISIBLE);

    if (adapter == null) {
      adapter = new HighwayTransferRecordCustomAdapter(this, listOfItems);
      receivedAppListView.setAdapter(adapter);
    } else {
      adapter.notifyDataSetChanged();
    }
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

      builder.setTitle(this.getResources()
          .getString(R.string.alert))
          .setMessage(this.getResources()
              .getString(R.string.alertCreatorLeave))
          .setPositiveButton(this.getResources()
              .getString(R.string.leave), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              sendServerShutdownMessage();
              presenter.listenToDisconnect();
            }
          })
          .setNegativeButton(this.getResources()
              .getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              // User cancelled the dialog
            }
          });
      return builder.create();
    } else {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);

      builder.setTitle(this.getResources()
          .getString(R.string.alert))
          .setMessage(this.getResources()
              .getString(R.string.alertClientLeave))
          .setPositiveButton(this.getResources()
              .getString(R.string.leave), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              presenter.listenToDisconnect();
              sendDisconnectMessage();
            }
          })
          .setNegativeButton(this.getResources()
              .getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

  @Override public void openAppSelectionView() {
    Intent appSelection = new Intent().setClass(HighwayTransferRecordActivity.this,
        HighwayAppSelectionActivity.class);
    Log.i("TransferRecordActivity ", "going to start the app selection activity");
    System.out.println("The is hotspot boolean is : " + isHotspot);
    appSelection.putExtra("isAHotspot", isHotspot);
    startActivityForResult(appSelection, SELECT_APPS_REQUEST_CODE);
  }

  @Override public void showNoRecordsToDeleteToast() {
    Toast.makeText(this, HighwayTransferRecordActivity.this.getResources()
        .getString(R.string.noRecordsDelete), Toast.LENGTH_SHORT)
        .show();
  }

  @Override public void showDeleteHistoryDialog() {
    Dialog d = createDialogToDelete();
    d.show();
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

  @Override public void showDialogToInstall(String appName, String filePath, String packageName) {
    Dialog dialog = createDialogToInstall(appName, filePath, packageName);
    dialog.show();
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
    Toast.makeText(this, R.string.generalError, Toast.LENGTH_LONG)
        .show();
  }

  @Override public void showRecoveringWifiStateToast() {
    Toast.makeText(this, this.getResources()
        .getString(R.string.recoveringWifiState), Toast.LENGTH_SHORT)
        .show();
  }

  @Override public void dismiss() {
    finish();
  }

  @Override public void showServerLeftMessage() {
    Toast.makeText(this, this.getResources()
        .getString(R.string.groupCreatorLeft), Toast.LENGTH_SHORT)
        .show();
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
    Method[] methods = wifimanager.getClass()
        .getDeclaredMethods();
    WifiConfiguration wc = DataHolder.getInstance()
        .getWcOnJoin();
    for (Method m : methods) {
      if (m.getName()
          .equals("setWifiApConfiguration")) {

        try {
          Method setConfigMethod = wifimanager.getClass()
              .getMethod("setWifiApConfiguration", WifiConfiguration.class);
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
      if (m.getName()
          .equals("setWifiApEnabled")) {

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

  public Dialog createDialogToDelete() {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle(this.getResources()
        .getString(R.string.warning))
        .setMessage(this.getResources()
            .getString(R.string.clear_history_warning))
        .setPositiveButton(this.getResources()
            .getString(R.string.delete), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {

            setTransparencyClearHistory(true);
            presenter.deleteAllApps();
          }
        })
        .setNegativeButton(this.getResources()
            .getString(R.string.cancel), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            // User cancelled the dialog
            System.out.println(
                "TransferREcordsCustomAdapter : Person pressed the CANCEL BUTTON !!!!!!!! ");
          }
        });
    return builder.create();
  }

  public Dialog createInstallErrorDialog(String appName) {
    String message =
        String.format(getResources().getString(R.string.errorAppVersionNew), appName, appName);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setMessage(message);
    builder.setPositiveButton(this.getResources()
        .getString(R.string.ok), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        System.out.println("Pressed OK in the error of the app version");
      }
    });

    AlertDialog dialog = builder.create();
    return dialog;
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
