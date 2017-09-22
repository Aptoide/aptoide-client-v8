package cm.aptoide.pt.social.leaderboard.presenter;

import android.os.Bundle;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.social.leaderboard.data.Leaderboard;
import cm.aptoide.pt.social.leaderboard.view.LeaderboardView;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class LeaderboardPresenter implements Presenter {

  private final LeaderboardView view;
  private final Leaderboard leaderboard;
  private final CrashReport crashReport;
  private final LeaderboardNavigation leaderboardNavigation;
  private final FragmentNavigator fragmentNavigator;


  public LeaderboardPresenter(LeaderboardView view, Leaderboard leaderboard,
      CrashReport crashReport, LeaderboardNavigation leaderboardNavigation, FragmentNavigator fragmentNavigator) {

    this.view = view;
    this.leaderboard = leaderboard;
    this.crashReport = crashReport;
    this.leaderboardNavigation = leaderboardNavigation;
    this.fragmentNavigator = fragmentNavigator;
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__-> leaderboard.getLeaderboardEntries())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(leaderboardEntries->view.showLeaderboardEntries(leaderboardEntries))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(leaderboardEntries -> {},
            throwable -> crashReport.log(throwable));

    //view.getLifecycle()
    //    .filter(event -> event.equals(View.LifecycleEvent.CREATE))
    //    .flatMap(__-> leaderboard.getCurrentUser())
    //    .observeOn(AndroidSchedulers.mainThread())
    //    .doOnNext(currentUser -> view.showCurrentUser(currentUser))
    //    .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
    //    .subscribe(leaderboardEntries -> {},
    //        throwable -> crashReport.log(throwable));

    //view.getLifecycle()
    //    .filter(event-> event.equals(View.LifecycleEvent.CREATE))
    //    .flatMap(created->view.postClicked())
    //    .doOnNext(leaderboardTouchEvent -> {
    //      leaderboardNavigation.navigateToUser();
    //    } )
  }


}

