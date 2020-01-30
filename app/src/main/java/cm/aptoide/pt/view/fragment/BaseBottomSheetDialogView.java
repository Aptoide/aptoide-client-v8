package cm.aptoide.pt.view.fragment;

import androidx.annotation.NonNull;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;
import rx.Observable;

public class BaseBottomSheetDialogView extends BaseBottomSheetDialogFragment implements View {
  @NonNull @Override
  public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull LifecycleEvent lifecycleEvent) {
    return RxLifecycle.bindUntilEvent(getLifecycleEvent(), lifecycleEvent);
  }

  @Override public Observable<LifecycleEvent> getLifecycleEvent() {
    return lifecycle().flatMap(this::convertToEvent);
  }

  @Override public void attachPresenter(Presenter presenter) {
    presenter.present();
  }

  @NonNull private Observable<cm.aptoide.pt.presenter.View.LifecycleEvent> convertToEvent(
      FragmentEvent event) {
    switch (event) {
      case ATTACH:
      case CREATE:
        return Observable.empty();
      case CREATE_VIEW:
        return Observable.just(cm.aptoide.pt.presenter.View.LifecycleEvent.CREATE);
      case START:
        return Observable.just(cm.aptoide.pt.presenter.View.LifecycleEvent.START);
      case RESUME:
        return Observable.just(cm.aptoide.pt.presenter.View.LifecycleEvent.RESUME);
      case PAUSE:
        return Observable.just(cm.aptoide.pt.presenter.View.LifecycleEvent.PAUSE);
      case STOP:
        return Observable.just(cm.aptoide.pt.presenter.View.LifecycleEvent.STOP);
      case DESTROY_VIEW:
        return Observable.just(cm.aptoide.pt.presenter.View.LifecycleEvent.DESTROY);
      case DETACH:
      case DESTROY:
        return Observable.empty();
      default:
        throw new IllegalStateException("Unrecognized event: " + event.name());
    }
  }
}
