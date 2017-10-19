package cm.aptoide.pt.social.leaderboard.view;

import cm.aptoide.pt.dataprovider.ws.v7.GetLeaderboardEntriesResponse;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.social.leaderboard.data.LeaderboardEntry;
import java.util.List;
import rx.Observable;

/**
 * Created by franciscocalado on 9/22/17.
 */

public interface LeaderboardView extends View {

  void showLeaderboardEntries(List<List<LeaderboardEntry>> entries);
  Observable<LeaderboardEntry> postClicked();
  Observable<String> spinnerChoice();
  void waitForData();
}

