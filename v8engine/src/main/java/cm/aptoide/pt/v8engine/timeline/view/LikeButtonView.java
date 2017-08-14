package cm.aptoide.pt.v8engine.timeline.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.custom.CircleView;
import cm.aptoide.pt.v8engine.view.custom.DotsView;

public class LikeButtonView extends FrameLayout {
  private static final DecelerateInterpolator DECELERATE_INTERPOLATOR =
      new DecelerateInterpolator();
  private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR =
      new AccelerateDecelerateInterpolator();
  private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

  private ImageView vHeart;
  private DotsView vDotsView;
  private CircleView vCircle;
  private AnimatorSet animatorSet;
  private boolean iconEnabled;

  public LikeButtonView(Context context) {
    super(context);
    init();
  }

  public LikeButtonView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public LikeButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public LikeButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    LayoutInflater.from(getContext())
        .inflate(R.layout.view_like_button, this, true);
    vHeart = (ImageView) findViewById(R.id.vHeart);
    vDotsView = (DotsView) findViewById(R.id.vDotsView);
    vCircle = (CircleView) findViewById(R.id.vCircle);
    iconEnabled = false;
    setupAnimation();
  }

  public void setHeartState(boolean iconEnabled) {
    if (animatorSet != null && animatorSet.isRunning()) {
      animatorSet.cancel();
    }

    if (iconEnabled) {
      setHeartIconOnWithAnimation();
    } else {
      setHeartOffWithoutAnimation();
    }
    this.iconEnabled = iconEnabled;
  }

  public void setHeartStateWithoutAnimation(boolean iconEnabled) {
    if (animatorSet != null && animatorSet.isRunning()) {
      animatorSet.cancel();
    }

    if (iconEnabled) {
      setHeartIconOnWithoutAnimation();
    } else {
      setHeartOffWithoutAnimation();
    }
    this.iconEnabled = iconEnabled;
  }

  public boolean isIconEnabled() {
    return iconEnabled;
  }

  private void setHeartOffWithoutAnimation() {
    vHeart.setImageResource(R.drawable.heart_off);
    vHeart.invalidate();
  }

  private void setHeartIconOnWithoutAnimation() {
    vHeart.setImageResource(R.drawable.heart_on);
    vHeart.invalidate();
  }

  private void setHeartIconOnWithAnimation() {
    vHeart.setImageResource(R.drawable.heart_on);
    vHeart.animate()
        .cancel();
    vHeart.setScaleX(0);
    vHeart.setScaleY(0);
    vCircle.setInnerCircleRadiusProgress(0);
    vCircle.setOuterCircleRadiusProgress(0);
    vDotsView.setCurrentProgress(0);

    animatorSet.start();
  }

  private void setupAnimation() {
    animatorSet = new AnimatorSet();

    ObjectAnimator outerCircleAnimator =
        ObjectAnimator.ofFloat(vCircle, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
    outerCircleAnimator.setDuration(250);
    outerCircleAnimator.setInterpolator(DECELERATE_INTERPOLATOR);

    ObjectAnimator innerCircleAnimator =
        ObjectAnimator.ofFloat(vCircle, CircleView.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
    innerCircleAnimator.setDuration(200);
    innerCircleAnimator.setStartDelay(200);
    innerCircleAnimator.setInterpolator(DECELERATE_INTERPOLATOR);

    ObjectAnimator starScaleYAnimator = ObjectAnimator.ofFloat(vHeart, ImageView.SCALE_Y, 0.2f, 1f);
    starScaleYAnimator.setDuration(350);
    starScaleYAnimator.setStartDelay(250);
    starScaleYAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

    ObjectAnimator starScaleXAnimator = ObjectAnimator.ofFloat(vHeart, ImageView.SCALE_X, 0.2f, 1f);
    starScaleXAnimator.setDuration(350);
    starScaleXAnimator.setStartDelay(250);
    starScaleXAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

    ObjectAnimator dotsAnimator = ObjectAnimator.ofFloat(vDotsView, DotsView.DOTS_PROGRESS, 0, 1f);
    dotsAnimator.setDuration(900);
    dotsAnimator.setStartDelay(50);
    dotsAnimator.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);

    animatorSet.playTogether(outerCircleAnimator, innerCircleAnimator, starScaleYAnimator,
        starScaleXAnimator, dotsAnimator);

    animatorSet.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationCancel(Animator animation) {
        vCircle.setInnerCircleRadiusProgress(0);
        vCircle.setOuterCircleRadiusProgress(0);
        vDotsView.setCurrentProgress(50);
        vHeart.setScaleX(1);
        vHeart.setScaleY(1);
      }
    });
  }
}
