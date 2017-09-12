package cm.aptoide.pt.spotandshareapp.view;

import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by filipe on 12-09-2017.
 */

public interface ShareAptoideView extends View {

  Observable<Void> backButtonEvent();

  void showExitWarning();

  Observable<Void> exitEvent();

  void navigateBack();
}
