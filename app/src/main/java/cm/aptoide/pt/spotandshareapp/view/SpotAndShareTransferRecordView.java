package cm.aptoide.pt.spotandshareapp.view;

import cm.aptoide.pt.spotandshare.socket.entities.Friend;
import cm.aptoide.pt.spotandshareapp.TransferAppModel;
import java.util.ArrayList;
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

  void updateFriendsNumber(int friendsList);

  void showFriendsOnMenu(ArrayList<Friend> friendsList);

  Observable<Void> friendsMenuDismiss();

  void clearMenu();
}
