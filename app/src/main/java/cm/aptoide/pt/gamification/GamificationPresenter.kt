package cm.aptoide.pt.gamification

import android.util.Log
import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.wallet.WalletAppProvider
import rx.Scheduler

class GamificationPresenter(private val gamificationManager: GamificationManager,
                            private val view: GamificationView,
                            private val gamificationNavigator: GamificationNavigator,
                            private val viewScheduler: Scheduler,
                            private val walletAppProvider: WalletAppProvider) : Presenter {

  override fun present() {
    handleStartUp()
    handleClickOnInstall()
    handleClickOnRedeem()
  }

  private fun handleStartUp() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMapSingle {
          gamificationManager.isFirstLevelComplete()
        }
        .doOnNext { completed -> if (!completed) view.showInitialState() }
        .filter { it }
        .flatMapSingle { gamificationManager.isSecondLevelComplete() }
        .observeOn(viewScheduler)
        .doOnNext { level2Complete ->
          if (!level2Complete && !gamificationManager.isSecondChallengeUnlocked()) {
            view.showSecondChallengeLocked(
                gamificationManager.getSecondChallengeTimeLeft())
          } else if (level2Complete) {
            view.showRedeem()
          }
        }
        .filter { !it && gamificationManager.isSecondChallengeUnlocked() }
        .flatMap { walletAppProvider.getWalletApp() }
        .observeOn(viewScheduler)
        .doOnNext { view.showSecondChallengeOpen(it) }
        .subscribe({}, { err -> Log.d(this.javaClass.name, err.message) })
  }

  private fun handleClickOnInstall() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap { view.clickOnInstall() }
        .doOnNext {
          gamificationNavigator.navigateToAppViewAndInstall("com.appcoins.wallet")
          view.dismiss()
        }
        .subscribe({}, { err -> Log.d(this.javaClass.name, err.message) })
  }

  private fun handleClickOnRedeem() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap { view.clickOnRedeem() }
        .subscribe({}, { err -> Log.d(this.javaClass.name, err.message) })
  }
}