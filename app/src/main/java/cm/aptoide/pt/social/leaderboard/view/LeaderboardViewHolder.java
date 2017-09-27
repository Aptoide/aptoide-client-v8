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

  private final TextView globalPosition;
  private final TextView globalName;
  private final TextView globalScore;

  private final TextView countryPosition;
  private final TextView countryName;
  private final TextView countryScore;

  private final TextView friendsPosition;
  private final TextView friendsName;
  private final TextView friendsScore;

  private final View friendsEntry;
  private final PublishSubject<LeaderboardEntry> leaderboardEntryPublishSubject;

  public LeaderboardViewHolder(View itemView,
      PublishSubject<LeaderboardEntry> leaderboardEntryPublishSubject) {
    super(itemView);
    this.globalPosition = (TextView) itemView.findViewById(R.id.global_entry)
        .findViewById(R.id.leaderboard_position);
    this.globalName = (TextView) itemView.findViewById(R.id.global_entry)
        .findViewById(R.id.leaderboard_user_name);
    this.globalScore = (TextView) itemView.findViewById(R.id.global_entry)
        .findViewById(R.id.leaderboard_user_score);

    this.countryPosition = (TextView) itemView.findViewById(R.id.country_entry)
        .findViewById(R.id.leaderboard_position);
    this.countryName = (TextView) itemView.findViewById(R.id.country_entry)
        .findViewById(R.id.leaderboard_user_name);
    this.countryScore = (TextView) itemView.findViewById(R.id.country_entry)
        .findViewById(R.id.leaderboard_user_score);

    this.friendsPosition = (TextView) itemView.findViewById(R.id.friends_entry)
        .findViewById(R.id.leaderboard_position);
    this.friendsName = (TextView) itemView.findViewById(R.id.friends_entry)
        .findViewById(R.id.leaderboard_user_name);
    this.friendsScore = (TextView) itemView.findViewById(R.id.friends_entry)
        .findViewById(R.id.leaderboard_user_score);

    this.friendsEntry = itemView.findViewById(R.id.friends_entry);

    this.leaderboardEntryPublishSubject = leaderboardEntryPublishSubject;
  }

  public void setItem(List<LeaderboardEntry> entries) {

    globalPosition.setText("#"+String.valueOf(entries.get(0).getPosition()));
    globalName.setText(entries.get(0).getName());
    globalScore.setText(String.valueOf(entries.get(0).getScore()));

    countryPosition.setText("#"+String.valueOf(entries.get(1).getPosition()));
    countryName.setText(entries.get(1).getName());
    countryScore.setText(String.valueOf(entries.get(1).getScore()));

    if(entries.get(2).getName()==null)
      this.friendsEntry.setVisibility(View.INVISIBLE);
    else{
      friendsEntry.setVisibility(View.VISIBLE);
      friendsPosition.setText("#"+String.valueOf(entries.get(2).getPosition()));
      friendsName.setText(entries.get(2).getName());
      friendsScore.setText(String.valueOf(entries.get(2).getScore()));
    }
    //globalName.setOnClickListener(click -> leaderboardEntryPublishSubject.onNext(entries.get(0)));
  }
}
