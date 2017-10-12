package cm.aptoide.pt.social.leaderboard.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.social.leaderboard.data.LeaderboardEntry;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class LeaderboardViewHolder extends RecyclerView.ViewHolder {

  private final TextView position;
  private final TextView name;
  private final TextView score;


  private final PublishSubject<LeaderboardEntry> leaderboardEntryPublishSubject;

  public LeaderboardViewHolder(View itemView,
      PublishSubject<LeaderboardEntry> leaderboardEntryPublishSubject) {
    super(itemView);
    this.position = (TextView) itemView.findViewById(R.id.user_position);
    this.name = (TextView) itemView.findViewById(R.id.user_name);
    this.score = (TextView) itemView.findViewById(R.id.user_score);

    this.leaderboardEntryPublishSubject = leaderboardEntryPublishSubject;
  }

  public void setItem(LeaderboardEntry entries, String currentUser) {


    position.setText(String.valueOf(entries.getPosition()));
    name.setText(entries.getName());
    if(name.getText().toString().toLowerCase().equals(currentUser.toLowerCase()))
      name.setTextColor(itemView.getResources().getColor(R.color.card_store_title));
    else
      name.setTextColor(itemView.getResources().getColor(R.color.black_87_alpha));
    score.setText(String.valueOf(entries.getScore()));
  }
}
