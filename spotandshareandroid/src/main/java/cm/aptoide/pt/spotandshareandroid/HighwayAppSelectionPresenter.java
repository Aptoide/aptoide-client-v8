package cm.aptoide.pt.spotandshareandroid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 07-02-2017.
 */

public class HighwayAppSelectionPresenter implements Presenter {

  private ApplicationProvider applicationProvider;
  private ApplicationSender applicationSender;
  private HighwayAppSelectionView view;
  private boolean isHotspot;
  private List<AppViewModel> viewModelApps;
  private List<App> selectedApps;

  public HighwayAppSelectionPresenter(ApplicationProvider applicationProvider,
      ApplicationSender applicationSender, HighwayAppSelectionView view, boolean isHotspot) {
    this.applicationProvider = applicationProvider;
    this.applicationSender = applicationSender;
    this.view = view;
    this.isHotspot = isHotspot;
    this.selectedApps = new ArrayList<>();
  }

  @Override public void onCreate() {
    applicationProvider.initializeUI(new ApplicationProvider.InitializeUIListener() {
      @Override public void onListInitialized(List<App> itemList) {
        viewModelApps = applicationProvider.convertAppListToAppViewModelList(itemList);
        view.generateAdapter(isHotspot, viewModelApps);
        view.setUpSendListener();
        view.enableGridView(true);
        setAppSelectionListener();
      }
    });
  }

  @Override public void onResume() {

  }

  @Override public void onPause() {

  }

  @Override public void onDestroy() {
    applicationProvider.stop();
    applicationSender.stop();
  }

  @Override public void onStop() {

  }

  @Override public void onStart() {

  }

  private void setAppSelectionListener() {
    view.setAppSelectionListener(new HighwayAppSelectionView.AppSelectionListener() {
      @Override public void onAppSelected(AppViewModel item) {
        App app = applicationProvider.getApp(item.getPackageName());
        if (item.isSelected()) {
          item.setSelected(false);
          selectedApps.remove(app);
        } else {
          item.setSelected(true);
          selectedApps.add(app);
        }
        view.notifyChanges();
      }
    });
  }

  public void sendFiles(List<App> list) {

  }

  public void clickedSendButton() {

    System.out.println("selectedApps size : " + selectedApps.size());

    if (selectedApps.size() > 0) {
      applicationSender.sendApp(selectedApps);
      view.goBackToTransferRecord();
    } else {
      view.showNoAppsSelectedToast();
    }
  }
}
