package cm.aptoide.pt.spotandshareapp.view;

import android.graphics.drawable.Drawable;
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

  void showExitWarning();

  Observable<Void> exitEvent();

  void navigateBack();

  void onLeaveGroupError();

  void updateReceivedAppsList(List<TransferAppModel> transferAppModelList);

  void openAppSelectionFragment(boolean shouldCreateGroup);

  Observable<TransferAppModel> installApp();

  void updateTransferInstallStatus(TransferAppModel transferAppModel);

  Observable<Void> clickedConnectedFriends();

  void openConnectedFriendsFragment();

  void updateFriendsNumber(int friendsList);

  void updateFriendsAvatar(Drawable friendAvatar);
}
