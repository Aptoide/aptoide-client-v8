package cm.aptoide.pt.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;
import cm.aptoide.pt.R;

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

  @Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
    super.addView(child, index, params);
    if (child instanceof EditText) {
      if (!TextUtils.isEmpty(helperText)) {
        setHelperText(helperText);
      }
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

  private void setHelperText(CharSequence helperText) {
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

  public int getHelperTextAppearance() {
    return helperTextAppearance;
  }

  public void setHelperTextAppearance(int helperTextAppearance) {
    this.helperTextAppearance = helperTextAppearance;
  }

  public void setHelperTextColor(ColorStateList helperTextColor) {
    this.helperTextColor = helperTextColor;
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
        this.helperView.setTextAppearance(this.getContext(), this.helperTextAppearance);
        if (helperTextColor != null) {
          this.helperView.setTextColor(helperTextColor);
        }
        this.helperView.setText(helperText);
        this.helperView.setVisibility(VISIBLE);
        this.addView(this.helperView);
        if (this.helperView != null) {
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

  public void setHelperTextVisibility(boolean visible) {
    if (!visible) {
      helperView.setVisibility(View.GONE);
    }
  }
}
