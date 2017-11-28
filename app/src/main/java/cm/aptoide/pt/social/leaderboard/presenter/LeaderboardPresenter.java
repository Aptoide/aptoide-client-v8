package cm.aptoide.pt.social.leaderboard.presenter;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.social.leaderboard.data.Leaderboard;
import cm.aptoide.pt.social.leaderboard.view.LeaderboardView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class LeaderboardPresenter implements Presenter {

  private final LeaderboardView view;
  private final Leaderboard leaderboard;
  private final CrashReport crashReport;


  public LeaderboardPresenter(LeaderboardView view, Leaderboard leaderboard,
      CrashReport crashReport) {

    this.view = view;
    this.leaderboard = leaderboard;
    this.crashReport = crashReport;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.waitForData())
        .flatMap(__-> leaderboard.getLeaderboardEntries("global"))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(leaderboardEntries->view.showLeaderboardEntries(leaderboardEntries))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(leaderboardEntries -> {},
            throwable -> crashReport.log(throwable));


    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.spinnerChoice())
        .doOnNext(choice ->view.waitForData())
        .flatMap(choice-> leaderboard.getLeaderboardEntries(choice))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(leaderboardEntries->view.showLeaderboardEntries(leaderboardEntries))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(leaderboardEntries -> {},
            throwable -> crashReport.log(throwable));
  }


}

