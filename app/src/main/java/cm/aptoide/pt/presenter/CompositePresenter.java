package cm.aptoide.pt.presenter;

import android.os.Bundle;
import java.util.List;

public final class CompositePresenter implements Presenter {

  private final List<Presenter> presenters;

  public CompositePresenter(List<Presenter> presenters) {
    this.presenters = presenters;
  }

  @Override public void present() {
    for (Presenter presenter : presenters) {
      presenter.present();
    }
  }

  @Override public void saveState(Bundle state) {
    for (Presenter presenter : presenters) {
      presenter.saveState(state);
    }
  }

  @Override public void restoreState(Bundle state) {
    for (Presenter presenter : presenters) {
      presenter.restoreState(state);
    }
  }
}
