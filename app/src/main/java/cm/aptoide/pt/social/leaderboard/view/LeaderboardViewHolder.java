package cm.aptoide.pt.social.leaderboard.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.social.leaderboard.data.LeaderboardEntry;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class LeaderboardViewHolder extends RecyclerView.ViewHolder {

  private final TextView position;
  private final TextView name;
  private final TextView score;
  private final ImageView avatar;


  private final PublishSubject<LeaderboardEntry> leaderboardEntryPublishSubject;

  public LeaderboardViewHolder(View itemView,
      PublishSubject<LeaderboardEntry> leaderboardEntryPublishSubject) {
    super(itemView);
    this.position = (TextView) itemView.findViewById(R.id.user_position);
    this.name = (TextView) itemView.findViewById(R.id.user_name);
    this.score = (TextView) itemView.findViewById(R.id.user_score);
    this.avatar = (ImageView) itemView.findViewById(R.id.user_icon);

    this.leaderboardEntryPublishSubject = leaderboardEntryPublishSubject;
  }

  public void setItem(LeaderboardEntry entries, String currentUser) {


    position.setText(String.valueOf(entries.getPosition()));
    name.setText(entries.getName());
    if(name.getText().toString().toLowerCase().equals(currentUser.toLowerCase())) {
      name.setTextColor(itemView.getResources()
          .getColor(R.color.card_store_title));
      if(entries.getAvatar()!=null)
        ImageLoader.with(itemView.getContext()).load(entries.getAvatar(), avatar);
      else
        avatar.setImageResource(R.mipmap.spotandshare_avatar_02);
    }
    else {
      name.setTextColor(itemView.getResources()
          .getColor(R.color.black_87_alpha));
      if(entries.getAvatar()!=null)
        ImageLoader.with(itemView.getContext()).load(entries.getAvatar(), avatar);
      else if(entries.getPosition()==1)
        avatar.setImageResource(R.mipmap.spotandshare_avatar_01);
      else if(entries.getPosition()==2)
        avatar.setImageResource(R.mipmap.spotandshare_avatar_03);
      else if(entries.getPosition()==3)
        avatar.setImageResource(R.mipmap.spotandshare_avatar_04);
      else{
        Random r = new Random();
        int rand = r.nextInt(3)+1;
        switch (rand){
          case 1:
            avatar.setImageResource(R.mipmap.spotandshare_avatar_01);
            break;
          case 2:
            avatar.setImageResource(R.mipmap.spotandshare_avatar_03);
            break;
          case 3:
            avatar.setImageResource(R.mipmap.spotandshare_avatar_04);
        }
      }
    }
    score.setText(String.valueOf(entries.getScore()));
  }
}
