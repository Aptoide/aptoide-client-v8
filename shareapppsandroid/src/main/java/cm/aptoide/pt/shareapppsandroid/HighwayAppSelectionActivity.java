package cm.aptoide.pt.shareapppsandroid;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class HighwayAppSelectionActivity extends ActivityView implements HighwayAppSelectionView {

  private static final int porto = 55555;//12341
  public List<App> gridViewAppItemsList = new ArrayList<App>();
  private PackageManager packageManager;
  private HighwayAppSelectionActivity highwayAppSelectionActivity;
  private String targetIPAddress;
  private boolean isHotspot;
  private HighwayAppSelectionCustomAdapter adapter;
  private List<App> listOfSelectedApps = new ArrayList<>();
  private Button sendButton;
  private GridView gridView;
  private String nickname;
  private ProgressBar progressBar;
  private HighwayAppSelectionPresenter presenter;
  private ApplicationProvider applicationProvider;
  private ApplicationSender applicationSender;
  private android.support.v7.widget.Toolbar mToolbar;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.highway_appselection_activity);
    gridView = (GridView) findViewById(R.id.HighwayGridView);
    progressBar = (ProgressBar) findViewById(R.id.appSelectionProgressBar);
    mToolbar= (android.support.v7.widget.Toolbar) findViewById(R.id.shareAppsToolbar);

    gridView.setSelector(new ColorDrawable(Color.BLACK));

    sendButton = (Button) findViewById(R.id.sendButton);

    isHotspot = getIntent().getBooleanExtra("isAHotspot", false);
    setUpToolbar();
    highwayAppSelectionActivity = this;

    applicationProvider = new ApplicationProvider(this);
    applicationSender = new ApplicationSender(this, isHotspot);

    presenter =
        new HighwayAppSelectionPresenter(applicationProvider, applicationSender, this, isHotspot);
    attachPresenter(presenter);

    //        new initializeUITask().execute();

  }

  //    public void getInstalledApps() {
  //        packageManager = getPackageManager();
  //
  //        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
  //
  //        for (ApplicationInfo applicationInfo : packages) {
  //
  //            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && applicationInfo.packageName != null) {
  //                String obbsfilepath = checkIfHasObb(applicationInfo.packageName);
  //                System.out.println("obbs is : " + obbsfilepath);
  ////                if(!obbs){
  //
  //                App aux = new App(applicationInfo.loadIcon(packageManager), applicationInfo.loadLabel(packageManager).toString(), applicationInfo.packageName, applicationInfo.sourceDir, "inside");
  //                aux.setObbsFilePath(obbsfilepath);
  //                if (!gridViewAppItemsList.contains(aux)) {
  //                    gridViewAppItemsList.add(aux);
  //                }
  ////                }
  //
  //
  //            }
  //        }
  //    }

  @Override public void onBackPressed() {

    gridViewAppItemsList.clear();
    System.out.println("Cleaned the list of apps");
    super.onBackPressed();
  }

  @Override protected void onResume() {
    super.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    adapter.removeListener();
    presenter.onDestroy();
    super.onDestroy();
  }

  //
  //    public String checkIfHasObb(String appName) {
  //        boolean hasObb = false;
  //        String obbsFilePath = "noObbs";
  //        String obbPath = Environment.getExternalStoragePublicDirectory("/") + "/Android/Obb/";
  //        File obbFolder = new File(obbPath);
  //        File[] list = obbFolder.listFiles();
  //        if (list != null) {
  //            System.out.println("list lenght is : " + list.length);
  //            if (list.length > 0) {
  //                System.out.println("appName is : " + appName);
  //                for (int i = 0; i < list.length; i++) {
  //                    System.out.println("List get name is : " + list[i].getName());
  //                    if (list[i].getName().equals(appName)) {
  //                        hasObb = true;
  //                        obbsFilePath = list[i].getAbsolutePath();
  //                    }
  //                }
  //            }
  //        }
  //        return obbsFilePath;
  //    }

  private void setUpToolbar() {
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
//      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayShowTitleEnabled(true);
      getSupportActionBar().setTitle(getResources().getString(R.string.shareApps));
    }
  }

  @Override public void setUpSendListener() {
    sendButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        presenter.clickedSendButton();
      }
    });
  }

  @Override public void showNoAppsSelectedToast() {
    Toast.makeText(HighwayAppSelectionActivity.this,
        HighwayAppSelectionActivity.this.getResources().getString(R.string.noSelectedAppsToSend),
        Toast.LENGTH_SHORT).show();
  }

  //@Override public void sendMultipleFiles(List<App> list) {
  //  Intent sendIntent = null;
  //  if (isHotspot) {
  //    System.out.println(
  //        "HIGHWAY APP SELECTION ACTIVITY I will try to send a message and i am a hostpot");
  //    sendIntent = new Intent(this, HighwayServerService.class);
  //    //como sou servidor devo meter o firstSender a ""
  //  } else {
  //    System.out.println(
  //        "HIGHWAY APP SELECTION ACTIVITY I will try to send a message and i am NOT NOT NOT NOT a hotspot");
  //    sendIntent = new Intent(this, HighwayClientService.class);
  //    sendIntent.putExtra("targetIP", targetIPAddress);
  //  }
  //  sendIntent.putExtra("port", porto);
  //  System.out.println("App selection activity  : o bool do isHotspot : " + isHotspot);
  //  sendIntent.putExtra("isHotspot", isHotspot);
  //
  //  //        sendIntent.putExtra("fromOutside",false);
  //
  //  Bundle tmp = new Bundle();
  //  tmp.putParcelableArrayList("listOfAppsToInstall",
  //      new ArrayList<Parcelable>(list));//change listOfAppsToInstall to listOfAppsTOSend
  //  sendIntent.putExtra("bundle", tmp);
  //  sendIntent.setAction("SEND");
  //  startService(sendIntent);
  //}

  @Override public void enableGridView(boolean enable) {
    progressBar.setVisibility(View.GONE);
    gridView.setVisibility(View.VISIBLE);
  }

  @Override public void generateAdapter(boolean isHotspot, List<AppViewModel> itemList) {
    if (isHotspot) {
      System.out.println("Estou no true do ser hotspot, na app selection.");
      adapter = new HighwayAppSelectionCustomAdapter(this, gridView.getContext(), itemList, true);
    } else {
      System.out.println(
          " hIGHWAY APP SELECTION : Not a hotspot, na app selection - Cliente - so p test se ta a null "
              + isHotspot);
      adapter = new HighwayAppSelectionCustomAdapter(this, gridView.getContext(), itemList, false);
    }

    gridView.setDrawSelectorOnTop(false);

    gridView.setAdapter(adapter);
  }

  //    @Override
  //    public void selectedApp(int position) {
  ////        presenter.pressedApp(position);
  //        System.out.println("pressed app");
  //    }

  @Override public void setAppSelectionListener(AppSelectionListener listener) {
    adapter.setListener(listener);
  }

  @Override public void removeAppSelectionListener() {
    adapter.removeListener();
  }

  @Override public void notifyChanges() {
    if (adapter != null) {
      adapter.notifyDataSetChanged();
    }
  }

  //    @Override
  //    public void setAppSelectionListener(ApplicationProvider.AppSelectionListener listener) {
  //        adapter.setListener(listener);
  //    }

  //    public String getServerIPAddress() {
  //        String ip = "";
  //        try {
  //            //run the net interf and then the ips for each net int
  //            Enumeration<NetworkInterface> listOfNetInt = NetworkInterface.getNetworkInterfaces();
  //            while (listOfNetInt.hasMoreElements()) {
  //                NetworkInterface tmp = listOfNetInt.nextElement();
  //
  //                Enumeration<InetAddress> adresses = tmp.getInetAddresses();
  //
  //
  //                while (adresses.hasMoreElements()) {
  //                    InetAddress inetAddress = adresses.nextElement();
  //
  //                    if (!inetAddress.isLoopbackAddress()) {
  //                        ip = inetAddress.getHostAddress().toString();
  //                        System.out.println("HIGHWAY APP SELECTION ACTIVITY The ip Address i got is...  " + ip);
  //                    }
  //                }
  //
  //                List<InterfaceAddress> addIntf = tmp.getInterfaceAddresses();
  //                for (int i = 0; i < addIntf.size(); i++) {
  //                    InterfaceAddress aux = addIntf.get(i);
  //                    System.out.println("HIGHWAY APP SELECTION ACTIVITY As interface address estao a  : " + aux.toString());
  //
  //                }
  //            }
  //        } catch (SocketException e) {
  //            e.printStackTrace();
  //            System.out.println("HIGHWAY APP SELECTION ACTIVITY Houve aqui um erro caiu na excepcao a obter o meu ip");
  //        }
  //        return ip;
  //    }

  //    public void sendMultipleFiles(List<App> list) {//se fizer onback tmb tenho que passar o isHOtspot
  //        Intent sendIntent = null;
  //        if (isHotspot) {
  //            System.out.println("HIGHWAY APP SELECTION ACTIVITY I will try to send a message and i am a hostpot");
  //            sendIntent = new Intent(this, HighwayServerComm.class);
  //            //como sou servidor devo meter o firstSender a ""
  //        } else {
  //            System.out.println("HIGHWAY APP SELECTION ACTIVITY I will try to send a message and i am NOT NOT NOT NOT a hotspot");
  //            sendIntent = new Intent(this, HighwayClientComm.class);
  //            sendIntent.putExtra("targetIP", targetIPAddress);
  //
  //        }
  //        sendIntent.putExtra("port", porto);
  //        System.out.println("App selection activity  : o bool do isHotspot : " + isHotspot);
  //        sendIntent.putExtra("isHotspot", isHotspot);
  //
  ////        sendIntent.putExtra("fromOutside",false);
  //
  //        Bundle tmp = new Bundle();
  //        tmp.putParcelableArrayList("listOfAppsToInstall", new ArrayList<Parcelable>(list));//change listOfAppsToInstall to listOfAppsTOSend
  //        sendIntent.putExtra("bundle", tmp);
  //        sendIntent.setAction("SEND");
  //        startService(sendIntent);
  //    }

  //    class SendListener implements View.OnClickListener {
  //
  //        public void onClick(View v) {
  //
  //
  //            listOfSelectedApps = adapter.getListOfSelectedApps();
  //            System.out.println("Appselection activity  - - Got the list of selected apps, its size is : ::: " + listOfSelectedApps.size());
  //            if (listOfSelectedApps.size() > 0) {
  //                sendMultipleFiles(listOfSelectedApps);
  //            } else {
  //                Toast.makeText(HighwayAppSelectionActivity.this, HighwayAppSelectionActivity.this.getResources().getString(R.string.noSelectedAppsToSend), Toast.LENGTH_SHORT).show();
  //            }
  //        }
  //    }

  //
  //    private class initializeUITask extends AsyncTask<Void, Void, String> {
  //
  //        @Override
  //        protected String doInBackground(Void... params) {
  //            getInstalledApps();
  ////            getServerIPAddress();
  //            return null;
  //        }
  //
  //        @Override
  //        protected void onPostExecute(String s) {
  ////            if (isHotspot == null) {
  ////                if (DataHolder.getInstance().getWcOnJoin() != null) {
  ////                    isHotspot = "true";
  ////                } else {
  ////                    isHotspot = "false";
  ////                }
  //////            }
  ////            if (isHotspot) {
  ////                System.out.println("Estou no true do ser hotspot, na app selection.");
  ////
  ////                adapter = new HighwayAppSelectionCustomAdapter(highwayAppSelectionActivity, gridViewAppItemsList, true);
  ////
  ////
  ////            } else {
  ////                System.out.println(" hIGHWAY APP SELECTION : Not a hotspot, na app selection - Cliente - so p test se ta a null " + isHotspot);
  ////
  ////                adapter = new HighwayAppSelectionCustomAdapter(highwayAppSelectionActivity, gridViewAppItemsList, false);
  ////
  ////            }
  ////
  ////            gridView.setDrawSelectorOnTop(false);
  ////
  ////            gridView.setAdapter(adapter);
  ////
  ////            progressBar.setVisibility(View.GONE);
  ////            gridView.setVisibility(View.VISIBLE);
  ////            sendButton.setOnClickListener(new SendListener());
  //            super.onPostExecute(s);
  //        }
  //    }
}
