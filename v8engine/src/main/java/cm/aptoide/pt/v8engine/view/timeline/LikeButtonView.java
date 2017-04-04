package cm.aptoide.pt.v8engine.view.timeline;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.custom.CircleView;
import cm.aptoide.pt.v8engine.view.custom.DotsView;

public class LikeButtonView extends FrameLayout implements View.OnClickListener {
  private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR =
      new DecelerateInterpolator();
  private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR =
      new AccelerateDecelerateInterpolator();
  private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

  private ImageView vHeart;
  private DotsView vDotsView;
  private CircleView vCircle;

  private boolean isChecked;
  private AnimatorSet animatorSet;
  private OnClickListener onClickListener;

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
    LayoutInflater.from(getContext()).inflate(R.layout.view_like_button, this, true);
    vHeart = (ImageView) findViewById(R.id.vHeart);
    vDotsView = (DotsView) findViewById(R.id.vDotsView);
    vCircle = (CircleView) findViewById(R.id.vCircle);
    setOnClickListener(this);
  }

  @Override public void setOnClickListener(OnClickListener onClickListener) {
    this.onClickListener = onClickListener;
    if (onClickListener != null) {
      super.setOnClickListener(this);
    } else {
      super.setOnClickListener(null);
    }
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        vHeart.animate()
            .scaleX(0.7f)
            .scaleY(0.7f)
            .setDuration(150)
            .setInterpolator(DECCELERATE_INTERPOLATOR);
        setPressed(true);
        break;

      case MotionEvent.ACTION_MOVE:
        float x = event.getX();
        float y = event.getY();
        boolean isInside = (x > 0 && x < getWidth() && y > 0 && y < getHeight());
        if (isPressed() != isInside) {
          setPressed(isInside);
        }
        break;
      case MotionEvent.ACTION_CANCEL:
        vHeart.animate().scaleX(1).scaleY(1).setInterpolator(DECCELERATE_INTERPOLATOR);
        setPressed(false);
        break;
      case MotionEvent.ACTION_UP:
        vHeart.animate().scaleX(1).scaleY(1).setInterpolator(DECCELERATE_INTERPOLATOR);
        if (isPressed()) {
          performClick();
          setPressed(false);
        }
        break;
    }
    return true;
  }

  @Override public void onClick(View v) {
    if (animatorSet != null) {
      animatorSet.cancel();
    }

    if (!isChecked) {
      if (((V8Engine) getContext().getApplicationContext()).getAccountManager().isLoggedIn()) {
        vHeart.setImageResource(R.drawable.heart_on);
        vHeart.animate().cancel();
        vHeart.setScaleX(0);
        vHeart.setScaleY(0);
        vCircle.setInnerCircleRadiusProgress(0);
        vCircle.setOuterCircleRadiusProgress(0);
        vDotsView.setCurrentProgress(0);

        animatorSet = new AnimatorSet();

        ObjectAnimator outerCircleAnimator =
            ObjectAnimator.ofFloat(vCircle, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
        outerCircleAnimator.setDuration(250);
        outerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator innerCircleAnimator =
            ObjectAnimator.ofFloat(vCircle, CircleView.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
        innerCircleAnimator.setDuration(200);
        innerCircleAnimator.setStartDelay(200);
        innerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator starScaleYAnimator =
            ObjectAnimator.ofFloat(vHeart, ImageView.SCALE_Y, 0.2f, 1f);
        starScaleYAnimator.setDuration(350);
        starScaleYAnimator.setStartDelay(250);
        starScaleYAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator starScaleXAnimator =
            ObjectAnimator.ofFloat(vHeart, ImageView.SCALE_X, 0.2f, 1f);
        starScaleXAnimator.setDuration(350);
        starScaleXAnimator.setStartDelay(250);
        starScaleXAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator dotsAnimator =
            ObjectAnimator.ofFloat(vDotsView, DotsView.DOTS_PROGRESS, 0, 1f);
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

        animatorSet.start();

        isChecked = true;
        onClickListener.onClick(v);
      }
    }
  }

  public void setHeartState(boolean state) {
    if (state) {
      vHeart.setImageResource(R.drawable.heart_on);
      vHeart.invalidate();
      setChecked(true);
    } else {
      vHeart.setImageResource(R.drawable.heart_off);
      vHeart.invalidate();
      setChecked(false);
    }
  }

  private void setChecked(boolean checked) {
    this.isChecked = checked;
  }
}
