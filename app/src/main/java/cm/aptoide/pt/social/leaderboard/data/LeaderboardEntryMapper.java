package cm.aptoide.pt.social.leaderboard.data;

import cm.aptoide.pt.dataprovider.ws.v7.GetLeaderboardEntriesResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class LeaderboardEntryMapper {

  public List<List<LeaderboardEntry>> map(GetLeaderboardEntriesResponse.Data data){
    List<List<LeaderboardEntry>> result = new ArrayList<List<LeaderboardEntry>>();
    LeaderboardEntry entry = new LeaderboardEntry(null,0,0);
    int n = 0;

    for(int i=0;i<data.getGlobal().size();i++){
      result.add(new ArrayList<LeaderboardEntry>());
      result.get(i).add(entry);
      result.get(i).add(entry);
      result.get(i).add(entry);

    }

    for(GetLeaderboardEntriesResponse.User user : data.getGlobal()){
      entry = new LeaderboardEntry(user.getName(),user.getPosition(),user.getScore());
      result.get(n).set(0,entry);
      n++;
    }

    n=0;

    for(GetLeaderboardEntriesResponse.User user : data.getCountry()){
      entry = new LeaderboardEntry(user.getName(),user.getPosition(),user.getScore());
      result.get(n).set(1,entry);
      n++;
    }

    n=0;

    for(GetLeaderboardEntriesResponse.User user : data.getFriends()){
      entry = new LeaderboardEntry(user.getName(),user.getPosition(),user.getScore());
      result.get(n).set(2,entry);
      n++;
    }

    return result;
  }
}
