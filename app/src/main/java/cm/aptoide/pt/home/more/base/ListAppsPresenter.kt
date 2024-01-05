package cm.aptoide.pt.home.more.base

import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.dataprovider.util.ErrorUtils
import cm.aptoide.pt.home.bundles.apps.EskillsApp
import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.view.app.Application
import rx.Observable
import rx.Scheduler

/**
 * This class implements most of the [ListAppsView] interface.
 * It helps if you're using it in conjunction with [ListAppsFragment]
 */
abstract class ListAppsPresenter<T : Application>(private val view: ListAppsView<T>,
                                                  private val viewScheduler: Scheduler,
                                                  private val crashReporter: CrashReport) :
    Presenter {

  override fun present() {
    loadAppList()
    setupToolbar()
    handleAppClick()
    handleRetryClick()
    handleRefreshSwipe()
    handleBottomReached()
    handleBundleHeaderClick()
  }

  /**
   * Responsible for retrieving the list of apps to be displayed.
   * Do not handle errors in this stream, as they are handled internally
   */
  abstract fun getApps(refresh: Boolean): Observable<List<T>>

  /**
   * Responsible for retrieving more apps when the RV bottom has been reached
   *
   * By default it does nothing. You may override this.
   */
  open fun loadMoreApps(): Observable<List<T>> {
    return Observable.just(null)
  }

  /**
   * Retrieves the title of the screen to be displayed on the Toolbar
   */
  abstract fun getTitle(): String

  /**
   * This method is called when an app is clicked
   */
  abstract fun handleAppClick(appClickEvent: ListAppsClickEvent<T>)

  /**
   * This method is called when the bundle header is clicked
   */
  abstract fun handleHeaderClick()

  private fun handleRetryClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(viewScheduler)
        .flatMap {
          view.errorRetryClick()
              .doOnNext { view.showLoading() }
              .flatMap { loadApps(true) }
              .retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun handleRefreshSwipe() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(viewScheduler)
        .flatMap {
          view.refreshEvents()
              .flatMap { loadApps(true) }
              .retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun setupToolbar() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(viewScheduler)
        .doOnNext { view.setToolbarInfo(getTitle()) }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun loadAppList() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .observeOn(viewScheduler)
        .doOnNext { view.showLoading() }
        .flatMap { loadApps(false) }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }


  private fun loadApps(refresh: Boolean): Observable<List<T>> {
    return getApps(refresh)
        .observeOn(viewScheduler)
        .doOnNext { apps ->
          if (apps.isNotEmpty() && apps[0] is EskillsApp) {
            view.setupEskillsView()
          }
          view.showApps(apps)
        }
        .doOnError { e ->
          if (ErrorUtils.isNoNetworkConnection(e)) {
            view.showNoNetworkError()
          } else {
            view.showGenericError()
          }
        }
  }

  private fun handleAppClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap {
          view.getItemClickEvents()
              .doOnNext { e -> handleAppClick(e) }
              .retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }


  open fun handleBundleHeaderClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap {
          view.headerClicks()
        }.doOnNext {
          handleHeaderClick()
        }.retry()
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  /**
   * We don't display any loading nor error on endless grid RV's.
   * This is the common behavior in these types of RV's
   */
  private fun handleBottomReached() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap {
          view.onBottomReached()
              .flatMap { loadMoreApps() }
              .observeOn(viewScheduler)
              .doOnNext { apps -> if (apps != null && apps.isNotEmpty()) view.addApps(apps) }
              .retry()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }
}