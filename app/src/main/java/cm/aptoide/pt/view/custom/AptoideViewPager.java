package cm.aptoide.pt.view.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.NavigationTrackerPagerAdapterHelper;
import cm.aptoide.pt.analytics.AptoideNavigationTracker;

/**
 * Created by neuro on 29-07-2016.
 */
public class AptoideViewPager extends ViewPager {

  private boolean enabled = true;
  private AptoideNavigationTracker aptoideNavigationTracker;

  public AptoideViewPager(Context context) {
    super(context);
  }

  public AptoideViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
    super.addView(child, index, params);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    addOnPageChangeListener(new SimpleOnPageChangeListener() {
      @Override public void onPageSelected(int position) {
        super.onPageSelected(position);
        String currentView =
            ((NavigationTrackerPagerAdapterHelper) getAdapter()).getItemName(position);
        aptoideNavigationTracker.registerView(currentView);
      }
    });
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

  public void setAptoideNavigationTracker(AptoideNavigationTracker aptoideNavigationTracker) {
    this.aptoideNavigationTracker = aptoideNavigationTracker;
  }
}
