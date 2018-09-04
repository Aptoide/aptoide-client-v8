package cm.aptoide.pt.app.view;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Scheduler;
import rx.Single;

/**
 * Created by D01 on 27/08/2018.
 */

public class EditorialPresenter implements Presenter {

  private final EditorialView view;
  private final EditorialManager editorialManager;
  private final Scheduler viewScheduler;
  private final CrashReport crashReporter;
  private final EditorialNavigator appOftheWeekNavigator;

  public EditorialPresenter(EditorialView view, EditorialManager editorialManager,
      Scheduler viewScheduler, CrashReport crashReporter,
      EditorialNavigator appOftheWeekNavigator) {
    this.view = view;
    this.editorialManager = editorialManager;
    this.viewScheduler = viewScheduler;
    this.crashReporter = crashReporter;
    this.appOftheWeekNavigator = appOftheWeekNavigator;
  }

  @Override public void present() {
    onCreateSetupToolbar();
    onCreateLoadAppOfTheWeek();
    handleRetryClick();
  }

  private void onCreateLoadAppOfTheWeek() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMapSingle(created -> loadEditorialViewModel())
        .subscribe(__ -> {
        }, crashReporter::log);
  }

  private Single<EditorialViewModel> loadEditorialViewModel() {
    return editorialManager.loadEditorialViewModel()
        .observeOn(viewScheduler)
        .doOnSuccess(editorialViewModel -> {
          if (!editorialViewModel.isLoading()) {
            view.hideLoading();
          }
          if (editorialViewModel.hasError()) {
            view.showError(editorialViewModel.getError());
          } else {
            view.populateView(editorialViewModel);
          }
        })
        .map(editorialViewModel -> editorialViewModel);
  }

  private void onCreateSetupToolbar() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .doOnNext(created -> view.setToolbarInfo(editorialManager.getEditorialName()))
        .subscribe(__ -> {
        }, crashReporter::log);
  }

  private void handleRetryClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.retryClicked()
            .observeOn(viewScheduler)
            .doOnNext(bottom -> view.showLoading())
            .flatMapSingle(__ -> loadEditorialViewModel()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, crashReporter::log);
  }
}
