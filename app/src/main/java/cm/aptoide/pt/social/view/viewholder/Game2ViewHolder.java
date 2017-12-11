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
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.Game2;
import cm.aptoide.pt.social.data.GameCardTouchEvent;
import cm.aptoide.pt.social.data.LeaderboardTouchEvent;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.spannable.SpannableFactory;
import rx.subjects.PublishSubject;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class Game2ViewHolder extends PostViewHolder<Game2> {

  private final TextView score;
  private final TextView leaderboard;
  private final ImageView scoreIcon;
  private final ImageView rankIcon;
  private final ImageView answerLeft;
  private final ImageView answerRight;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final SpannableFactory spannableFactory;
  private final ImageView headerIcon;
  private final TextView headerTitle;
  //private final ProgressBar scoreProgress;
  private final ProgressBar leaderboardProgress;
  private final ImageView stampLeft;
  private final ImageView stampRight;
  private final String marketName;
  private final View headerStats;
  private View wrapper;
  private ImageView questionIcon;
  private TextView question;
  private Game2 card;
  private double rand;


  public Game2ViewHolder(View itemView, PublishSubject<CardTouchEvent> cardTouchEventPublishSubject,
      SpannableFactory spannableFactory, String marketName) {
    super(itemView, cardTouchEventPublishSubject);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.spannableFactory = spannableFactory;
    this.marketName = marketName;

    this.score = (TextView) itemView.findViewById(R.id.stats_header).findViewById(R.id.displayable_social_timeline_game_card_score);
    leaderboard = (TextView) itemView.findViewById(R.id.stats_header).findViewById(R.id.displayable_social_timeline_game_card_leaderboard);
    answerLeft = (ImageView) itemView.findViewById(R.id.left_answer);
    answerRight = (ImageView) itemView.findViewById(R.id.right_answer);

    this.headerIcon =
        (ImageView) itemView.findViewById(R.id.displayable_social_timeline_game_card_icon);
    this.headerTitle =
        (TextView) itemView.findViewById(R.id.displayable_social_timeline_game_card_title);

    this.leaderboardProgress = (ProgressBar) itemView.findViewById(R.id.rank_progress);

    this.stampLeft = (ImageView) itemView.findViewById(R.id.stamp_left);
    this.stampRight = (ImageView) itemView.findViewById(R.id.stamp_right);

    this.scoreIcon = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_game_card_score_icon) ;
    this.rankIcon = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_game_card_leaderboard_icon);

    this.headerStats = itemView.findViewById(R.id.stats_header);

    itemView.setOnTouchListener((view, motionEvent) -> {
      itemView.getParent().requestDisallowInterceptTouchEvent(true);
      return false;
    });


    rand = Math.random();


  }

  @Override
  public void setPost(Game2 card, int position) {

    this.card = card;
    stampLeft.setVisibility(View.GONE);
    stampRight.setVisibility(View.GONE);
    answerLeft.setVisibility(View.VISIBLE);
    answerRight.setVisibility(View.VISIBLE);
    itemView.setVisibility(View.VISIBLE);


    this.score.setText(String.valueOf(card.getScore()));
    this.leaderboard.setText(String.valueOf(card.getgRanking()));

    //ImageLoader.with(itemView.getContext()).load("http://pool.img.aptoide.com/dfl/783ac07187647799c87c4e1d5cde6b8b_icon.png", this.headerIcon);
    headerIcon.setImageResource(R.drawable.aptoide_quiz_icon);
    this.headerTitle.setText(getStyledTitle(itemView.getContext(), getTitle(itemView.getContext()
        .getResources()), marketName));

    if (card.getQuestionIcon() == null){
      itemView.findViewById(R.id.icon_question).setVisibility(View.GONE);
      wrapper = itemView.findViewById(R.id.question);
      wrapper.setVisibility(View.VISIBLE);
      question = (TextView) wrapper.findViewById(R.id.game_card_question);
      RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)stampLeft.getLayoutParams();
      params.setMargins(25,10,0,0);
      stampLeft.setLayoutParams(params);
      params = (RelativeLayout.LayoutParams)stampRight.getLayoutParams();
      params.setMargins(0,10,25,0);
      stampRight.setLayoutParams(params);
      String test = card.getQuestion();
      if(test.charAt(test.length()-1)=='?'){
        this.question.setText(test);
      }
      else{
        this.question.setText(test+" "+card.getApp().getName()+"?");
      }
    }
    else{
      itemView.findViewById(R.id.question).setVisibility(View.GONE);
      wrapper = itemView.findViewById(R.id.icon_question);
      wrapper.setVisibility(View.VISIBLE);
      questionIcon = (ImageView) wrapper.findViewById(R.id.game_card_questionIcon);
      question = (TextView) wrapper.findViewById(R.id.game_card_question);
      ImageLoader.with(itemView.getContext()).load(card.getQuestionIcon(), questionIcon);
      this.question.setText(card.getQuestion());
    }
    //Randomize right answer to left or right side (if 0<rand<0.5, right answer is on the left side)
    if(rand < 0.5){
      ImageLoader.with(itemView.getContext()).load(card.getApp().getIcon(), answerLeft);
      ImageLoader.with(itemView.getContext()).load(card.getApp().getIcon(), stampRight);

      ImageLoader.with(itemView.getContext()).load(card.getWrongIcon(), answerRight);
      ImageLoader.with(itemView.getContext()).load(card.getWrongIcon(), stampLeft);

      answerLeft.setOnClickListener(click -> onClickLeft(position, String.valueOf(card.getApp().getIcon())));
      answerRight.setOnClickListener(click -> onClickRight(position, String.valueOf(card.getWrongIcon())));
    }
    else{
      ImageLoader.with(itemView.getContext()).load(card.getWrongIcon(), answerLeft);
      ImageLoader.with(itemView.getContext()).load(card.getWrongIcon(), stampRight);

      ImageLoader.with(itemView.getContext()).load(card.getApp().getIcon(), answerRight);
      ImageLoader.with(itemView.getContext()).load(card.getApp().getIcon(), stampLeft);

      answerLeft.setOnClickListener(click -> onClickLeft(position, String.valueOf(card.getWrongIcon())));
      answerRight.setOnClickListener(click -> onClickRight(position, String.valueOf(card.getApp().getIcon())));
    }

    LeaderboardTouchEvent event = new LeaderboardTouchEvent(card, CardTouchEvent.Type.BODY,
        position);

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
        ContextCompat.getColor(context, R.color.appstimeline_recommends_title), coloredTextPart);
  }

  public String getTitle(Resources resources) {
    return AptoideUtils.StringU.getFormattedString(
        R.string.timeline_title_card_title_game_quiz_present_singular, resources,
        marketName);
  }

  public void onPostDismissedLeft(Game2 card, int position){
    card.setAnswerType("Swipe");
    if(rand<0.5) {
      cardTouchEventPublishSubject.onNext(
          new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(card.getApp().getIcon())));
    }
    else{
      cardTouchEventPublishSubject.onNext(
          new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(card.getWrongIcon())));
    }
  }

  public void onPostDismissedRight(Game2 card, int position){
    card.setAnswerType("Swipe");
    if(rand<0.5) {
      cardTouchEventPublishSubject.onNext(
          new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(card.getWrongIcon())));
    }
    else{
      cardTouchEventPublishSubject.onNext(
          new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(card.getApp().getIcon())));
    }
  }

  public Game2 getCard() {
    return card;
  }

  public void onClickLeft(int position, String status){
    card.setAnswerType("Click");
    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
        R.anim.slide_out_left);
    animation.setDuration(1000);
    animation.setAnimationListener(new Animation.AnimationListener(){
      @Override public void onAnimationStart(Animation animation) {
        answerLeft.setVisibility(View.INVISIBLE);
        answerRight.setVisibility(View.INVISIBLE);
        stampRight.setAlpha(1f);
        stampRight.setVisibility(View.VISIBLE);
      }

      @Override public void onAnimationEnd(Animation animation) {
        itemView.setVisibility(View.INVISIBLE);
        cardTouchEventPublishSubject.onNext(
            new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, status));
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    this.itemView.startAnimation(animation);
  }

  public void onClickRight(int position, String status){
    card.setAnswerType("Click");
    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
        R.anim.slide_out_right);
    animation.setDuration(1000);
    animation.setAnimationListener(new Animation.AnimationListener(){
      @Override public void onAnimationStart(Animation animation) {
        answerLeft.setVisibility(View.INVISIBLE);
        answerRight.setVisibility(View.INVISIBLE);
        stampLeft.setAlpha(1f);
        stampLeft.setVisibility(View.VISIBLE);
      }

      @Override public void onAnimationEnd(Animation animation) {
        itemView.setVisibility(View.INVISIBLE);
        cardTouchEventPublishSubject.onNext(
            new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, status));
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    this.itemView.startAnimation(animation);
  }

}
