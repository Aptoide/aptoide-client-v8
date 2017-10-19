package cm.aptoide.pt.view.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.NavigationTrackerPagerAdapterHelper;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;

/**
 * Created by neuro on 29-07-2016.
 */

/**
 * this class extends an old v4 component. avoid its usage.
 */
@Deprecated public class AptoideViewPager extends ViewPager {

  private boolean enabled = false;
  private boolean trackingEnabled = true;

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
        if (trackingEnabled) {
          if (!(getAdapter() instanceof NavigationTrackerPagerAdapterHelper)) {
            throw new RuntimeException(getAdapter().getClass()
                .getSimpleName()
                + " has to implement "
                + NavigationTrackerPagerAdapterHelper.class.getSimpleName());
          }
          if (position != 0) {
            final NavigationTrackerPagerAdapterHelper adapter =
                (NavigationTrackerPagerAdapterHelper) getAdapter();

            String currentView = adapter.getItemName(position);
            String tag = adapter.getItemTag(position);
            StoreContext storeContext = adapter.getItemStore();

            ((AptoideApplication) getContext().getApplicationContext()).getAptoideNavigationTracker()
                .registerScreen(ScreenTagHistory.Builder.build(currentView, tag, storeContext));

            ((AptoideApplication) getContext().getApplicationContext()).getPageViewsAnalytics()
                .sendPageViewedEvent();
          }
        }
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

  public void setTrackingEnabled(boolean trackingEnabled) {
    this.trackingEnabled = trackingEnabled;
  }
}
