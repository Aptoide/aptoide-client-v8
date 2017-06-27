package cm.aptoide.pt.spotandshareapp.view;

import rx.Observable;

/**
 * Created by filipe on 12-06-2017.
 */

public interface SpotAndShareWaitingToReceiveView extends View {

  void finish();

  Observable<Void> startSearch();

  void openSpotandShareTransferRecordFragment();
}
