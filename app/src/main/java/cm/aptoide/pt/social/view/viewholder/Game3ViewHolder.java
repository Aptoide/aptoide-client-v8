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
import cm.aptoide.pt.social.data.Game3;
import cm.aptoide.pt.social.data.GameCardTouchEvent;
import cm.aptoide.pt.social.data.LeaderboardTouchEvent;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.spannable.SpannableFactory;
import rx.subjects.PublishSubject;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class Game3ViewHolder extends PostViewHolder<Game3> {

  private final TextView score;
  private final TextView leaderboard;
  private final TextView answerLeft;
  private final TextView answerRight;
  private final ImageView answerLeftIcon;
  private final ImageView answerRightIcon;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final SpannableFactory spannableFactory;
  private final ImageView headerIcon;
  private final TextView headerTitle;
  private final ProgressBar leaderboardProgress;
  private final ImageView stampLeft;
  private final ImageView stampRight;
  private final String marketName;
  private final View headerStats;
  private View wrapper;
  private ImageView questionIcon;
  private TextView question;
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

    this.stampLeft = (ImageView) itemView.findViewById(R.id.stamp_left);
    this.stampRight = (ImageView) itemView.findViewById(R.id.stamp_right);

    this.leaderboardProgress = (ProgressBar) itemView.findViewById(R.id.rank_progress);

    this.headerStats = itemView.findViewById(R.id.stats_header);

    itemView.setOnTouchListener((view, motionEvent) -> {
      itemView.getParent().requestDisallowInterceptTouchEvent(true);
      return false;
    });

    rand=Math.random();


  }

  @Override
  public void setPost(Game3 card, int position) {
    this.card = card;
    stampLeft.setVisibility(View.GONE);
    stampRight.setVisibility(View.GONE);
    answerLeft.setVisibility(View.VISIBLE);
    answerRight.setVisibility(View.VISIBLE);
    answerLeftIcon.setVisibility(View.VISIBLE);
    answerRightIcon.setVisibility(View.VISIBLE);
    itemView.setVisibility(View.VISIBLE);


    this.score.setText(String.valueOf(card.getScore()));
    this.leaderboard.setText(String.valueOf(card.getgRanking()));

    //ImageLoader.with(itemView.getContext()).load("http://pool.img.aptoide.com/dfl/783ac07187647799c87c4e1d5cde6b8b_icon.png", this.headerIcon);
    headerIcon.setImageResource(R.mipmap.aptoide_quiz_icon);
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

    answerLeftIcon.setOnClickListener(click -> onClickLeft(position));
    answerRightIcon.setOnClickListener(click -> onClickRight(position));
    answerRight.setOnClickListener(click -> onClickLeft(position));
    answerLeft.setOnClickListener(click -> onClickLeft(position));

    headerStats.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(new LeaderboardTouchEvent(card, CardTouchEvent.Type.BODY,
        position)));




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
  public void onPostDismissedLeft(Game3 card, int position){
    card.setAnswerType("Swipe");
    cardTouchEventPublishSubject.onNext(
        new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(answerLeft.getText())));
  }

  public void onPostDismissedRight(Game3 card, int position){
    card.setAnswerType("Swipe");
    cardTouchEventPublishSubject.onNext(
        new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(answerRight.getText())));
  }

  public Game3 getCard() {
    return card;
  }

  public void onClickLeft(int position){
    card.setAnswerType("Click");
    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
        R.anim.slide_out_left);
    animation.setDuration(1000);
    animation.setAnimationListener(new Animation.AnimationListener(){
      @Override public void onAnimationStart(Animation animation) {
        answerLeft.setVisibility(View.INVISIBLE);
        answerRight.setVisibility(View.INVISIBLE);
        answerLeftIcon.setVisibility(View.INVISIBLE);
        answerRightIcon.setVisibility(View.INVISIBLE);
        stampRight.setAlpha(1f);
        stampRight.setVisibility(View.VISIBLE);
      }

      @Override public void onAnimationEnd(Animation animation) {
        itemView.setVisibility(View.INVISIBLE);
        cardTouchEventPublishSubject.onNext(
            new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(answerLeft.getText())));
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });

    this.itemView.startAnimation(animation);
  }

  public void onClickRight(int position){
    card.setAnswerType("Click");
    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
        R.anim.slide_out_right);
    animation.setDuration(1000);
    animation.setAnimationListener(new Animation.AnimationListener(){
      @Override public void onAnimationStart(Animation animation) {
        answerLeft.setVisibility(View.INVISIBLE);
        answerRight.setVisibility(View.INVISIBLE);
        answerLeftIcon.setVisibility(View.INVISIBLE);
        answerRightIcon.setVisibility(View.INVISIBLE);
        stampLeft.setAlpha(1f);
        stampLeft.setVisibility(View.VISIBLE);
      }

      @Override public void onAnimationEnd(Animation animation) {
        itemView.setVisibility(View.INVISIBLE);
        cardTouchEventPublishSubject.onNext(
            new GameCardTouchEvent(card, CardTouchEvent.Type.BODY, position, String.valueOf(answerRight.getText())));
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });

    this.itemView.startAnimation(animation);
  }
}
