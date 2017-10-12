package cm.aptoide.pt.social.leaderboard.data;

import cm.aptoide.pt.dataprovider.ws.v7.GetLeaderboardEntriesResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class LeaderboardEntryMapper {

  public List<List<LeaderboardEntry>> map(GetLeaderboardEntriesResponse.Data data){
    List<List<LeaderboardEntry>> result = new ArrayList<>();
    List<LeaderboardEntry> username = new ArrayList<>();
    List<LeaderboardEntry> top = new ArrayList<>();
    List<LeaderboardEntry> leaderboard = new ArrayList<>();
    LeaderboardEntry entry;
    List<GetLeaderboardEntriesResponse.User> current = data.getLeaderboard();

    entry = new LeaderboardEntry(data.getUsername().getName(), data.getUsername().getPosition(), data.getUsername().getScore());
    username.add(entry);

    for(GetLeaderboardEntriesResponse.User user : data.getTop()){
      entry = new LeaderboardEntry(user.getName(), user.getPosition(), user.getScore());
      top.add(entry);
    }

    for(GetLeaderboardEntriesResponse.User user : current){
      entry = new LeaderboardEntry(user.getName(), user.getPosition(), user.getScore());
      leaderboard.add(entry);
    }

    result.add(username);
    result.add(top);
    result.add(leaderboard);

    return result;
  }
}
