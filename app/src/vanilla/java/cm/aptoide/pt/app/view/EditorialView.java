package cm.aptoide.pt.app.view;

import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by D01 on 27/08/2018.
 */

public interface EditorialView extends View {
  void showLoading();

  void hideLoading();

  Observable<Void> retryClicked();

  void setToolbarInfo(String title);

  Observable<Void> installButtonClick();

  void populateView(EditorialViewModel editorialViewModel);

  void showError(EditorialViewModel.Error error);
}
