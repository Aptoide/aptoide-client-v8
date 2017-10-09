package cm.aptoide.pt.social.view.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.Game1;
import cm.aptoide.pt.social.data.GameCardTouchEvent;
import cm.aptoide.pt.social.data.LeaderboardTouchEvent;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.recycler.displayable.SpannableFactory;
import rx.subjects.PublishSubject;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class Game1ViewHolder extends  PostViewHolder<Game1> {

  private final TextView score;
  private final TextView leaderboard;
  private final View statsHeader;
  private final ImageView questionIcon;
  private final TextView question;
  private final TextView leftAnswer;
  private final TextView rightAnswer;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final SpannableFactory spannableFactory;

  private final ImageView headerIcon;
  private final TextView headerTitle;
  private final TextView headerSubTitle;
  //private final ImageView leftArrow;
  //private final ImageView rightArrow;

  //private final ProgressBar scoreProgress;
  private final ProgressBar leaderboardProgress;

  private final TextView stampLeft;
  private final TextView stampRight;


  private final String marketName;
  private Game1 card;

  private double rand;



  public Game1ViewHolder(View itemView, PublishSubject<CardTouchEvent> cardTouchEventPublishSubject,
      SpannableFactory spannableFactory, String marketName) {
    super(itemView, cardTouchEventPublishSubject);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.spannableFactory = spannableFactory;
    this.marketName = marketName;

    this.score = (TextView) itemView.findViewById(R.id.stats_header).findViewById(R.id.displayable_social_timeline_game_card_score);
    leaderboard = (TextView) itemView.findViewById(R.id.stats_header).findViewById(R.id.displayable_social_timeline_game_card_leaderboard);
    questionIcon = (ImageView) itemView.findViewById(R.id.game_card_question1_icon);
    question = (TextView) itemView.findViewById(R.id.game_card_question1_question);
    leftAnswer = (TextView) itemView.findViewById(R.id.left_answer);
    rightAnswer = (TextView) itemView.findViewById(R.id.right_answer);

    this.headerIcon =
        (ImageView) itemView.findViewById(R.id.displayable_social_timeline_game_card_icon);
    this.headerTitle =
        (TextView) itemView.findViewById(R.id.displayable_social_timeline_game_card_title);
    this.headerSubTitle =
        (TextView) itemView.findViewById(R.id.displayable_social_timeline_game_card_subtitle);

    //this.leftArrow = (ImageView) itemView.findViewById(R.id.left_arrow);
    //this.rightArrow = (ImageView) itemView.findViewById(R.id.right_arrow);

    //this.scoreProgress = (ProgressBar) itemView.findViewById(R.id.score_progress);
    this.leaderboardProgress = (ProgressBar) itemView.findViewById(R.id.rank_progress);

    this.stampLeft = (TextView) itemView.findViewById(R.id.stamp_left);
    this.stampRight = (TextView) itemView.findViewById(R.id.stamp_right);

    this.statsHeader = itemView.findViewById(R.id.stats_header);
    rand = Math.random();
  }

  @Override
  public void setPost(Game1 card, int position) {
    int played;

    this.card = card;
    stampLeft.setVisibility(View.GONE);
    stampRight.setVisibility(View.GONE);
    rightAnswer.setVisibility(View.VISIBLE);
    leftAnswer.setVisibility(View.VISIBLE);
    itemView.setVisibility(View.VISIBLE);

    this.score.setText(String.valueOf(card.getScore()));
    this.leaderboard.setText(String.valueOf(card.getgRanking()));
    ImageLoader.with(itemView.getContext()).load(card.getApp().getIcon(), questionIcon);
    this.question.setText(card.getQuestion());

    ImageLoader.with(itemView.getContext()).load("http://pool.img.aptoide.com/dfl/783ac07187647799c87c4e1d5cde6b8b_icon.png", this.headerIcon);
    this.headerTitle.setText(getStyledTitle(itemView.getContext(), getTitle(itemView.getContext()
        .getResources()), marketName));
    if(card.getCardsLeft() == 1)
      this.headerSubTitle.setText("Last card. Come back tomorrow for more!");
    else
      this.headerSubTitle.setText(String.valueOf(card.getCardsLeft())+" cards left today.");

    //Randomize right answer to left or right side (if 0<rand<0.5, right answer is on the left side)
    if(rand<0.5){
      this.leftAnswer.setText(card.getApp().getName());
      this.stampRight.setText(card.getApp().getName());

      this.rightAnswer.setText(card.getWrongName());
      this.stampLeft.setText(card.getWrongName());
    }

    else{
      this.leftAnswer.setText(card.getWrongName());
      this.stampRight.setText(card.getWrongName());

      this.rightAnswer.setText(card.getApp().getName());
      this.stampLeft.setText(card.getApp().getName());
    }

    leftAnswer.setOnClickListener(click -> onClickLeft(position));
    rightAnswer.setOnClickListener(click -> onClickRight(position));
    LeaderboardTouchEvent event = new LeaderboardTouchEvent(card, CardTouchEvent.Type.BODY,
        position);

    //score.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(event));
    //leaderboard.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(event));
    statsHeader.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(event));


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

  public void onPostDismissedLeft(Game1 card, int position){
    cardTouchEventPublishSubject.onNext(
        new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(leftAnswer.getText())));
  }

  public void onPostDismissedRight(Game1 card, int position){
    cardTouchEventPublishSubject.onNext(
        new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(rightAnswer.getText())));
  }

  public Game1 getCard() {
    return card;
  }

  public void onClickLeft(int position){
    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
        R.anim.slide_out_left);
    animation.setDuration(1000);
    animation.setAnimationListener(new Animation.AnimationListener(){
      @Override public void onAnimationStart(Animation animation) {
        leftAnswer.setVisibility(View.INVISIBLE);
        rightAnswer.setVisibility(View.INVISIBLE);
        stampRight.setAlpha(1f);
        stampRight.setVisibility(View.VISIBLE);
      }

      @Override public void onAnimationEnd(Animation animation) {
        itemView.setVisibility(View.INVISIBLE);
        cardTouchEventPublishSubject.onNext(
            new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(leftAnswer.getText())));
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    this.itemView.startAnimation(animation);
  }

  public void onClickRight(int position){
    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
        R.anim.slide_out_right);
    animation.setDuration(1000);
    animation.setAnimationListener(new Animation.AnimationListener(){
      @Override public void onAnimationStart(Animation animation) {
        leftAnswer.setVisibility(View.INVISIBLE);
        rightAnswer.setVisibility(View.INVISIBLE);
        stampLeft.setAlpha(1f);
        stampLeft.setVisibility(View.VISIBLE);
      }

      @Override public void onAnimationEnd(Animation animation) {
        itemView.setVisibility(View.INVISIBLE);
        cardTouchEventPublishSubject.onNext(
            new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(rightAnswer.getText())));
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    this.itemView.startAnimation(animation);
  }

}

