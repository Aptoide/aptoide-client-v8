package cm.aptoide.pt.social.view.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.GameAnswer;
import cm.aptoide.pt.social.data.LeaderboardTouchEvent;
import cm.aptoide.pt.social.leaderboard.data.Leaderboard;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.recycler.displayable.SpannableFactory;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class GameAnswerViewHolder extends  PostViewHolder<GameAnswer> {

  private final TextView score;
  private final TextView headerIncrement;
  private final TextView leaderboard;
  private final ImageView scoreIcon;
  private final ImageView leaderboardIcon;
  private final ImageView headerIcon;
  private final TextView headerTitle;
  private final TextView headerSubTitle;
  private final SpannableFactory spannableFactory;
  private final ImageView appIcon;
  private final TextView appName;
  private final Button getApp;
  private final ImageView answerStatus;
  private final TextView answerMessage;
  private final View leaderboards;
  private final ImageView leaderboardStatus;
  private final TextView scoreStatus;
  private final TextView incrementStatus;
  private final TextView globalStatus;
  private final TextView countryStatus;
  private final TextView friendsStatus;
  private final ProgressBar rankProgress;
  private final ProgressBar leaderboardProgress;
  private final String marketName;
  private final View leaderboardElement;
  private final View headerStats;

  private int scoreValue;

  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;

  public GameAnswerViewHolder(View itemView, PublishSubject<CardTouchEvent> cardTouchEventPublishSubject,
      SpannableFactory spannableFactory, String marketName) {
    super(itemView, cardTouchEventPublishSubject);

    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.spannableFactory = spannableFactory;
    this.marketName = marketName;

    this.score = (TextView) itemView.findViewById(R.id.stats_header).findViewById(R.id.displayable_social_timeline_game_card_score);
    this.headerIncrement = (TextView) itemView.findViewById(R.id.stats_header).findViewById(R.id.score_increment);
    this.leaderboard = (TextView) itemView.findViewById(R.id.stats_header).findViewById(R.id.displayable_social_timeline_game_card_leaderboard);

    this.appIcon = (ImageView) itemView.findViewById(R.id.get_app_icon);
    this.appName = (TextView) itemView.findViewById(R.id.app_name);
    this.getApp = (Button) itemView.findViewById(R.id.get_app_button);
    this.answerStatus = (ImageView) itemView.findViewById(R.id.answer_status);
    this.answerMessage = (TextView) itemView.findViewById(R.id.answer_message);

    this.headerIcon = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_answer_card_icon);
    this.headerTitle = (TextView) itemView.findViewById(R.id.displayable_social_timeline_answer_card_title);
    this.headerSubTitle = (TextView) itemView.findViewById(R.id.displayable_social_timeline_answer_card_subtitle);
    this.leaderboardIcon = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_game_card_leaderboard_icon);
    this.scoreIcon = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_game_card_score_icon);

    this.leaderboards = itemView.findViewById(R.id.card_leaderboard);

    this.leaderboardStatus = (ImageView) itemView.findViewById(R.id.trophy_image);
    this.scoreStatus = (TextView) itemView.findViewById(R.id.score_value);
    this.incrementStatus = (TextView) itemView.findViewById(R.id.status_increment);
    this.globalStatus = (TextView) itemView.findViewById(R.id.global_value);
    this.countryStatus = (TextView) itemView.findViewById(R.id.country_value);
    this.friendsStatus = (TextView) itemView.findViewById(R.id.friends_value);

    this.rankProgress = (ProgressBar) itemView.findViewById(R.id.rank_progress);
    this.leaderboardProgress = (ProgressBar) itemView.findViewById(R.id.leaderboard_progress);

    this.leaderboardElement = itemView.findViewById(R.id.card_leaderboard);

    this.headerStats = itemView.findViewById(R.id.stats_header);


  }

  @Override public void setPost(GameAnswer card, int position) {

    if (card.getPoints() == 1) {
      this.headerIncrement.setVisibility(View.VISIBLE);
      this.headerIncrement.setTextColor(Color.GREEN);
    }else
      this.headerIncrement.setVisibility(View.GONE);


    this.leaderboard.setText(String.valueOf(card.getgRanking()));
    this.score.setText(String.valueOf(card.getScore()));
    ImageLoader.with(itemView.getContext())
        .load(card.getRightAnswer()
            .getIcon(), appIcon);
    this.appName.setText(card.getRightAnswer().getName());
    if(card.getStatus() == "Wrong") {
      this.answerStatus.setImageResource(R.drawable.gameanswer_wrong);
      this.incrementStatus.setVisibility(View.GONE);
    }
    else{
      this.answerStatus.setImageResource(R.drawable.gameanswer_correct);
      this.incrementStatus.setVisibility(View.VISIBLE);
    }
    this.answerMessage.setText("Answer:");

    this.getApp.setText("get app");

    ImageLoader.with(itemView.getContext())
        .load("http://pool.img.aptoide.com/dfl/783ac07187647799c87c4e1d5cde6b8b_icon.png", this.headerIcon);
    this.headerTitle.setText(getStyledTitle(itemView.getContext(), getTitle(itemView.getContext()
        .getResources()), marketName));
    if(card.getCardsLeft() == 0)
      this.headerSubTitle.setText("No more cards. Come back tomorrow for more!");
    else
      this.headerSubTitle.setText(String.valueOf(card.getCardsLeft())+" cards left today.");

    LeaderboardTouchEvent event  = new LeaderboardTouchEvent(card, CardTouchEvent.Type.BODY);

    getApp.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.BODY)));

    leaderboardElement.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(event));

    headerStats.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(event));



    //if(!card.getLoginButton()){
    //    playerList.setVisibility(View.GONE);
    //    logIn.setVisibility(View.VISIBLE);
    //    logIn.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
    //        new CardTouchEvent(card, CardTouchEvent.Type.LOGIN)));
    //    score.setVisibility(View.GONE);
    //    leaderboard.setVisibility(View.GONE);
    //    leaderboardIcon.setVisibility(View.GONE);
    //    scoreIcon.setVisibility(View.GONE);
    //    this.headerIncrement.setVisibility(View.GONE);
    //}
    if(card.getgRanking() != -1){
      leaderboard.setVisibility(View.VISIBLE);
      rankProgress.setVisibility(View.INVISIBLE);
      this.leaderboard.setText(String.valueOf(card.getgRanking()));
      leaderboards.setVisibility(View.VISIBLE);
      leaderboardProgress.setVisibility(View.INVISIBLE);

      scoreStatus.setText(String.valueOf(card.getScore()));
      globalStatus.setText(String.valueOf(card.getgRanking()));
      countryStatus.setText(String.valueOf(card.getlRanking()));
      friendsStatus.setText(String.valueOf(card.getfRanking()));
    }
    else{
      leaderboard.setVisibility(View.INVISIBLE);
      rankProgress.setVisibility(View.VISIBLE);
      leaderboards.setVisibility(View.INVISIBLE);
      leaderboardProgress.setVisibility(View.VISIBLE);
    }
  }

  private Spannable getStyledTitle(Context context, String title, String coloredTextPart) {
    return spannableFactory.createColorSpan(title,
        ContextCompat.getColor(context, R.color.card_store_title), coloredTextPart);
  }

  public String getTitle(Resources resources) {
    return AptoideUtils.StringU.getFormattedString(
        R.string.timeline_title_card_title_game_quiz_present_singular, resources,
        marketName);
  }

}
