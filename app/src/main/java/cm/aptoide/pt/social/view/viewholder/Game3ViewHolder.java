package cm.aptoide.pt.social.view.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.Game3;
import cm.aptoide.pt.social.data.GameCardTouchEvent;
import cm.aptoide.pt.social.data.LeaderboardTouchEvent;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.recycler.displayable.SpannableFactory;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class Game3ViewHolder extends PostViewHolder<Game3> {

  private final TextView score;
  private final TextView leaderboard;
  private final ImageView scoreIcon;
  private final ImageView rankIcon;
  private View wrapper;
  private ImageView questionIcon;
  private TextView question;
  private final TextView answerLeft;
  private final TextView answerRight;
  private final ImageView answerLeftIcon;
  private final ImageView answerRightIcon;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final SpannableFactory spannableFactory;

  private final ImageView headerIcon;
  private final TextView headerTitle;
  private final TextView headerSubTitle;

  private final ImageView arrowLeft;
  private final ImageView arrowRight;

  //private final ProgressBar scoreProgress;
  private final ProgressBar leaderboardProgress;

  private final ImageView stampLeft;
  private final ImageView stampRight;

  private final String marketName;

  private final View headerStats;

  private double rand;

  private Game3 card;

  public Game3ViewHolder(View itemView, PublishSubject<CardTouchEvent> cardTouchEventPublishSubject,
      SpannableFactory spannableFactory, String marketName) {
    super(itemView, cardTouchEventPublishSubject);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.spannableFactory = spannableFactory;
    this.marketName = marketName;

    this.score = (TextView) itemView.findViewById(R.id.stats_header).findViewById(R.id.displayable_social_timeline_game_card_score);
    leaderboard = (TextView) itemView.findViewById(R.id.stats_header).findViewById(R.id.displayable_social_timeline_game_card_leaderboard);
    answerLeft = (TextView) itemView.findViewById(R.id.game_card_question3_answer_left);
    answerRight = (TextView) itemView.findViewById(R.id.game_card_question3_answer_right);
    answerLeftIcon = (ImageView) itemView.findViewById(R.id.game_card_question3_icon_left);
    answerRightIcon = (ImageView) itemView.findViewById(R.id.game_card_question3_icon_right);

    this.headerIcon =
        (ImageView) itemView.findViewById(R.id.displayable_social_timeline_game_card_icon);
    this.headerTitle =
        (TextView) itemView.findViewById(R.id.displayable_social_timeline_game_card_title);
    this.headerSubTitle =
        (TextView) itemView.findViewById(R.id.displayable_social_timeline_game_card_subtitle);

    this.arrowLeft = (ImageView) itemView.findViewById(R.id.left_arrow);
    this.arrowRight = (ImageView) itemView.findViewById(R.id.right_arrow);

    this.stampLeft = (ImageView) itemView.findViewById(R.id.stamp_left);
    this.stampRight = (ImageView) itemView.findViewById(R.id.stamp_right);

    //this.scoreProgress = (ProgressBar) itemView.findViewById(R.id.score_progress);
    this.leaderboardProgress = (ProgressBar) itemView.findViewById(R.id.rank_progress);

    this.scoreIcon = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_game_card_score_icon) ;
    this.rankIcon = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_game_card_leaderboard_icon);

    this.headerStats = itemView.findViewById(R.id.stats_header);



    rand=Math.random();


  }

  @Override
  public void setPost(Game3 card, int position) {
    this.card = card;

    this.score.setText(String.valueOf(card.getScore()));
    this.leaderboard.setText(String.valueOf(card.getgRanking()));

    ImageLoader.with(itemView.getContext()).load("http://pool.img.aptoide.com/dfl/783ac07187647799c87c4e1d5cde6b8b_icon.png", this.headerIcon);
    this.headerTitle.setText(getStyledTitle(itemView.getContext(), getTitle(itemView.getContext()
        .getResources()), marketName));
    if(card.getCardsLeft() == 1)
      this.headerSubTitle.setText("Last card. Come back tomorrow for more!");
    else
      this.headerSubTitle.setText(String.valueOf(card.getCardsLeft())+" cards left today.");

    if (card.getQuestionIcon() == null){
      itemView.findViewById(R.id.icon_question).setVisibility(View.GONE);
      wrapper = itemView.findViewById(R.id.question);
      wrapper.setVisibility(View.VISIBLE);
      question = (TextView) wrapper.findViewById(R.id.game_card_question);

    }
    else{
      itemView.findViewById(R.id.question).setVisibility(View.GONE);
      wrapper = itemView.findViewById(R.id.icon_question);
      wrapper.setVisibility(View.VISIBLE);
      questionIcon = (ImageView) wrapper.findViewById(R.id.game_card_questionIcon);
      question = (TextView) wrapper.findViewById(R.id.game_card_question);
      ImageLoader.with(itemView.getContext()).load(card.getQuestionIcon(), questionIcon);
    }
    this.question.setText(card.getQuestion());

    //Randomize right answer to left or right side (if 0<rand<0.5, right answer is on the left side)
    if(rand<0.5){
      ImageLoader.with(itemView.getContext()).load(card.getApp().getIcon(), answerLeftIcon);
      ImageLoader.with(itemView.getContext()).load(card.getApp().getIcon(), stampRight);
      this.answerLeft.setText(card.getApp().getName());

      ImageLoader.with(itemView.getContext()).load(card.getWrongIcon(), answerRightIcon);
      ImageLoader.with(itemView.getContext()).load(card.getWrongIcon(), stampLeft);
      this.answerRight.setText(card.getWrongName());
    }
    else{
      ImageLoader.with(itemView.getContext()).load(card.getWrongIcon(), answerLeftIcon);
      ImageLoader.with(itemView.getContext()).load(card.getWrongIcon(), stampRight);
      this.answerLeft.setText(card.getWrongName());
      ImageLoader.with(itemView.getContext()).load(card.getApp().getIcon(), answerRightIcon);
      ImageLoader.with(itemView.getContext()).load(card.getApp().getIcon(), stampLeft);
      this.answerRight.setText(card.getApp().getName());
    }

    answerLeftIcon.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(answerLeft.getText()))));
    answerRightIcon.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(answerRight.getText()))));

    LeaderboardTouchEvent event = new LeaderboardTouchEvent(card, CardTouchEvent.Type.BODY);

    //score.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(event));
    //leaderboard.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(event));
    //scoreIcon.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(event));
    //rankIcon.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(event));
    headerStats.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(event));




    if(card.getScore()==-1){
      //scoreProgress.setVisibility(View.VISIBLE);
      leaderboardProgress.setVisibility(View.VISIBLE);
      score.setVisibility(View.INVISIBLE);
      leaderboard.setVisibility(View.INVISIBLE);
    }
    else{
      //scoreProgress.setVisibility(View.INVISIBLE);
      leaderboardProgress.setVisibility(View.INVISIBLE);
      score.setVisibility(View.VISIBLE);
      leaderboard.setVisibility(View.VISIBLE);
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
  public void onPostDismissedLeft(Game3 card, int position){
    cardTouchEventPublishSubject.onNext(
        new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(answerLeft.getText())));
  }

  public void onPostDismissedRight(Game3 card, int position){
    cardTouchEventPublishSubject.onNext(
        new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(answerRight.getText())));
  }

  public Game3 getCard() {
    return card;
  }
}
