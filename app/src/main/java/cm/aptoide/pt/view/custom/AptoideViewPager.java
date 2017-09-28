package cm.aptoide.pt.view.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import cm.aptoide.pt.NavigationTrackerPagerAdapterHelper;
import cm.aptoide.pt.PageViewsAnalytics;
import cm.aptoide.pt.analytics.AptoideNavigationTracker;

/**
 * Created by neuro on 29-07-2016.
 */
public class AptoideViewPager extends ViewPager {

  private boolean enabled = true;
  private AptoideNavigationTracker aptoideNavigationTracker;
  private PageViewsAnalytics pageViewAnalytics;

  public AptoideViewPager(Context context) {
    super(context);
  }

  public AptoideViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    addOnPageChangeListener(new SimpleOnPageChangeListener() {
      @Override public void onPageSelected(int position) {
        super.onPageSelected(position);
        String currentView =
            ((NavigationTrackerPagerAdapterHelper) getAdapter()).getItemName(position);
        aptoideNavigationTracker.registerView(currentView);
        pageViewAnalytics.sendPageViewedEvent();
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

  public void setPageViewAnalytics(PageViewsAnalytics pageViewAnalytics) {
    this.pageViewAnalytics = pageViewAnalytics;
  }

}
