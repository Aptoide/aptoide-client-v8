package cm.aptoide.pt.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import cm.aptoide.pt.R;
import com.google.android.material.textfield.TextInputLayout;

public class CustomTextInputLayout extends TextInputLayout {

  static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();

  private ColorStateList helperTextColor;
  private CharSequence helperText;
  private TextView helperView;
  private boolean errorEnabled = false;
  private boolean helperTextEnabled = false;
  private int helperTextAppearance;

  public CustomTextInputLayout(Context context) {
    super(context);
  }

  public CustomTextInputLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    final TypedArray a =
        getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextInputLayout, 0, 0);
    try {
      helperTextColor = a.getColorStateList(R.styleable.CustomTextInputLayout_helperTextColor);
      helperText = a.getText(R.styleable.CustomTextInputLayout_helperText);
    } finally {
      a.recycle();
    }
  }

  @Override public void setErrorEnabled(boolean enabled) {
    if (errorEnabled == enabled) {
      return;
    }
    errorEnabled = enabled;
    if (enabled && helperTextEnabled) {
      setHelperTextEnabled(false);
    }

    super.setErrorEnabled(enabled);

    if (!(enabled || TextUtils.isEmpty(helperText))) {
      setHelperText(helperText);
    }
  }

  public void setHelperTextEnabled(boolean enabled) {
    if (helperTextEnabled == enabled) {
      return;
    }
    if (enabled && errorEnabled) {
      setErrorEnabled(false);
    }
    if (this.helperTextEnabled != enabled) {
      if (enabled) {
        this.helperView = new TextView(this.getContext());
        this.helperView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        this.helperView.setTextAppearance(this.getContext(), this.helperTextAppearance);
        if (helperTextColor != null) {
          this.helperView.setTextColor(helperTextColor);
        }
        this.helperView.setText(helperText);
        this.helperView.setVisibility(VISIBLE);
        this.addView(this.helperView);
        if (this.helperView != null && getEditText() != null) {
          ViewCompat.setPaddingRelative(this.helperView, ViewCompat.getPaddingStart(getEditText()),
              0, ViewCompat.getPaddingEnd(getEditText()), getEditText().getPaddingBottom());
        }
      } else {
        this.removeView(this.helperView);
        this.helperView = null;
      }
      this.helperTextEnabled = enabled;
    }
  }

  public void setHelperText(CharSequence helperText) {
    this.helperText = helperText;
    if (!this.helperTextEnabled) {
      if (TextUtils.isEmpty(helperText)) {
        return;
      }
      this.setHelperTextEnabled(true);
    }

    if (!TextUtils.isEmpty(helperText)) {
      this.helperView.setText(helperText);
      this.helperView.setVisibility(View.VISIBLE);
      ViewCompat.setAlpha(this.helperView, 0.0F);
      ViewCompat.animate(this.helperView)
          .alpha(1.0F)
          .setDuration(200L)
          .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
          .setListener(null)
          .start();
    } else if (this.helperView.getVisibility() == VISIBLE) {
      ViewCompat.animate(this.helperView)
          .alpha(0.0F)
          .setDuration(200L)
          .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
          .setListener(new ViewPropertyAnimatorListenerAdapter() {
            public void onAnimationEnd(View view) {
              helperView.setText(null);
              helperView.setVisibility(INVISIBLE);
            }
          })
          .start();
    }
    this.sendAccessibilityEvent(2048);
  }

  public void setHelperTextColor(ColorStateList helperTextColor) {
    this.helperTextColor = helperTextColor;
  }
}
