package cm.aptoide.pt.social.leaderboard.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.ws.v7.GetLeaderboardEntriesResponse;
import cm.aptoide.pt.social.leaderboard.data.LeaderboardEntry;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {

  private List<List<LeaderboardEntry>> entries;
  private GetLeaderboardEntriesResponse.User user;
  private PublishSubject<LeaderboardEntry> leaderboardEntryPublishSubject;

  public LeaderboardAdapter(List<List<LeaderboardEntry>> entries, GetLeaderboardEntriesResponse.User user,
      PublishSubject<LeaderboardEntry> leaderboardEntryPublishSubject){
    this.entries=entries;
    this.user=user;
    this.leaderboardEntryPublishSubject = leaderboardEntryPublishSubject;
  }

  @Override public LeaderboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new LeaderboardViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_leaderboard_list_item, parent, false), leaderboardEntryPublishSubject);
  }

  @Override public void onBindViewHolder(LeaderboardViewHolder holder, int position) {
    holder.setItem(entries.get(2).get(position), entries.get(0).get(0).getName());
  }

  @Override public int getItemCount() {
    if(!entries.isEmpty())
      return entries.get(2).size();
    else
      return entries.size();
  }

  public void updateLeaderboardEntries(List<List<LeaderboardEntry>> entries){
    this.entries=entries;
    notifyDataSetChanged();
  }
}