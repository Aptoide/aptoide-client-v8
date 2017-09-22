package cm.aptoide.pt.view.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by neuro on 29-07-2016.
 */

/**
 * this class extends an old v4 component. avoid its usage.
 */
@Deprecated
public class AptoideViewPager extends ViewPager {

  private boolean enabled = true;

  public AptoideViewPager(Context context) {
    super(context);
  }

  public AptoideViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent event) {
    if (this.enabled) {
      return super.onInterceptTouchEvent(event);
    }

    return false;
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if (this.enabled) {
      return super.onTouchEvent(event);
    }

    return false;
  }

  public void setPagingEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
