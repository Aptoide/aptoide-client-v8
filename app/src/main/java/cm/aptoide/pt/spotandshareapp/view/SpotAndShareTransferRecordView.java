package cm.aptoide.pt.spotandshareapp.view;

import cm.aptoide.pt.spotandshareapp.SpotAndShareUser;
import cm.aptoide.pt.spotandshareapp.TransferAppModel;
import java.util.List;
import rx.Observable;

/**
 * Created by filipe on 12-06-2017.
 */

public interface SpotAndShareTransferRecordView extends SpotAndSharePickAppsView {

  void finish();

  Observable<TransferAppModel> acceptApp();

  Observable<Void> backButtonEvent();

  void back();

  Observable<Void> exitEvent();

  void navigateBack();

  void onLeaveGroupError();

  void updateReceivedAppsList(List<TransferAppModel> transferAppModelList);

  Observable<TransferAppModel> installApp();

  void updateTransferInstallStatus(TransferAppModel transferAppModel);

  Observable<Void> clickedFriendsInformationButton();

  void showFriendsNumber(int friendsList);

  void showFriendsOnMenu(List<SpotAndShareUser> friendsList);

  Observable<Void> friendsMenuDismiss();

  void clearMenu();

  Observable<Void> listenBottomSheetHeaderClicks();

  void pressedBottomSheetHeader();

  void hideFriendsNumber();
}
