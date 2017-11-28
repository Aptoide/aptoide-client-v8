package cm.aptoide.pt.presenter;

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
}
